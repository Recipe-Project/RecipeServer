package com.recipe.app.src.recipe.infra;

import com.recipe.app.src.user.infra.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RecipeScrapJpaRepository extends CrudRepository<RecipeScrapEntity, Long> {
    Optional<RecipeScrapEntity> findByUserAndRecipe(UserEntity user, RecipeEntity recipe);

    List<RecipeScrapEntity> findByUser(UserEntity user);
}
