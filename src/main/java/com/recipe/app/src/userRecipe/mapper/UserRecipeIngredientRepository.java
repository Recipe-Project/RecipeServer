package com.recipe.app.src.userRecipe.mapper;

import com.recipe.app.src.userRecipe.domain.UserRecipeIngredient;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRecipeIngredientRepository extends CrudRepository<UserRecipeIngredient, Integer> {
    List<UserRecipeIngredient> findByUserRecipeIdxAndStatus(Integer userRecipeIdx, String status);
}
