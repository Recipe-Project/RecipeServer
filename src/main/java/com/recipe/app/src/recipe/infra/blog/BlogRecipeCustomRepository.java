package com.recipe.app.src.recipe.infra.blog;

import com.recipe.app.src.common.utils.SearchKeywordNormalizer.SearchQuery;
import com.recipe.app.src.recipe.domain.blog.BlogRecipe;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface BlogRecipeCustomRepository {

    Long countByKeyword(SearchQuery query);

    List<BlogRecipe> findByKeywordLimit(String keyword, int size);

    // 아래 정렬들은 모두 "검색어 일치율(relevance) 대분류 → 각 정렬키 소분류 → blogRecipeId" 순.
    Double findRelevanceScoreByBlogRecipeId(SearchQuery query, Long blogRecipeId);

    List<BlogRecipe> findByKeywordLimitOrderByPublishedAtDesc(SearchQuery query, Long lastBlogRecipeId, Double lastRelevance, LocalDate lastBlogRecipePublishedAt, int size);

    List<BlogRecipe> findByKeywordLimitOrderByBlogScrapCntDesc(SearchQuery query, Long lastBlogRecipeId, Double lastRelevance, long lastBlogScrapCnt, int size);

    List<BlogRecipe> findByKeywordLimitOrderByBlogViewCntDesc(SearchQuery query, Long lastBlogRecipeId, Double lastRelevance, long lastBlogViewCnt, int size);

    List<BlogRecipe> findUserScrapBlogRecipesLimit(Long userId, Long lastBlogRecipeId, LocalDateTime scrapCreatedAt, int size);
}
