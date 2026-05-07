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

    List<Recipe> findByKeywordLimitOrderByCreatedAtDesc(SearchQuery query, Long lastRecipeId, LocalDateTime createdAt, int size);

    List<Recipe> findByKeywordLimitOrderByRecipeScrapCntDesc(SearchQuery query, Long lastRecipeId, long recipeScrapCnt, int size);

    List<Recipe> findByKeywordLimitOrderByRecipeViewCntDesc(SearchQuery query, Long lastRecipeId, long recipeViewCnt, int size);

    List<Recipe> findUserScrapRecipesLimit(Long userId, Long lastRecipeId, LocalDateTime scrapCreatedAt, int size);

    List<Recipe> findLimitByUserId(Long userId, Long lastRecipeId, int size);

    List<Recipe> findRecipesInFridge(Collection<String> ingredientNames);
}
