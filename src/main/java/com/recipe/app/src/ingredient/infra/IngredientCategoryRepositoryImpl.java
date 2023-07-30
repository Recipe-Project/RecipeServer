package com.recipe.app.src.ingredient.infra;

import com.recipe.app.src.ingredient.application.port.IngredientCategoryRepository;
import com.recipe.app.src.ingredient.domain.IngredientCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class IngredientCategoryRepositoryImpl implements IngredientCategoryRepository {

    private final IngredientCategoryJpaRepository ingredientCategoryJpaRepository;

    @Override
    public List<IngredientCategory> findByStatus(String status) {
        return ingredientCategoryJpaRepository.findByStatus(status);
    }

    @Override
    public Optional<IngredientCategory> findById(Integer ingredientCategoryIdx) {
        return ingredientCategoryJpaRepository.findById(ingredientCategoryIdx);
    }
}
