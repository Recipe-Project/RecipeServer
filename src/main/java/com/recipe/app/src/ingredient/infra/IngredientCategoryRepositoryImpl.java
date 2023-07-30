package com.recipe.app.src.ingredient.infra;

import com.recipe.app.src.ingredient.application.port.IngredientCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class IngredientCategoryRepositoryImpl implements IngredientCategoryRepository {

    private final IngredientCategoryJpaRepository ingredientCategoryJpaRepository;

    @Override
    public List<IngredientCategoryEntity> findAll(String status) {
        return null;
    }

    @Override
    public Optional<IngredientCategoryEntity> findById(Integer ingredientCategoryIdx) {
        return ingredientCategoryJpaRepository.findById(ingredientCategoryIdx);
    }
}
