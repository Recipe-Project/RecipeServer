package com.recipe.app.src.ingredient.infra;

import com.recipe.app.src.ingredient.domain.Ingredient;

import java.util.List;

public interface IngredientCustomRepository {

    List<Ingredient> findDefaultIngredientsByKeyword(String keyword);

    List<Ingredient> findDefaultIngredients();
}
