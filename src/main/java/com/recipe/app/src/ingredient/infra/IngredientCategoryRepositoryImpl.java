package com.recipe.app.src.ingredient.infra;

import com.recipe.app.src.ingredient.application.port.IngredientCategoryRepository;
import com.recipe.app.src.ingredient.domain.IngredientCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class IngredientCategoryRepositoryImpl implements IngredientCategoryRepository {

    private final IngredientCategoryJpaRepository ingredientCategoryJpaRepository;

    @Override
    public Optional<IngredientCategory> findById(Long ingredientCategoryId) {
        return ingredientCategoryJpaRepository.findById(ingredientCategoryId).map(IngredientCategoryEntity::toModel);
    }
}
