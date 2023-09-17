package com.recipe.app.src.recipe.infra;

import com.recipe.app.src.user.infra.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RecipeScrapJpaRepository extends CrudRepository<RecipeScrapEntity, Long> {
    Optional<RecipeScrapEntity> findByUserAndRecipe(UserEntity user, RecipeEntity recipe);

    Page<RecipeScrapEntity> findByUser(UserEntity user, Pageable pageable);

    long countByUser(UserEntity user);

    List<RecipeScrapEntity> findByRecipe(RecipeEntity recipe);
}
