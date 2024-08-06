package com.recipe.app.src.recipe.application;

import com.recipe.app.src.fridge.application.FridgeService;
import com.recipe.app.src.recipe.application.dto.RecipeIngredientResponse;
import com.recipe.app.src.recipe.domain.RecipeIngredient;
import com.recipe.app.src.recipe.infra.RecipeIngredientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecipeIngredientService {

    private final RecipeIngredientRepository recipeIngredientRepository;
    private final FridgeService fridgeService;


    public RecipeIngredientService(RecipeIngredientRepository recipeIngredientRepository, FridgeService fridgeService) {
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.fridgeService = fridgeService;
    }

    @Transactional
    public void deleteAllByRecipeIds(Collection<Long> recipeIds) {

        recipeIngredientRepository.deleteAll(findByRecipeIds(recipeIds));
    }

    @Transactional
    public void createRecipeIngredients(List<RecipeIngredient> ingredients) {
        recipeIngredientRepository.saveAll(ingredients);
    }

    @Transactional
    public void deleteAllByRecipeId(Long recipeId) {

        recipeIngredientRepository.deleteAll(findByRecipeId(recipeId));
    }

    @Transactional(readOnly = true)
    public List<RecipeIngredientResponse> findRecipeIngredientsByUserIdAndRecipeId(Long userId, Long recipeId) {

        return findByRecipeId(recipeId).stream()
                .map(recipeIngredient -> RecipeIngredientResponse.from(recipeIngredient, fridgeService.isInFridge(userId, recipeIngredient.getIngredientName())))
                .collect(Collectors.toList());
    }

    private List<RecipeIngredient> findByRecipeId(Long recipeId) {

        return recipeIngredientRepository.findByRecipeId(recipeId);
    }

    @Transactional(readOnly = true)
    public List<RecipeIngredient> findByRecipeIds(Collection<Long> recipeIds) {

        return recipeIngredientRepository.findByRecipeIdIn(recipeIds);
    }
}
