package com.recipe.app.src.ingredient.infra;

import com.recipe.app.src.ingredient.domain.Ingredient;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface IngredientRepository extends CrudRepository<Ingredient, Integer> {
    List<Ingredient> findByNameContainingAndStatus(String name, String status);

    Optional<Ingredient> findByNameAndStatus(String name, String status);

    List<Ingredient> findByStatus(String status);

    List<Ingredient> findAllByIngredientIdxIn(List<Integer> idxList);
}
