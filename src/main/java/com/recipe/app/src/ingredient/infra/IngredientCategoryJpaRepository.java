package com.recipe.app.src.ingredient.infra;

import com.recipe.app.src.ingredient.domain.IngredientCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IngredientCategoryJpaRepository extends JpaRepository<IngredientCategory, Integer> {
    List<IngredientCategory> findByStatus(String status);
}
