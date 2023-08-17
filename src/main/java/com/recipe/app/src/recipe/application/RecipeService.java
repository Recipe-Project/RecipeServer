package com.recipe.app.src.recipe.application;

import com.recipe.app.src.recipe.application.port.RecipeRepository;
import com.recipe.app.src.recipe.domain.Recipe;
import com.recipe.app.src.recipe.exception.NotFoundRecipeException;
import com.recipe.app.src.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecipeService {

    private final RecipeRepository recipeRepository;

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

    /*
    public List<RecipeInfo> retrieveFridgeRecipes(User user, Integer start, Integer display) {
        return recipeRepository.searchRecipeListOrderByIngredientCntWhichUserHasDesc(UserEntity.fromModel(user), "ACTIVE").stream()
                .skip(start == 0 ? 0 : start - 1)
                .limit(display)
                .collect(Collectors.toList());
    }

    public int countFridgeRecipes(User user) {
        return recipeRepository.searchRecipeListOrderByIngredientCntWhichUserHasDesc(UserEntity.fromModel(user), "ACTIVE").size();
    }

     */
}

