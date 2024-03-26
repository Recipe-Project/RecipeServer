package com.recipe.app.src.ingredient.infra;

import com.recipe.app.src.ingredient.domain.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    @Query("SELECT i FROM Ingredient i WHERE i.ingredientName LIKE concat('%', :keyword, '%') AND i.userId is null AND i.defaultYn = 'Y' AND i.hiddenYn = 'N'")
    List<Ingredient> findDefaultIngredientsByIngredientNameContaining(String keyword);

    @Query("SELECT i FROM Ingredient i WHERE i.userId is null AND i.defaultYn = 'Y' AND i.hiddenYn = 'N'")
    List<Ingredient> findDefaultIngredients();

    List<Ingredient> findByIngredientIdIn(List<Long> ingredientIds);

    Optional<Ingredient> findByIngredientName(String ingredientName);

    Optional<Ingredient> findByUserIdAndIngredientNameAndIngredientIconUrlAndIngredientCategoryId(Long userId, String ingredientName, String ingredientIconUrl, Long ingredientCategoryId);

    List<Ingredient> findByUserId(Long userId);
}
