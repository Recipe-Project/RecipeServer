package com.recipe.app.src.recipe.infra;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RecipeJpaRepository extends CrudRepository<RecipeEntity, Long> {

    @Query("SELECT r FROM RecipeEntity r LEFT OUTER JOIN r.recipeIngredients ri on r.recipeId=ri.recipe.recipeId WHERE (r.recipeNm LIKE CONCAT('%',:keyword,'%') OR ri.ingredient.ingredientName LIKE CONCAT('%',:keyword,'%')) group by r.recipeId")
    List<RecipeEntity> getRecipes(String keyword);
}
