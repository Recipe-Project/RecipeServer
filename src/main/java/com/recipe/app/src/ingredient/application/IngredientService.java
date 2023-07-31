package com.recipe.app.src.ingredient.application;

import com.recipe.app.src.ingredient.application.port.IngredientRepository;
import com.recipe.app.src.ingredient.domain.Ingredient;
import com.recipe.app.src.ingredient.domain.IngredientCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    public Map<IngredientCategory, List<Ingredient>> getIngredientsGroupingByIngredientCategory(String keyword) {
        return getIngredients(keyword).stream()
                .collect(Collectors.groupingBy(Ingredient::getIngredientCategory));
    }

    public List<Ingredient> getIngredients(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return ingredientRepository.findDefaultIngredients();
        }
        return ingredientRepository.findDefaultIngredientsByIngredientNameContaining(keyword);
    }
}
