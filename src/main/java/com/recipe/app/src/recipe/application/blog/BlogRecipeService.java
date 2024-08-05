package com.recipe.app.src.recipe.application.blog;

import com.recipe.app.src.common.application.BadWordService;
import com.recipe.app.src.recipe.application.dto.RecipesResponse;
import com.recipe.app.src.recipe.domain.blog.BlogRecipe;
import com.recipe.app.src.recipe.domain.blog.BlogRecipes;
import com.recipe.app.src.recipe.domain.blog.BlogScrap;
import com.recipe.app.src.recipe.exception.NotFoundRecipeException;
import com.recipe.app.src.recipe.infra.blog.BlogRecipeRepository;
import com.recipe.app.src.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlogRecipeService {

    private final BlogRecipeRepository blogRecipeRepository;
    private final BlogScrapService blogScrapService;
    private final BlogViewService blogViewService;
    private final BadWordService badWordService;
    private final BlogRecipeClientSearchService blogRecipeClientSearchService;


    private static final int MIN_RECIPE_CNT = 10;

    public BlogRecipeService(BlogRecipeRepository blogRecipeRepository, BlogScrapService blogScrapService, BlogViewService blogViewService,
                             BadWordService badWordService, BlogRecipeClientSearchService blogRecipeClientSearchService) {
        this.blogRecipeRepository = blogRecipeRepository;
        this.blogScrapService = blogScrapService;
        this.blogViewService = blogViewService;
        this.badWordService = badWordService;
        this.blogRecipeClientSearchService = blogRecipeClientSearchService;
    }

    public RecipesResponse getBlogRecipes(User user, String keyword, Long lastBlogRecipeId, int size, String sort) throws UnsupportedEncodingException {

        badWordService.checkBadWords(keyword);

        long totalCnt = blogRecipeRepository.countByKeyword(keyword);

        List<BlogRecipe> blogRecipes;
        if (totalCnt < MIN_RECIPE_CNT) {
            blogRecipes = blogRecipeClientSearchService.searchNaverBlogRecipes(user, keyword, size);
            totalCnt = blogRecipeRepository.countByKeyword(keyword);
        } else {
            blogRecipes = findByKeywordSortBy(keyword, lastBlogRecipeId, size, sort);
        }

        return getRecipes(user, totalCnt, blogRecipes);
    }

    @Transactional(readOnly = true)
    public List<BlogRecipe> findByKeywordSortBy(String keyword, Long lastBlogRecipeId, int size, String sort) {

        if (sort.equals("blogScraps")) {
            long lastBlogScrapCnt = blogScrapService.countByBlogRecipeId(lastBlogRecipeId);
            return blogRecipeRepository.findByKeywordLimitOrderByBlogScrapCntDesc(keyword, lastBlogRecipeId, lastBlogScrapCnt, size);
        } else if (sort.equals("blogViews")) {
            long lastBlogViewCnt = blogViewService.countByBlogRecipeId(lastBlogRecipeId);
            return blogRecipeRepository.findByKeywordLimitOrderByBlogViewCntDesc(keyword, lastBlogRecipeId, lastBlogViewCnt, size);
        } else {
            BlogRecipe blogRecipe = null;
            if (lastBlogRecipeId != null && lastBlogRecipeId > 0) {
                blogRecipe = blogRecipeRepository.findById(lastBlogRecipeId).orElseThrow(()
                        -> {
                    throw new NotFoundRecipeException();
                });
            }
            return blogRecipeRepository.findByKeywordLimitOrderByPublishedAtDesc(keyword, lastBlogRecipeId, blogRecipe == null ? null : blogRecipe.getPublishedAt(), size);
        }
    }

    @Transactional(readOnly = true)
    public RecipesResponse getScrapBlogRecipes(User user, Long lastBlogRecipeId, int size) {

        long totalCnt = blogScrapService.countBlogScrapByUser(user);
        BlogScrap blogScrap = null;
        if (lastBlogRecipeId != null && lastBlogRecipeId > 0) {
            blogScrap = blogScrapService.findByUserIdAndBlogRecipeId(user.getUserId(), lastBlogRecipeId);
        }
        List<BlogRecipe> blogRecipes = blogRecipeRepository.findUserScrapBlogRecipesLimit(user.getUserId(), lastBlogRecipeId, blogScrap != null ? blogScrap.getCreatedAt() : null, size);

        return getRecipes(user, totalCnt, blogRecipes);
    }

    private RecipesResponse getRecipes(User user, long totalCnt, List<BlogRecipe> blogRecipes) {

        List<Long> blogRecipeIds = blogRecipes.stream()
                .map(BlogRecipe::getBlogRecipeId)
                .collect(Collectors.toList());
        List<BlogScrap> blogScraps = blogScrapService.findByBlogRecipeIds(blogRecipeIds);

        return RecipesResponse.from(totalCnt, new BlogRecipes(blogRecipes), blogScraps, user);
    }

    @Transactional
    public void createBlogScrap(User user, Long blogRecipeId) {

        BlogRecipe blogRecipe = blogRecipeRepository.findById(blogRecipeId).orElseThrow(() -> {
            throw new NotFoundRecipeException();
        });
        blogRecipe.plusScrapCnt();
        blogRecipeRepository.save(blogRecipe);
        blogScrapService.createBlogScrap(user, blogRecipeId);
    }

    @Transactional
    public void deleteBlogScrap(User user, Long blogRecipeId) {

        BlogRecipe blogRecipe = blogRecipeRepository.findById(blogRecipeId).orElseThrow(() -> {
            throw new NotFoundRecipeException();
        });
        blogRecipe.minusScrapCnt();
        blogRecipeRepository.save(blogRecipe);
        blogScrapService.deleteBlogScrap(user, blogRecipeId);
    }

    @Transactional
    public void createBlogView(User user, Long blogRecipeId) {

        BlogRecipe blogRecipe = blogRecipeRepository.findById(blogRecipeId).orElseThrow(() -> {
            throw new NotFoundRecipeException();
        });
        blogRecipe.plusViewCnt();
        blogRecipeRepository.save(blogRecipe);
        blogViewService.createBlogView(user, blogRecipeId);
    }
}
