package com.recipe.app.src.ingredient.infra;

import com.recipe.app.src.ingredient.domain.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface IngredientRepository extends JpaRepository<Ingredient, Long>, IngredientCustomRepository {

    List<Ingredient> findByIngredientIdIn(Collection<Long> ingredientIds);

    Optional<Ingredient> findByUserIdAndIngredientNameAndIngredientIconIdAndIngredientCategoryId(Long userId, String ingredientName, Long ingredientIconId, Long ingredientCategoryId);

    List<Ingredient> findByUserId(Long userId);
}
