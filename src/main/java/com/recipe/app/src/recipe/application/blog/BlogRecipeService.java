package com.recipe.app.src.recipe.application.blog;

import com.recipe.app.src.common.utils.BadWordFiltering;
import com.recipe.app.src.common.utils.SearchKeywordNormalizer;
import com.recipe.app.src.common.utils.SearchKeywordNormalizer.SearchQuery;
import com.recipe.app.src.recipe.application.dto.RecipesResponse;
import com.recipe.app.src.recipe.domain.blog.BlogRecipe;
import com.recipe.app.src.recipe.domain.blog.BlogRecipes;
import com.recipe.app.src.recipe.domain.blog.BlogScrap;
import com.recipe.app.src.recipe.infra.blog.BlogRecipeRepository;
import com.recipe.app.src.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BlogRecipeService {

    private final BlogRecipeRepository blogRecipeRepository;
    private final BlogScrapService blogScrapService;
    private final BlogViewService blogViewService;
    private final BadWordFiltering badWordFiltering;
    private final BlogRecipeClientSearchService blogRecipeClientSearchService;


    private static final int MIN_RECIPE_CNT = 10;

    public BlogRecipeService(BlogRecipeRepository blogRecipeRepository, BlogScrapService blogScrapService, BlogViewService blogViewService,
                             BadWordFiltering badWordFiltering, BlogRecipeClientSearchService blogRecipeClientSearchService) {
        this.blogRecipeRepository = blogRecipeRepository;
        this.blogScrapService = blogScrapService;
        this.blogViewService = blogViewService;
        this.badWordFiltering = badWordFiltering;
        this.blogRecipeClientSearchService = blogRecipeClientSearchService;
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
            blogRecipeClientSearchService.searchNaverBlogRecipes(keyword);
        }

        List<BlogRecipe> blogRecipes = findByKeywordOrderBy(query, lastBlogRecipeId, size, sort);
        totalCnt = blogRecipeRepository.countByKeyword(query);

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

        long lastBlogScrapCnt = blogScrapService.countByBlogRecipeId(lastBlogRecipeId);

        return blogRecipeRepository.findByKeywordLimitOrderByBlogScrapCntDesc(query, lastBlogRecipeId, lastBlogScrapCnt, size);
    }

    private List<BlogRecipe> findByKeywordOrderByBlogViewCnt(SearchQuery query, long lastBlogRecipeId, int size) {

        long lastBlogViewCnt = blogViewService.countByBlogRecipeId(lastBlogRecipeId);

        return blogRecipeRepository.findByKeywordLimitOrderByBlogViewCntDesc(query, lastBlogRecipeId, lastBlogViewCnt, size);
    }

    private List<BlogRecipe> findByKeywordOrderByPublishedAt(SearchQuery query, long lastBlogRecipeId, int size) {

        BlogRecipe blogRecipe = blogRecipeRepository.findById(lastBlogRecipeId).orElse(null);

        return blogRecipeRepository.findByKeywordLimitOrderByPublishedAtDesc(query, lastBlogRecipeId, blogRecipe == null ? null : blogRecipe.getPublishedAt(), size);
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
