package com.recipe.app.src.ingredient.application.port;

import com.recipe.app.src.ingredient.domain.Ingredient;

import java.util.List;
import java.util.Optional;

public interface IngredientRepository {
    List<Ingredient> findByNameContainingAndStatus(String name, String status);

    Optional<Ingredient> findByNameAndStatus(String name, String status);

    List<Ingredient> findByStatus(String status);

    List<Ingredient> findAllByIngredientIdxIn(List<Integer> idxList);
}
