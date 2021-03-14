package com.recipe.app.src.userRecipe;

import com.recipe.app.src.user.models.User;
import com.recipe.app.src.userRecipe.models.UserRecipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserRecipeRepository extends CrudRepository<UserRecipe, Integer> {
    UserRecipe findByUserAndUserRecipeIdxAndStatus(User user, Integer userRecipeIdx, String status);
    Page<UserRecipe> findByUserAndStatus(User user, String status, Pageable pageable);
    Boolean existsByUserRecipeIdxAndStatus(Integer userRecipeIdx, String status);
    List<UserRecipe> findTop5ByStatusOrderByCreatedAtDesc(String status);
}
