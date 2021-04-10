package com.recipe.app.src.recipeIngredient;

import com.recipe.app.src.recipeInfo.models.RecipeInfo;
import com.recipe.app.src.recipeIngredient.models.RecipeIngredient;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface RecipeIngredientRepository extends CrudRepository<RecipeIngredient, Integer> {

    List<RecipeIngredient> findByRecipeInfoAndStatus(RecipeInfo recipeInfo, String active);
}
