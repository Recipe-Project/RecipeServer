package com.recipe.app.src.recipe.application;

import com.recipe.app.src.fridge.application.port.FridgeRepository;
import com.recipe.app.src.fridge.domain.Fridge;
import com.recipe.app.src.ingredient.application.port.IngredientRepository;
import com.recipe.app.src.ingredient.domain.Ingredient;
import com.recipe.app.src.ingredient.exception.NotFoundIngredientException;
import com.recipe.app.src.recipe.application.dto.RecipeDto;
import com.recipe.app.src.recipe.application.port.RecipeRepository;
import com.recipe.app.src.recipe.domain.Recipe;
import com.recipe.app.src.recipe.domain.RecipeIngredient;
import com.recipe.app.src.recipe.domain.RecipeProcess;
import com.recipe.app.src.recipe.exception.NotFoundRecipeException;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.user.exception.ForbiddenAccessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final FridgeRepository fridgeRepository;

    public List<Recipe> getRecipes(String keyword) {
        return recipeRepository.getRecipes(keyword);
    }

    public Recipe getRecipe(Long recipeId) {
        return recipeRepository.findById(recipeId).orElseThrow(NotFoundRecipeException::new);
    }

    @Transactional
    public void createRecipeView(Long recipeId, User user) {
        Recipe recipe = getRecipe(recipeId);
        recipeRepository.saveRecipeView(recipe, user);
    }

    @Transactional
    public void createRecipeScrap(Long recipeId, User user) {
        Recipe recipe = getRecipe(recipeId);
        recipeRepository.saveRecipeScrap(recipe, user);
    }

    @Transactional
    public void deleteRecipeScrap(Long recipeId, User user) {
        Recipe recipe = getRecipe(recipeId);
        recipeRepository.deleteRecipeScrap(recipe, user);
    }

    public List<Recipe> getScrapRecipes(User user) {
        return recipeRepository.findScrapRecipesByUser(user);
    }

    public List<Recipe> getRecipesByUser(User user) {
        return recipeRepository.findByUser(user);
    }

    @Transactional
    public void deleteRecipe(User user, Long recipeId) {
        Recipe recipe = getRecipe(recipeId);
        if (!user.equals(recipe.getUser()))
            throw new ForbiddenAccessException();
        recipeRepository.delete(recipe);
    }

    @Transactional
    public void createRecipe(User user, RecipeDto.RecipeRequest request) {
        Recipe recipe = Recipe.from(request.getTitle(), null, null, null, request.getThumbnail(),
                null, null, user, true);
        recipe = recipeRepository.save(recipe);

        RecipeProcess recipeProcess = RecipeProcess.from(recipe, 1, request.getContent(), null);
        recipeRepository.saveRecipeProcess(recipeProcess);

        Map<Long, String> capacitiesByIngredientId = request.getIngredients().stream()
                .collect(Collectors.toMap(RecipeDto.RecipeIngredientRequest::getIngredientId, RecipeDto.RecipeIngredientRequest::getCapacity));
        List<Ingredient> ingredients = ingredientRepository.findByIngredientIdIn(new ArrayList<>(capacitiesByIngredientId.keySet()));
        if (ingredients.size() != capacitiesByIngredientId.keySet().size())
            throw new NotFoundIngredientException();

        List<RecipeIngredient> recipeIngredients = new ArrayList<>();
        for (Ingredient ingredient : ingredients) {
            String capacity = capacitiesByIngredientId.get(ingredient.getIngredientId());
            RecipeIngredient recipeIngredient = RecipeIngredient.from(recipe, ingredient, capacity);
            recipeIngredients.add(recipeIngredient);
        }
        recipeRepository.saveRecipeIngredients(recipeIngredients);
    }

    @Transactional
    public void updateRecipe(User user, Long recipeId, RecipeDto.RecipeRequest request) {
        Recipe recipe = getRecipe(recipeId);
        if (!user.equals(recipe.getUser()))
            throw new ForbiddenAccessException();

        recipe = recipe.update(recipe.getRecipeId(), request.getTitle(), null, null, null, request.getThumbnail(),
                null, null, true);
        recipe = recipeRepository.save(recipe);

        RecipeProcess recipeProcess = recipeRepository.findRecipeProcessByRecipe(recipe);
        recipeProcess = recipeProcess.update(request.getContent(), null);
        recipeRepository.saveRecipeProcess(recipeProcess);

        recipeRepository.deleteRecipeIngredients(recipe.getRecipeIngredients());
        Map<Long, String> capacitiesByIngredientId = request.getIngredients().stream()
                .collect(Collectors.toMap(RecipeDto.RecipeIngredientRequest::getIngredientId, RecipeDto.RecipeIngredientRequest::getCapacity));
        List<Ingredient> ingredients = ingredientRepository.findByIngredientIdIn(new ArrayList<>(capacitiesByIngredientId.keySet()));
        if (ingredients.size() != capacitiesByIngredientId.keySet().size())
            throw new NotFoundIngredientException();

        List<RecipeIngredient> recipeIngredients = new ArrayList<>();
        for (Ingredient ingredient : ingredients) {
            String capacity = capacitiesByIngredientId.get(ingredient.getIngredientId());
            RecipeIngredient recipeIngredient = RecipeIngredient.from(recipe, ingredient, capacity);
            recipeIngredients.add(recipeIngredient);
        }
        recipeRepository.saveRecipeIngredients(recipeIngredients);
    }

    public List<Recipe> retrieveFridgeRecipes(User user, Pageable pageable) {

        List<Ingredient> fridgeIngredients = fridgeRepository.findByUser(user).stream()
                .map(Fridge::getIngredient)
                .collect(Collectors.toList());

        return recipeRepository.findRecipesOrderByFridgeIngredientCntDesc(fridgeIngredients, pageable);
    }

    public long countRecipeScrapByUser(User user) {
        return recipeRepository.countRecipeScrapByUser(user);
    }
}
