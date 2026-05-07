package com.recipe.app.src.recipe.infra.blog;

import com.recipe.app.src.common.utils.SearchKeywordNormalizer.SearchQuery;
import com.recipe.app.src.recipe.domain.blog.BlogRecipe;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface BlogRecipeCustomRepository {

    Long countByKeyword(SearchQuery query);

    List<BlogRecipe> findByKeywordLimit(String keyword, int size);

    List<BlogRecipe> findByKeywordLimitOrderByPublishedAtDesc(SearchQuery query, Long lastBlogRecipeId, LocalDate lastBlogRecipePublishedAt, int size);

    List<BlogRecipe> findByKeywordLimitOrderByBlogScrapCntDesc(SearchQuery query, Long lastBlogRecipeId, long lastBlogScrapCnt, int size);

    List<BlogRecipe> findByKeywordLimitOrderByBlogViewCntDesc(SearchQuery query, Long lastBlogRecipeId, long lastBlogViewCnt, int size);

    List<BlogRecipe> findUserScrapBlogRecipesLimit(Long userId, Long lastBlogRecipeId, LocalDateTime scrapCreatedAt, int size);
}
