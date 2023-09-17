package com.recipe.app.src.ingredient.application.port;

import com.recipe.app.src.ingredient.domain.IngredientCategory;

import java.util.Optional;

public interface IngredientCategoryRepository {
    Optional<IngredientCategory> findById(Long ingredientCategoryId);
}
