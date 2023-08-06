package com.recipe.app.src.ingredient.application.port;

import com.recipe.app.src.ingredient.infra.IngredientCategoryEntity;

import java.util.List;
import java.util.Optional;

public interface IngredientCategoryRepository {
    List<IngredientCategoryEntity> findAll(String status);

    Optional<IngredientCategoryEntity> findById(Integer ingredientCategoryIdx);
}
