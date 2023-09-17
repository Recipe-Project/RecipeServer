package com.recipe.app.src.ingredient.application;

import com.recipe.app.src.ingredient.application.port.IngredientCategoryRepository;
import com.recipe.app.src.ingredient.application.port.IngredientRepository;
import com.recipe.app.src.ingredient.domain.Ingredient;
import com.recipe.app.src.ingredient.domain.IngredientCategory;
import com.recipe.app.src.ingredient.exception.NotFoundIngredientCategoryException;
import com.recipe.app.src.ingredient.exception.NotFoundIngredientException;
import com.recipe.app.src.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final IngredientCategoryRepository ingredientCategoryRepository;

    public Map<IngredientCategory, List<Ingredient>> getIngredientsGroupingByIngredientCategory(String keyword) {
        return getIngredientsByKeyword(keyword).stream()
                .collect(Collectors.groupingBy(Ingredient::getIngredientCategory));
    }

    public List<Ingredient> getIngredientsByKeyword(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return ingredientRepository.findDefaultIngredients();
        }
        return ingredientRepository.findDefaultIngredientsByIngredientNameContaining(keyword);
    }

    public List<Ingredient> getIngredientsByIngredientIds(List<Long> ingredientIds) {
        List<Ingredient> ingredients = ingredientRepository.findByIngredientIdIn(ingredientIds);
        if (ingredients.size() != ingredientIds.size())
            throw new NotFoundIngredientException();
        return ingredients;
    }

    public Optional<Ingredient> getIngredientByUserAndIngredientNameAndIngredientIconUrlAndIngredientCategory(User user, String ingredientName, String ingredientIconUrl, IngredientCategory ingredientCategory) {
        return ingredientRepository.findByUserAndIngredientNameAndIngredientIconUrlAndIngredientCategory(user, ingredientName, ingredientIconUrl, ingredientCategory);
    }

    public IngredientCategory getIngredientCategoryByIngredientCategoryId(Long ingredientCategoryId) {
        return ingredientCategoryRepository.findById(ingredientCategoryId).orElseThrow(NotFoundIngredientCategoryException::new);
    }

    public Ingredient createIngredient(Ingredient ingredient) {
        return ingredientRepository.save(ingredient);
    }
}
