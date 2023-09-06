package com.recipe.app.src.recipe.infra;

import com.recipe.app.src.ingredient.infra.IngredientEntity;
import com.recipe.app.src.user.infra.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RecipeJpaRepository extends CrudRepository<RecipeEntity, Long> {

    @Query("SELECT r FROM RecipeEntity r LEFT OUTER JOIN r.recipeIngredients ri on r.recipeId=ri.recipe.recipeId WHERE (r.recipeNm LIKE CONCAT('%',:keyword,'%') OR ri.ingredient.ingredientName LIKE CONCAT('%',:keyword,'%')) group by r.recipeId")
    List<RecipeEntity> getRecipes(String keyword);

    List<RecipeEntity> findByUser(UserEntity user);

    @Query("select ri.recipe from RecipeIngredientEntity ri where ri.ingredient in :ingredients group by ri.recipe order by count(ri) desc")
    List<RecipeEntity> findRecipesOrderByFridgeIngredientCntDesc(List<IngredientEntity> ingredients, Pageable pageable);
}
