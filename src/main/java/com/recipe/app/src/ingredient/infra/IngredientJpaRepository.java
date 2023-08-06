package com.recipe.app.src.ingredient.infra;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IngredientJpaRepository extends JpaRepository<IngredientEntity, Integer> {
    @Query("SELECT i FROM IngredientEntity i WHERE i.ingredientName LIKE concat('%', :keyword, '%') AND i.user is null AND i.defaultYn = 'Y' AND i.hiddenYn = 'N'")
    List<IngredientEntity> findDefaultIngredientsByIngredientNameContaining(String keyword);

    @Query("SELECT i FROM IngredientEntity i WHERE i.user is null AND i.defaultYn = 'Y' AND i.hiddenYn = 'N'")
    List<IngredientEntity> findDefaultIngredients();

    List<IngredientEntity> findByIngredientIdIn(List<Long> ingredientList);

    Optional<IngredientEntity> findByIngredientName(String ingredientName);
}
