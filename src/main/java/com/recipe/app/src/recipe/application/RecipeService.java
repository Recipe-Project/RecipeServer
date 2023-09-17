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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
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

    public Page<Recipe> getRecipes(String keyword, int page, int size, String sort) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Recipe> recipes;
        if (sort.equals("recipeScraps"))
            recipes = recipeRepository.getRecipesOrderByRecipeScrapSizeDesc(keyword, pageable);
        else if (sort.equals("recipeViews"))
            recipes = recipeRepository.getRecipesOrderByRecipeViewSizeDesc(keyword, pageable);
        else
            recipes = recipeRepository.getRecipesOrderByCreatedAtDesc(keyword, pageable);

        return recipes;
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

    public Page<Recipe> getScrapRecipes(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return recipeRepository.findScrapRecipesByUser(user, pageable);
    }

    public Page<Recipe> getRecipesByUser(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return recipeRepository.findByUser(user, pageable);
    }

    public List<RecipeIngredient> getRecipeIngredientsByRecipe(Recipe recipe) {
        return recipeRepository.findRecipeIngredientsByRecipe(recipe);
    }

    public List<RecipeProcess> getRecipeProcessesByRecipe(Recipe recipe) {
        return recipeRepository.findRecipeProcessesByRecipe(recipe);
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

        List<RecipeProcess> existRecipeProcesses = getRecipeProcessesByRecipe(recipe);
        recipeRepository.deleteRecipeProcesses(existRecipeProcesses);

        RecipeProcess recipeProcess = RecipeProcess.from(recipe, 1, request.getContent(), null);
        recipeRepository.saveRecipeProcess(recipeProcess);

        List<RecipeIngredient> existRecipeIngredients = getRecipeIngredientsByRecipe(recipe);
        recipeRepository.deleteRecipeIngredients(existRecipeIngredients);

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

    public Page<Recipe> retrieveFridgeRecipes(User user, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        List<Ingredient> fridgeIngredients = fridgeRepository.findByUser(user).stream()
                .map(Fridge::getIngredient)
                .collect(Collectors.toList());
        List<String> fridgeIngredientNames = fridgeIngredients.stream()
                .flatMap(fridgeIngredient -> {
                    List<String> ingredientNames = new ArrayList<>();
                    ingredientNames.add(fridgeIngredient.getIngredientName());
                    ingredientNames.addAll(fridgeIngredient.getSimilarIngredientName());
                    return ingredientNames.stream();
                })
                .collect(Collectors.toList());

        return recipeRepository.findRecipesOrderByFridgeIngredientCntDesc(fridgeIngredients, fridgeIngredientNames, pageable);
    }

    public long countRecipeScrapByUser(User user) {
        return recipeRepository.countRecipeScrapByUser(user);
    }

    public Map<Recipe, Integer> getIngredientsMatchRateByRecipes(User user, List<Recipe> recipes) {
        Map<Recipe, List<RecipeIngredient>> recipeIngredientsMapByRecipe = recipeRepository.findRecipeIngredientsByRecipeIn(recipes).stream()
                .collect(Collectors.groupingBy(RecipeIngredient::getRecipe));

        List<Ingredient> ingredientsInFridge = fridgeRepository.findByUser(user).stream()
                .map(Fridge::getIngredient)
                .collect(Collectors.toList());

        Map<Recipe, Integer> ingredientsMatchRateMapByRecipe = new HashMap<>();
        for (Recipe recipe : recipeIngredientsMapByRecipe.keySet()) {
            List<RecipeIngredient> recipeIngredients = recipeIngredientsMapByRecipe.get(recipe);
            System.out.println(recipeIngredients.size());
            long ingredientsMatchCount = recipeIngredients.stream()
                    .filter(recipeIngredient -> ingredientsInFridge.contains(recipeIngredient.getIngredient()))
                    .count();
            System.out.println(recipe.getRecipeId() +" "+ingredientsMatchCount+" "+ recipeIngredients.size()+" "+(double) ingredientsMatchCount / recipeIngredients.size() * 100);
            ingredientsMatchRateMapByRecipe.put(recipe, (int) ((double) ingredientsMatchCount / recipeIngredients.size() * 100));
        }

        return ingredientsMatchRateMapByRecipe;
    }
}

