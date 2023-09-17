package com.recipe.app.src.recipe.infra;

import com.recipe.app.src.user.infra.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RecipeViewJpaRepository extends CrudRepository<RecipeViewEntity, Long> {

    Optional<RecipeViewEntity> findByUserAndRecipe(UserEntity user, RecipeEntity recipe);

    List<RecipeViewEntity> findByRecipe(RecipeEntity recipe);
}
