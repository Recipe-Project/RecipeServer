package com.recipe.app.src.recipe.application;

import com.recipe.app.src.fridge.application.FridgeService;
import com.recipe.app.src.ingredient.application.IngredientService;
import com.recipe.app.src.ingredient.domain.Ingredient;
import com.recipe.app.src.recipe.application.dto.RecipeIngredientRequest;
import com.recipe.app.src.recipe.application.dto.RecipeIngredientResponse;
import com.recipe.app.src.recipe.domain.RecipeIngredient;
import com.recipe.app.src.recipe.infra.RecipeIngredientRepository;
import com.recipe.app.src.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RecipeIngredientService {

    private final RecipeIngredientRepository recipeIngredientRepository;
    private final IngredientService ingredientService;
    private final FridgeService fridgeService;


    public RecipeIngredientService(RecipeIngredientRepository recipeIngredientRepository, IngredientService ingredientService, FridgeService fridgeService) {
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.ingredientService = ingredientService;
        this.fridgeService = fridgeService;
    }

    @Transactional
    public void deleteAllByRecipeIds(Collection<Long> recipeIds) {

        recipeIngredientRepository.deleteAll(findByRecipeIds(recipeIds));
    }

    @Transactional
    public void createRecipeIngredients(User user, Long recipeId, List<RecipeIngredientRequest> request) {

        List<RecipeIngredient> recipeIngredients = getRecipeIngredientsWithIngredientsSave(user, recipeId, request);
        recipeIngredients.addAll(getRecipeIngredientsByIngredientIds(recipeId, request));

        recipeIngredientRepository.saveAll(recipeIngredients);
    }

    private List<RecipeIngredient> getRecipeIngredientsWithIngredientsSave(User user, Long recipeId, List<RecipeIngredientRequest> request) {

        Map<Ingredient, String> capacityMapByIngredient = request.stream()
                .filter(recipeIngredient -> recipeIngredient.getIngredientId() == null)
                .collect(Collectors.toMap(recipeIngredient -> Ingredient.builder()
                                .ingredientCategoryId(recipeIngredient.getIngredientCategoryId())
                                .ingredientName(recipeIngredient.getIngredientName())
                                .ingredientIconUrl(recipeIngredient.getIngredientIconUrl())
                                .userId(user.getUserId())
                                .build(),
                        RecipeIngredientRequest::getCapacity));
        ingredientService.createIngredients(capacityMapByIngredient.keySet());

        return capacityMapByIngredient.keySet().stream()
                .map(ingredient -> RecipeIngredient.builder()
                        .ingredientId(ingredient.getIngredientId())
                        .recipeId(recipeId)
                        .capacity(capacityMapByIngredient.get(ingredient))
                        .build())
                .collect(Collectors.toList());
    }

    private List<RecipeIngredient> getRecipeIngredientsByIngredientIds(Long recipeId, List<RecipeIngredientRequest> request) {

        return request.stream()
                .filter(recipeIngredient -> recipeIngredient.getIngredientId() != null)
                .map(recipeIngredient -> RecipeIngredient.builder()
                        .ingredientId(recipeIngredient.getIngredientId())
                        .recipeId(recipeId)
                        .capacity(recipeIngredient.getCapacity())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteAllByRecipeId(Long recipeId) {

        recipeIngredientRepository.deleteAll(findByRecipeId(recipeId));
    }

    @Transactional(readOnly = true)
    public List<RecipeIngredientResponse> findRecipeIngredientsByUserIdAndRecipeId(Long userId, Long recipeId) {

        List<RecipeIngredient> recipeIngredients = findByRecipeId(recipeId);
        List<Long> ingredientIds = recipeIngredients.stream()
                .map(RecipeIngredient::getIngredientId)
                .collect(Collectors.toList());
        Map<Long, Ingredient> ingredientMapById = ingredientService.findByIngredientIds(ingredientIds).stream()
                .collect(Collectors.toMap(Ingredient::getIngredientId, Function.identity()));

        return recipeIngredients.stream()
                .map(recipeIngredient -> {
                    Ingredient ingredient = ingredientMapById.get(recipeIngredient.getIngredientId());
                    return RecipeIngredientResponse.from(recipeIngredient, ingredient, fridgeService.isInFridge(userId, ingredient));
                })
                .collect(Collectors.toList());
    }

    private List<RecipeIngredient> findByRecipeId(Long recipeId) {

        return recipeIngredientRepository.findByRecipeId(recipeId);
    }

    private List<RecipeIngredient> findByRecipeIds(Collection<Long> recipeIds) {

        return recipeIngredientRepository.findByRecipeIdIn(recipeIds);
    }
}
