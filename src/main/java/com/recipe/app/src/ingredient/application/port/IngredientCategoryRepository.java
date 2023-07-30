package com.recipe.app.src.ingredient.application.port;

import com.recipe.app.src.ingredient.domain.IngredientCategory;

import java.util.List;
import java.util.Optional;

public interface IngredientCategoryRepository {
    List<IngredientCategory> findByStatus(String status);

    Optional<IngredientCategory> findById(Integer ingredientCategoryIdx);
}
