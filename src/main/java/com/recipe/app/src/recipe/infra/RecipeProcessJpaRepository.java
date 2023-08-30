package com.recipe.app.src.recipe.infra;

import org.springframework.data.repository.CrudRepository;

public interface RecipeProcessJpaRepository extends CrudRepository<RecipeProcessEntity, Long> {
    RecipeProcessEntity findByRecipeAndCookingNo(RecipeEntity recipe, int cookingNo);
}
