package com.recipe.app.src.ingredient.infra;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IngredientJpaRepository extends JpaRepository<IngredientEntity, Integer> {
    List<IngredientEntity> findByNameContainingAndStatus(String name, String status);

    Optional<IngredientEntity> findByNameAndStatus(String name, String status);

    List<IngredientEntity> findByStatus(String status);

    List<IngredientEntity> findAllByIngredientIdxIn(List<Integer> idxList);
}
