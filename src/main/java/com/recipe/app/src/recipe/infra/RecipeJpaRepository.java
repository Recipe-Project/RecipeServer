package com.recipe.app.src.recipe.infra;

import com.recipe.app.src.user.domain.User;
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

    Page<RecipeEntity> findByUser(User user, Pageable pageable);

    @Query(value = "select r.*, rs.userId as scrapUserId, (count(*) / (select count(*) from RecipeIngredient where recipeId = r.recipeId) * 100) as matchRate from RecipeIngredient ri " +
            "inner join Recipe r on ri.recipeId = r.recipeId " +
            "inner join Ingredient i on i.ingredientId = ri.ingredientId " +
            "left join RecipeScrap rs on rs.recipeId = r.recipeId and rs.userId = :userId " +
            "left join RecipeView rv on rv.recipeId = r.recipeId and rv.userId = :userId " +
            "where r.hiddenYn = 'N' and (ri.ingredientId in :ingredientIds or i.ingredientName in :ingredientNames) " +
            "group by ri.recipeId order by matchRate desc",
            countQuery="select count(*) from RecipeIngredient ri " +
            "inner join Ingredient i on ri.ingredientId = i.ingredientId\n" +
            "inner join Recipe r on ri.recipeId = r.recipeId\n" +
            "left join RecipeScrap rs on rs.recipeId = r.recipeId and rs.userId = :userId " +
            "left join RecipeView rv on rv.recipeId = r.recipeId and rv.userId = :userId " +
            "where (ri.ingredientId in :ingredientIds or i.ingredientName in :ingredientNames) and r.hiddenYn = 'N' " +
            "group by r.recipeId",
            nativeQuery = true)
    Page<RecipeEntityWithRate> findRecipesOrderByFridgeIngredientCntDesc(List<Long> ingredientIds, List<String> ingredientNames, Long userId, Pageable pageable);
}