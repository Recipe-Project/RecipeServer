package com.recipe.app.src.ingredient.application;

import com.recipe.app.src.ingredient.application.port.IngredientRepository;
import com.recipe.app.src.ingredient.domain.Ingredient;
import com.recipe.app.src.ingredient.domain.IngredientCategory;
import com.recipe.app.src.user.domain.User;
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

    public Map<IngredientCategory, List<Ingredient>> getUserIngredientsGroupingByIngredientCategory(String keyword, User user) {
        return getUserIngredients(keyword, user).stream()
                .collect(Collectors.groupingBy(Ingredient::getIngredientCategory));
    }

    public List<Ingredient> getUserIngredients(String keyword, User user) {
        if (!StringUtils.hasText(keyword)) {
            return ingredientRepository.findByUserIngredientsOrDefaultIngredients(user);
        }
        return ingredientRepository.findByUserIngredientsOrDefaultIngredientsByKeyword(user, keyword);
    }

    public Map<IngredientCategory, List<Ingredient>> getDefaultIngredientsGroupingByIngredientCategory() {
        return getDefaultIngredients().stream()
                .collect(Collectors.groupingBy(Ingredient::getIngredientCategory));
    }

    public List<Ingredient> getDefaultIngredients() {
        return ingredientRepository.findByDefaultIngredients();
    }
}
