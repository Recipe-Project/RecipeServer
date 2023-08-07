package com.recipe.app.src.ingredient.application.port;

import com.recipe.app.src.ingredient.domain.Ingredient;
import com.recipe.app.src.ingredient.domain.IngredientCategory;
import com.recipe.app.src.user.domain.User;

import java.util.List;
import java.util.Optional;

public interface IngredientRepository {
    List<Ingredient> findDefaultIngredientsByIngredientNameContaining(String keyword);

    List<Ingredient> findDefaultIngredients();

    List<Ingredient> findByIngredientIdIn(List<Long> ingredientIds);

    Optional<Ingredient> findByUserAndIngredientNameAndIngredientIconUrlAndIngredientCategory(User user, String ingredientName, String ingredientIconUrl, IngredientCategory ingredientCategory);

    Ingredient save(Ingredient ingredient);
}
