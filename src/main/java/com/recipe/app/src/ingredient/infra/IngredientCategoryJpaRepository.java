package com.recipe.app.src.ingredient.infra;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IngredientCategoryJpaRepository extends JpaRepository<IngredientCategoryEntity, Integer> {
    List<IngredientCategoryEntity> findByStatus(String status);
}
