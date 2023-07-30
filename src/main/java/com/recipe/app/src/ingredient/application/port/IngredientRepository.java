package com.recipe.app.src.ingredient.application.port;

import com.recipe.app.src.ingredient.domain.Ingredient;
import com.recipe.app.src.user.domain.User;

import java.util.List;

public interface IngredientRepository {
    List<Ingredient> findByUserIngredientsOrDefaultIngredientsByKeyword(User user, String keyword);

    List<Ingredient> findByUserIngredientsOrDefaultIngredients(User user);

    List<Ingredient> findByDefaultIngredients();
}
