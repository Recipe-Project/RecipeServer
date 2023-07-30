package com.recipe.app.src.ingredient.infra;

import com.recipe.app.src.ingredient.application.port.IngredientRepository;
import com.recipe.app.src.ingredient.domain.Ingredient;
import com.recipe.app.src.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class IngredientRepositoryImpl implements IngredientRepository {

    private final IngredientJpaRepository ingredientJpaRepository;

    @Override
    public List<Ingredient> findByUserIngredientsOrDefaultIngredients(User user) {
        return ingredientJpaRepository.findByUserIngredientsOrDefaultIngredients(user).stream().map(IngredientEntity::toModel).collect(Collectors.toList());
    }

    @Override
    public List<Ingredient> findByUserIngredientsOrDefaultIngredientsByKeyword(User user, String keyword) {
        return ingredientJpaRepository.findByUserIngredientsOrDefaultIngredientsByKeyword(user, keyword).stream().map(IngredientEntity::toModel).collect(Collectors.toList());
    }
}
