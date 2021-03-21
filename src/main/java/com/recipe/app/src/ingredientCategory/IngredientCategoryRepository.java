package com.recipe.app.src.ingredientCategory;

import com.recipe.app.src.ingredientCategory.models.IngredientCategory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface IngredientCategoryRepository extends CrudRepository<IngredientCategory, Integer> {
    List<IngredientCategory> findByStatus(String status);
}
