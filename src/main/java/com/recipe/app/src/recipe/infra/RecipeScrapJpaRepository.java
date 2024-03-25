package com.recipe.app.src.recipe.infra;

import com.recipe.app.src.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RecipeScrapJpaRepository extends CrudRepository<RecipeScrapEntity, Long> {
    Optional<RecipeScrapEntity> findByUserAndRecipe(User user, RecipeEntity recipe);

    Page<RecipeScrapEntity> findByUser(User user, Pageable pageable);

    long countByUser(User user);

    List<RecipeScrapEntity> findByRecipe(RecipeEntity recipe);
}
