package com.recipe.app.src.recipe.infra;

import com.recipe.app.src.user.domain.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RecipeViewJpaRepository extends CrudRepository<RecipeViewEntity, Long> {

    Optional<RecipeViewEntity> findByUserAndRecipe(User user, RecipeEntity recipe);

    List<RecipeViewEntity> findByRecipe(RecipeEntity recipe);
}
