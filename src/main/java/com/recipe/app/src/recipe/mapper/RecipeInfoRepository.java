package com.recipe.app.src.recipe.mapper;

import com.recipe.app.src.recipe.domain.RecipeInfo;
import com.recipe.app.src.user.infra.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeInfoRepository extends CrudRepository<RecipeInfo, Integer> {
    @Query("SELECT r FROM RecipeInfo r LEFT OUTER JOIN r.recipeIngredients ri on r.recipeId=ri.recipeInfo.recipeId WHERE (r.recipeNmKo LIKE CONCAT('%',:keyword,'%') OR ri.irdntNm LIKE CONCAT('%',:keyword,'%')) AND r.status=:status group by r.recipeId")
    List<RecipeInfo> searchRecipeInfos(String keyword, String status);

    @Query("SELECT r FROM FridgeEntity f "
            + "    LEFT OUTER JOIN RecipeIngredient ri on ri.irdntNm LIKE concat('%', f.ingredientName, '%') AND ri.status=:status "
            + "    INNER JOIN RecipeInfo r on r.recipeId = ri.recipeInfo.recipeId AND r.status=:status"
            + " WHERE f.user=:user AND f.status=:status"
            + " GROUP BY ri.recipeInfo.recipeId ORDER BY COUNT(f) DESC")
    List<RecipeInfo> searchRecipeListOrderByIngredientCntWhichUserHasDesc(UserEntity user, String status);
}
