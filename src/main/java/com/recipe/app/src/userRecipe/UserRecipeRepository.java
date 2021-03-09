package com.recipe.app.src.userRecipe;

import com.recipe.app.src.userRecipe.models.UserRecipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRecipeRepository extends CrudRepository<UserRecipe, Integer> {
    UserRecipe findByUserIdxAndUserRecipeIdxAndStatus(Integer userIdx,Integer userRecipeIdx, String status);
    Page<UserRecipe> findByUserIdxAndStatus(Integer userIdx, String status, Pageable pageable);
}
