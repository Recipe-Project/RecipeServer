package com.recipe.app.src.recipe.application.blog;

import com.recipe.app.src.common.utils.BadWordFiltering;
import com.recipe.app.src.recipe.application.keyword.SearchKeywordService;
import com.recipe.app.src.common.utils.SearchKeywordNormalizer;
import com.recipe.app.src.common.utils.SearchKeywordNormalizer.SearchQuery;
import com.recipe.app.src.recipe.application.dto.RecipesResponse;
import com.recipe.app.src.recipe.domain.blog.BlogRecipe;
import com.recipe.app.src.recipe.domain.blog.BlogRecipes;
import com.recipe.app.src.recipe.domain.blog.BlogScrap;
import com.recipe.app.src.recipe.infra.blog.BlogRecipeRepository;
import com.recipe.app.src.user.domain.User;
import com.google.common.util.concurrent.Striped;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.locks.Lock;

@Service
public class BlogRecipeService {

    private final BlogRecipeRepository blogRecipeRepository;
    private final BlogScrapService blogScrapService;
    private final BlogViewService blogViewService;
    private final BadWordFiltering badWordFiltering;
    private final BlogRecipeClientSearchService blogRecipeClientSearchService;
    private final SearchKeywordService searchKeywordService;


    private static final int MIN_RECIPE_CNT = 10;

    // 같은 키워드를 여러 요청이 동시에 처음 검색할 때, 각자 findByBlogUrlIn(빈 결과) → 각자 saveAll 로
    // 같은 blogUrl 이 중복 저장되는 레이스를 막는다. 단일 인스턴스(EC2 1대) 전제의 JVM 내부 락.
    // 고정 64개 stripe 라 키워드가 늘어도 락 객체가 무한정 쌓이지 않는다.
    private final Striped<Lock> keywordLocks = Striped.lock(64);

    public BlogRecipeService(BlogRecipeRepository blogRecipeRepository, BlogScrapService blogScrapService, BlogViewService blogViewService,
                             BadWordFiltering badWordFiltering, BlogRecipeClientSearchService blogRecipeClientSearchService,
                             SearchKeywordService searchKeywordService) {
        this.blogRecipeRepository = blogRecipeRepository;
        this.blogScrapService = blogScrapService;
        this.blogViewService = blogViewService;
        this.badWordFiltering = badWordFiltering;
        this.blogRecipeClientSearchService = blogRecipeClientSearchService;
        this.searchKeywordService = searchKeywordService;
    }

    @Transactional
    public RecipesResponse findBlogRecipesByKeyword(User user, String keyword, long lastBlogRecipeId, int size, String sort) {

        badWordFiltering.check(keyword);

        SearchQuery query = SearchKeywordNormalizer.normalize(keyword);
        if (query instanceof SearchQuery.Empty) {
            return getRecipes(user, 0L, new BlogRecipes(List.of()));
        }

        long totalCnt = blogRecipeRepository.countByKeyword(query);

        if (totalCnt < MIN_RECIPE_CNT) {
            // 락을 잡은 한 요청만 네이버에서 채우고, 그 트랜잭션(REQUIRES_NEW)이 커밋된 뒤 락을 푼다.
            // 뒤이어 락을 잡는 요청은 findByBlogUrlIn 에서 앞 요청이 커밋한 행을 보고 걸러내므로 중복이 안 쌓인다.
            Lock lock = keywordLocks.get(keyword);
            lock.lock();
            try {
                blogRecipeClientSearchService.searchNaverBlogRecipes(keyword);
            } finally {
                lock.unlock();
            }
        }

        List<BlogRecipe> blogRecipes = findByKeywordOrderBy(query, lastBlogRecipeId, size, sort);
        totalCnt = blogRecipeRepository.countByKeyword(query);

        // 검색 로그 적재 (비동기·fire-and-forget). 욕설/빈 검색어는 위에서 이미 걸러진 상태.
        searchKeywordService.record(keyword, user != null ? user.getUserId() : null);

        return getRecipes(user, totalCnt, new BlogRecipes(blogRecipes));
    }

    private List<BlogRecipe> findByKeywordOrderBy(SearchQuery query, long lastBlogRecipeId, int size, String sort) {

        if (sort.equals("scraps")) {
            return findByKeywordOrderByBlogScrapCnt(query, lastBlogRecipeId, size);
        } else if (sort.equals("views")) {
            return findByKeywordOrderByBlogViewCnt(query, lastBlogRecipeId, size);
        } else {
            return findByKeywordOrderByPublishedAt(query, lastBlogRecipeId, size);
        }
    }

    private List<BlogRecipe> findByKeywordOrderByBlogScrapCnt(SearchQuery query, long lastBlogRecipeId, int size) {

        Double lastRelevance = blogRecipeRepository.findRelevanceScoreByBlogRecipeId(query, lastBlogRecipeId);
        long lastBlogScrapCnt = blogScrapService.countByBlogRecipeId(lastBlogRecipeId);

        return blogRecipeRepository.findByKeywordLimitOrderByBlogScrapCntDesc(query, lastBlogRecipeId, lastRelevance, lastBlogScrapCnt, size);
    }

    private List<BlogRecipe> findByKeywordOrderByBlogViewCnt(SearchQuery query, long lastBlogRecipeId, int size) {

        Double lastRelevance = blogRecipeRepository.findRelevanceScoreByBlogRecipeId(query, lastBlogRecipeId);
        long lastBlogViewCnt = blogViewService.countByBlogRecipeId(lastBlogRecipeId);

        return blogRecipeRepository.findByKeywordLimitOrderByBlogViewCntDesc(query, lastBlogRecipeId, lastRelevance, lastBlogViewCnt, size);
    }

    private List<BlogRecipe> findByKeywordOrderByPublishedAt(SearchQuery query, long lastBlogRecipeId, int size) {

        Double lastRelevance = blogRecipeRepository.findRelevanceScoreByBlogRecipeId(query, lastBlogRecipeId);
        BlogRecipe blogRecipe = blogRecipeRepository.findById(lastBlogRecipeId).orElse(null);

        return blogRecipeRepository.findByKeywordLimitOrderByPublishedAtDesc(query, lastBlogRecipeId, lastRelevance, blogRecipe == null ? null : blogRecipe.getPublishedAt(), size);
    }

    @Transactional(readOnly = true)
    public RecipesResponse findScrapBlogRecipes(User user, long lastBlogRecipeId, int size) {

        long totalCnt = blogScrapService.countByUserId(user.getUserId());

        BlogScrap blogScrap = blogScrapService.findByUserIdAndBlogRecipeId(user.getUserId(), lastBlogRecipeId);

        List<BlogRecipe> blogRecipes = blogRecipeRepository.findUserScrapBlogRecipesLimit(user.getUserId(), lastBlogRecipeId, blogScrap != null ? blogScrap.getCreatedAt() : null, size);

        return getRecipes(user, totalCnt, new BlogRecipes(blogRecipes));
    }

    private RecipesResponse getRecipes(User user, long totalCnt, BlogRecipes blogRecipes) {

        List<BlogScrap> blogScraps = blogScrapService.findByBlogRecipeIds(blogRecipes.getBlogRecipeIds());

        return RecipesResponse.from(totalCnt, blogRecipes, blogScraps, user);
    }

    @Transactional
    public void createBlogScrap(User user, long blogRecipeId) {

        blogRecipeRepository.findById(blogRecipeId)
                .ifPresent((blogRecipe) -> {
                    blogRecipe.plusScrapCnt();
                    blogScrapService.create(user.getUserId(), blogRecipeId);
                });
    }

    @Transactional
    public void deleteBlogScrap(User user, long blogRecipeId) {

        blogRecipeRepository.findById(blogRecipeId)
                .ifPresent((blogRecipe) -> {
                    blogRecipe.minusScrapCnt();
                    blogScrapService.delete(user.getUserId(), blogRecipeId);
                });
    }

    @Transactional
    public void createBlogView(User user, long blogRecipeId) {

        blogRecipeRepository.findById(blogRecipeId)
                .ifPresent((blogRecipe) -> {
                    blogRecipe.plusViewCnt();
                    blogViewService.create(user.getUserId(), blogRecipeId);
                });
    }
}
