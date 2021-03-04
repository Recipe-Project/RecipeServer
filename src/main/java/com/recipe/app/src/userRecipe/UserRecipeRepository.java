package com.recipe.app.src.userRecipe;

import com.recipe.app.src.user.models.User;
import com.recipe.app.src.userRecipe.models.UserRecipe;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRecipeRepository extends CrudRepository<UserRecipe, Integer> {
    UserRecipe findByUserIdxAndUserRecipeIdxAndStatus(Integer userIdx,Integer userRecipeIdx, String status);
    List<UserRecipe> findByUserIdxAndStatus(Integer userIdx, String status);
}
