package com.recipe.app.src.recipeInfo;



import com.recipe.app.src.recipeInfo.models.RecipeInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface RecipeInfoRepository extends CrudRepository<RecipeInfo, Integer> {
    @Query("SELECT r FROM RecipeInfo r LEFT OUTER JOIN r.recipeIngredients ri on r.recipeId=ri.recipeInfo.recipeId WHERE (r.recipeNmKo LIKE CONCAT('%',:keyword,'%') OR ri.irdntNm LIKE CONCAT('%',:keyword,'%')) AND r.status=:status group by r.recipeId")
    //@Query(value = "select r.* from RecipeInfo r left outer join RecipeIngredient ri on ri.recipeId = r.recipeId where r.recipeNmKo like '%고기%' OR ri.irdntNm like '%고기%' group by r.recipeId", nativeQuery = true)
    List<RecipeInfo> searchRecipeInfos(String keyword, String status);

    List<RecipeInfo> findByStatus(String active);

    RecipeInfo findByRecipeIdAndStatus(Integer recipeId, String active);
}
