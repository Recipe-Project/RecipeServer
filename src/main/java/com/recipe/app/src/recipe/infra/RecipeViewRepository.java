package com.recipe.app.src.recipe.infra;

import com.recipe.app.src.recipe.domain.RecipeView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeViewRepository extends JpaRepository<RecipeView, Long> {

    Optional<RecipeView> findByUserIdAndRecipeId(Long userId, Long recipeId);

    List<RecipeView> findByRecipeId(Long recipeId);

    List<RecipeView> findByRecipeIdIn(Collection<Long> recipeIds);

    long countByRecipeId(Long recipeId);
}
