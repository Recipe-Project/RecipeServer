package com.recipe.app.src.ingredient.infra;

import com.recipe.app.src.ingredient.domain.IngredientCategory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface IngredientCategoryRepository extends CrudRepository<IngredientCategory, Integer> {
    List<IngredientCategory> findByStatus(String status);
}
