package com.recipe.app.src.ingredient.infra;

import com.recipe.app.src.ingredient.domain.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IngredientJpaRepository extends JpaRepository<Ingredient, Integer> {
    List<Ingredient> findByNameContainingAndStatus(String name, String status);

    Optional<Ingredient> findByNameAndStatus(String name, String status);

    List<Ingredient> findByStatus(String status);

    List<Ingredient> findAllByIngredientIdxIn(List<Integer> idxList);
}
