package com.recipe.app.src.ingredient.application;

import com.recipe.app.src.ingredient.domain.IngredientCategory;
import com.recipe.app.src.ingredient.exception.NotFoundIngredientCategoryException;
import com.recipe.app.src.ingredient.infra.IngredientCategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IngredientCategoryService {

    private final IngredientCategoryRepository ingredientCategoryRepository;

    public IngredientCategoryService(IngredientCategoryRepository ingredientCategoryRepository) {
        this.ingredientCategoryRepository = ingredientCategoryRepository;
    }

    @Transactional(readOnly = true)
    public List<IngredientCategory> findAll() {

        return ingredientCategoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public IngredientCategory findById(Long ingredientCategoryId) {

        return ingredientCategoryRepository.findById(ingredientCategoryId).orElseThrow(NotFoundIngredientCategoryException::new);
    }
}
