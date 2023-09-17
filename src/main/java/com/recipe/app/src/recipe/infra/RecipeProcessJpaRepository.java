package com.recipe.app.src.recipe.infra;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RecipeProcessJpaRepository extends CrudRepository<RecipeProcessEntity, Long> {
    List<RecipeProcessEntity> findByRecipeOrderByCookingNo(RecipeEntity recipe);
}
