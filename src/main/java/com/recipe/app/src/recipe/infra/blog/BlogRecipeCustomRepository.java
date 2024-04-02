package com.recipe.app.src.recipe.infra.blog;

import com.recipe.app.src.recipe.domain.blog.BlogRecipe;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface BlogRecipeCustomRepository {

    Long countByKeyword(String keyword);

    List<BlogRecipe> findByKeywordLimitOrderByPublishedAtDesc(String keyword, Long lastBlogRecipeId, LocalDate lastBlogRecipePublishedAt, int size);

    List<BlogRecipe> findByKeywordLimitOrderByBlogScrapCntDesc(String keyword, Long lastBlogRecipeId, long lastBlogScrapCnt, int size);

    List<BlogRecipe> findByKeywordLimitOrderByBlogViewCntDesc(String keyword, Long lastBlogRecipeId, long lastBlogViewCnt, int size);

    List<BlogRecipe> findUserScrapBlogRecipesLimit(Long userId, Long lastBlogRecipeId, LocalDateTime scrapCreatedAt, int size);
}
