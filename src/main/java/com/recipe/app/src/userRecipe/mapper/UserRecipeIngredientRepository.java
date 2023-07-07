package com.recipe.app.src.userRecipe.mapper;

import com.recipe.app.src.userRecipe.domain.UserRecipeIngredient;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRecipeIngredientRepository extends CrudRepository<UserRecipeIngredient, Integer> {
}
