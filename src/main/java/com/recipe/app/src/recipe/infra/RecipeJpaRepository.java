package com.recipe.app.src.recipe.infra;

import com.recipe.app.src.user.infra.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RecipeJpaRepository extends CrudRepository<RecipeEntity, Long> {

    @Query("SELECT r FROM RecipeEntity r LEFT OUTER JOIN r.recipeIngredients ri on r.recipeId=ri.recipe.recipeId WHERE (r.recipeNm LIKE CONCAT('%',:keyword,'%') OR ri.ingredient.ingredientName LIKE CONCAT('%',:keyword,'%')) group by r.recipeId")
    List<RecipeEntity> getRecipes(String keyword);

    @Query("SELECT r FROM FridgeEntity f "
            + "    LEFT OUTER JOIN RecipeIngredientEntity ri on ri.irdntNm LIKE concat('%', f.ingredientName, '%') AND ri.status=:status "
            + "    INNER JOIN RecipeEntity r on r.recipeId = ri.recipeInfo.recipeId AND r.status=:status"
            + " WHERE f.user=:user AND f.status=:status"
            + " GROUP BY ri.recipeInfo.recipeId ORDER BY COUNT(f) DESC")
    List<RecipeEntity> searchRecipeListOrderByIngredientCntWhichUserHasDesc(UserEntity user, String status);
}
