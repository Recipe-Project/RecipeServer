package com.recipe.app.src.userRecipe.mapper;

import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.userRecipe.domain.UserRecipe;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRecipeRepository extends CrudRepository<UserRecipe, Integer> {
    List<UserRecipe> findByUserAndStatusOrderByCreatedAtDesc(User user, String status);

    Optional<UserRecipe> findByUserAndUserRecipeIdxAndStatus(User user, int userRecipeIdx, String status);
}
