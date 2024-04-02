package com.recipe.app.src.ingredient.infra;

import com.recipe.app.src.ingredient.domain.IngredientCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientCategoryRepository extends JpaRepository<IngredientCategory, Long> {
}
