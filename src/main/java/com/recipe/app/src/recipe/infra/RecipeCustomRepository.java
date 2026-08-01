package com.recipe.app.src.recipe.infra;

import com.recipe.app.src.common.utils.SearchKeywordNormalizer.SearchQuery;
import com.recipe.app.src.recipe.domain.Recipe;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RecipeCustomRepository {

    Optional<Recipe> findRecipeDetail(Long recipeId, Long userId);

    Long countByKeyword(SearchQuery query);

    // 아래 정렬들은 모두 "검색어 일치율(relevance) 대분류 → 각 정렬키 소분류 → recipeId" 순.
    // lastRelevance 는 커서(직전 페이지 마지막 레시피)의 현재 검색어 기준 점수. findRelevanceScoreByRecipeId 로 구해 넘긴다.
    Double findRelevanceScoreByRecipeId(SearchQuery query, Long recipeId);

    List<Recipe> findByKeywordLimitOrderByCreatedAtDesc(SearchQuery query, Long lastRecipeId, Double lastRelevance, LocalDateTime createdAt, int size);

    List<Recipe> findByKeywordLimitOrderByRecipeScrapCntDesc(SearchQuery query, Long lastRecipeId, Double lastRelevance, long recipeScrapCnt, int size);

    List<Recipe> findByKeywordLimitOrderByRecipeViewCntDesc(SearchQuery query, Long lastRecipeId, Double lastRelevance, long recipeViewCnt, int size);

    List<Recipe> findUserScrapRecipesLimit(Long userId, Long lastRecipeId, LocalDateTime scrapCreatedAt, int size);

    List<Recipe> findLimitByUserId(Long userId, Long lastRecipeId, int size);

    List<Recipe> findRecipesInFridge(Collection<String> ingredientNames);
}
