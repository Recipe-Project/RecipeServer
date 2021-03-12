package com.recipe.app.src.recipeIngredient;

import com.recipe.app.src.recipeIngredient.models.RecipeIngredient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface RecipeIngredientRepository extends CrudRepository<RecipeIngredient, Integer> {
}
