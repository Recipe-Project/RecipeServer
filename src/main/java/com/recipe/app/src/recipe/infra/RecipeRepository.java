package com.recipe.app.src.recipe.infra;

import com.recipe.app.src.recipe.domain.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long>, RecipeCustomRepository {

    List<Recipe> findByUserId(Long userId);

    long countByUserId(Long userId);

    Optional<Recipe> findByUserIdAndRecipeId(Long userId, Long recipeId);
}