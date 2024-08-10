package com.recipe.app.src.recipe.application.blog;

import com.recipe.app.src.common.utils.BadWordFiltering;
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

    public RecipesResponse findBlogRecipesByKeyword(User user, String keyword, long lastBlogRecipeId, int size, String sort) {

        badWordFiltering.check(keyword);

        long totalCnt = blogRecipeRepository.countByKeyword(keyword);

        List<BlogRecipe> blogRecipes;
        if (totalCnt < MIN_RECIPE_CNT) {

            blogRecipes = blogRecipeClientSearchService.searchNaverBlogRecipes(keyword, size);

            totalCnt = blogRecipeRepository.countByKeyword(keyword);
        } else {
            blogRecipes = findByKeywordOrderBy(keyword, lastBlogRecipeId, size, sort);
        }

        return getRecipes(user, totalCnt, new BlogRecipes(blogRecipes));
    }

    @Transactional(readOnly = true)
    public List<BlogRecipe> findByKeywordOrderBy(String keyword, long lastBlogRecipeId, int size, String sort) {

        if (sort.equals("blogScraps")) {
            return findByKeywordOrderByBlogScrapCnt(keyword, lastBlogRecipeId, size);
        } else if (sort.equals("blogViews")) {
            return findByKeywordOrderByBlogViewCnt(keyword, lastBlogRecipeId, size);
        } else {
            return findByKeywordOrderByPublishedAt(keyword, lastBlogRecipeId, size);
        }
    }

    private List<BlogRecipe> findByKeywordOrderByBlogScrapCnt(String keyword, long lastBlogRecipeId, int size) {

        long lastBlogScrapCnt = blogScrapService.countByBlogRecipeId(lastBlogRecipeId);

        return blogRecipeRepository.findByKeywordLimitOrderByBlogScrapCntDesc(keyword, lastBlogRecipeId, lastBlogScrapCnt, size);
    }

    private List<BlogRecipe> findByKeywordOrderByBlogViewCnt(String keyword, long lastBlogRecipeId, int size) {

        long lastBlogViewCnt = blogViewService.countByBlogRecipeId(lastBlogRecipeId);

        return blogRecipeRepository.findByKeywordLimitOrderByBlogViewCntDesc(keyword, lastBlogRecipeId, lastBlogViewCnt, size);
    }

    private List<BlogRecipe> findByKeywordOrderByPublishedAt(String keyword, long lastBlogRecipeId, int size) {

        BlogRecipe blogRecipe = blogRecipeRepository.findById(lastBlogRecipeId).orElse(null);

        return blogRecipeRepository.findByKeywordLimitOrderByPublishedAtDesc(keyword, lastBlogRecipeId, blogRecipe == null ? null : blogRecipe.getPublishedAt(), size);
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
