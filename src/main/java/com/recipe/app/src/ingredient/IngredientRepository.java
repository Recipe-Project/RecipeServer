package com.recipe.app.src.ingredient;

import com.recipe.app.src.ingredient.models.Ingredient;
import com.recipe.app.src.ingredientCategory.models.IngredientCategory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface IngredientRepository extends CrudRepository<Ingredient, Integer> {
    List<Ingredient> findByIngredientCategoryAndStatus(IngredientCategory ingredientCategory, String status);
    List<Ingredient> findByNameContainingAndIngredientCategoryAndStatus(String name,IngredientCategory ingredientCategory,String status);
    Ingredient findByNameAndStatus(String name, String status);
    List<Ingredient> findByStatus(String status);
}
