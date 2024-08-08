package com.recipe.app.src.ingredient.infra;

import com.recipe.app.src.ingredient.domain.Ingredient;

import java.util.List;

public interface IngredientCustomRepository {

    List<Ingredient> findDefaultIngredientsByKeyword(Long userId, String keyword);
}
