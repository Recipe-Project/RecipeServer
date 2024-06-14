package com.recipe.app.src.recipe.infra;

import com.recipe.app.src.recipe.domain.Recipe;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface RecipeCustomRepository {

    Long countByKeyword(String keyword);

    List<Recipe> findByKeywordLimitOrderByCreatedAtDesc(String keyword, Long lastRecipeId, LocalDateTime createdAt, int size);

    List<Recipe> findByKeywordLimitOrderByRecipeScrapCntDesc(String keyword, Long lastRecipeId, long recipeScrapCnt, int size);

    List<Recipe> findByKeywordLimitOrderByRecipeViewCntDesc(String keyword, Long lastRecipeId, long recipeViewCnt, int size);

    List<Recipe> findUserScrapRecipesLimit(Long userId, Long lastRecipeId, LocalDateTime scrapCreatedAt, int size);

    List<Recipe> findLimitByUserId(Long userId, Long lastRecipeId, int size);

    List<Recipe> findRecipesInFridge(Collection<String> ingredientNames);
}
