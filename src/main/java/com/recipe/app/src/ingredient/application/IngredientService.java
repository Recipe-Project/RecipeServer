package com.recipe.app.src.ingredient.application;

import com.recipe.app.src.ingredient.application.port.IngredientRepository;
import com.recipe.app.src.ingredient.domain.Ingredient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    public List<Ingredient> retrieveIngredients(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return ingredientRepository.findByStatus("ACTIVE");
        }
        return ingredientRepository.findByNameContainingAndStatus(keyword, "ACTIVE");
    }
}
