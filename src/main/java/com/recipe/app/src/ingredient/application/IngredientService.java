package com.recipe.app.src.ingredient.application;

import com.recipe.app.src.ingredient.domain.Ingredient;
import com.recipe.app.src.ingredient.exception.NotFoundIngredientException;
import com.recipe.app.src.ingredient.infra.IngredientRepository;
import com.recipe.app.src.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    @Transactional(readOnly = true)
    public List<Ingredient> findByKeyword(Long userId, String keyword) {

        if (!StringUtils.hasText(keyword)) {
            return ingredientRepository.findDefaultIngredients(userId);
        }

        return ingredientRepository.findDefaultIngredientsByKeyword(userId, keyword);
    }

    @Transactional(readOnly = true)
    public List<Ingredient> findByIngredientIds(Collection<Long> ingredientIds) {

        return ingredientRepository.findByIngredientIdIn(ingredientIds);
    }

    @Transactional(readOnly = true)
    public Optional<Ingredient> findByUserIdAndIngredientNameAndIngredientIconIdAndIngredientCategoryId(Long userId, String ingredientName, Long ingredientIconId, Long ingredientCategoryId) {

        return ingredientRepository.findByUserIdAndIngredientNameAndIngredientIconIdAndIngredientCategoryId(userId, ingredientName, ingredientIconId, ingredientCategoryId);
    }

    @Transactional
    public void createIngredient(Ingredient ingredient) {
        ingredientRepository.save(ingredient);
    }

    @Transactional
    public void createIngredients(Collection<Ingredient> ingredients) {
        ingredientRepository.saveAll(ingredients);
    }

    @Transactional
    public void deleteIngredientsByUser(User user) {

        List<Ingredient> ingredients = ingredientRepository.findByUserId(user.getUserId());
        ingredientRepository.deleteAll(ingredients);
    }

    @Transactional(readOnly = true)
    public Ingredient findByIngredientId(Long ingredientId) {

        return ingredientRepository.findById(ingredientId).orElseThrow(() -> {
            throw new NotFoundIngredientException();
        });
    }
}
