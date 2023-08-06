package com.recipe.app.src.ingredient.application.port;

import com.recipe.app.src.ingredient.domain.Ingredient;

import java.util.List;

public interface IngredientRepository {
    List<Ingredient> findDefaultIngredientsByIngredientNameContaining(String keyword);

    List<Ingredient> findDefaultIngredients();

    List<Ingredient> findByIngredientIdIn(List<Long> ingredientIds);
}
