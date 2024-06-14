package com.recipe.app.src.recipe.infra;

import com.recipe.app.src.recipe.domain.RecipeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, Long> {

    List<RecipeIngredient> findByRecipeId(Long recipeId);

    List<RecipeIngredient> findByRecipeIdIn(Collection<Long> recipeIds);
}
