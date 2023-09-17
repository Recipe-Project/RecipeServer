package com.recipe.app.src.recipe.infra;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RecipeIngredientJpaRepository extends CrudRepository<RecipeIngredientEntity, Long> {
    List<RecipeIngredientEntity> findByRecipe(RecipeEntity recipe);

}
