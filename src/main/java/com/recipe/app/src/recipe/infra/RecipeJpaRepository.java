package com.recipe.app.src.recipe.infra;

import com.recipe.app.src.user.infra.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RecipeJpaRepository extends CrudRepository<RecipeEntity, Long> {

    @Query("SELECT r FROM RecipeEntity r LEFT OUTER JOIN RecipeIngredientEntity ri on r.recipeId=ri.recipe.recipeId " +
            "WHERE (r.recipeNm LIKE CONCAT('%',:keyword,'%') OR ri.ingredient.ingredientName LIKE CONCAT('%',:keyword,'%')) AND r.hiddenYn = 'N' " +
            "group by r.recipeId order by r.createdAt desc")
    Page<RecipeEntity> getRecipesOrderByCreatedAtDesc(String keyword, Pageable pageable);

    @Query("SELECT r FROM RecipeEntity r LEFT OUTER JOIN RecipeIngredientEntity ri on r.recipeId=ri.recipe.recipeId " +
            "WHERE (r.recipeNm LIKE CONCAT('%',:keyword,'%') OR ri.ingredient.ingredientName LIKE CONCAT('%',:keyword,'%')) AND r.hiddenYn = 'N' " +
            "group by r.recipeId order by r.recipeScraps.size desc")
    Page<RecipeEntity> getRecipesOrderByRecipeScrapSizeDesc(String keyword, Pageable pageable);

    @Query("SELECT r FROM RecipeEntity r LEFT OUTER JOIN RecipeIngredientEntity ri on r.recipeId=ri.recipe.recipeId " +
            "WHERE (r.recipeNm LIKE CONCAT('%',:keyword,'%') OR ri.ingredient.ingredientName LIKE CONCAT('%',:keyword,'%')) AND r.hiddenYn = 'N' " +
            "group by r.recipeId order by r.recipeViews.size desc")
    Page<RecipeEntity> getRecipesOrderByRecipeViewSizeDesc(String keyword, Pageable pageable);

    @Query("select ri.recipe from RecipeIngredientEntity ri where ri.ingredient in :ingredients group by ri.recipe order by count(ri) desc")
    List<RecipeEntity> findRecipesOrderByFridgeIngredientCntDesc(List<IngredientEntity> ingredients, Pageable pageable);
}
