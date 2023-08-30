package com.recipe.app.src.recipe.infra;

import org.springframework.data.repository.CrudRepository;

public interface RecipeIngredientJpaRepository extends CrudRepository<RecipeIngredientEntity, Long> {
}
