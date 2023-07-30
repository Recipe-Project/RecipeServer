package com.recipe.app.src.ingredient.infra;

import com.recipe.app.src.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IngredientJpaRepository extends JpaRepository<IngredientEntity, Integer> {
    @Query("SELECT i FROM IngredientEntity i WHERE i.ingredientName LIKE CONCAT('%',:keyword,'%') AND (i.user = :user or i.user is null) AND i.hiddenYn = 'N'")
    List<IngredientEntity> findByUserIngredientsOrDefaultIngredientsByKeyword(User user, String keyword);

    @Query("SELECT i FROM IngredientEntity i WHERE (i.user = :user or i.user is null) AND i.hiddenYn = 'N'")
    List<IngredientEntity> findByUserIngredientsOrDefaultIngredients(User user);

    List<IngredientEntity> findByIngredientIdIn(List<Long> ingredientList);

    Optional<IngredientEntity> findByIngredientName(String ingredientName);
}
