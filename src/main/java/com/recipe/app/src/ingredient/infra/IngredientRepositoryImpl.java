package com.recipe.app.src.ingredient.infra;

import com.recipe.app.src.ingredient.application.port.IngredientRepository;
import com.recipe.app.src.ingredient.domain.Ingredient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class IngredientRepositoryImpl implements IngredientRepository {

    private final IngredientJpaRepository ingredientJpaRepository;

    @Override
    public List<Ingredient> findDefaultIngredients() {
        return ingredientJpaRepository.findDefaultIngredients().stream().map(IngredientEntity::toModel).collect(Collectors.toList());
    }

    @Override
    public List<Ingredient> findByIngredientIdIn(List<Long> ingredientIds) {
        return ingredientJpaRepository.findByIngredientIdIn(ingredientIds).stream().map(IngredientEntity::toModel).collect(Collectors.toList());
    }

    @Override
    public List<Ingredient> findDefaultIngredientsByIngredientNameContaining(String keyword) {
        return ingredientJpaRepository.findDefaultIngredientsByIngredientNameContaining(keyword).stream().map(IngredientEntity::toModel).collect(Collectors.toList());
    }
}
