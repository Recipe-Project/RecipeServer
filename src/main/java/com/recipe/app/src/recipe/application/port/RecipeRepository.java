package com.recipe.app.src.recipe.application.port;

import com.recipe.app.src.ingredient.domain.Ingredient;
import com.recipe.app.src.recipe.domain.*;
import com.recipe.app.src.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository {
    Optional<Recipe> findById(Long recipeId);

    Page<Recipe> getRecipesOrderByCreatedAtDesc(String keyword, Pageable pageable);

    Page<Recipe> getRecipesOrderByRecipeScrapSizeDesc(String keyword, Pageable pageable);

    Page<Recipe> getRecipesOrderByRecipeViewSizeDesc(String keyword, Pageable pageable);

    void saveRecipeScrap(Recipe recipe, User user);

    void deleteRecipeScrap(Recipe recipe, User user);

    Page<Recipe> findScrapRecipesByUser(User user, Pageable pageable);

    void saveRecipeView(Recipe recipe, User user);

    Page<Recipe> findByUser(User user, Pageable pageable);

    void delete(Recipe recipe);

    Recipe save(Recipe recipe);

    void saveRecipeProcess(RecipeProcess recipeProcess);

    void saveRecipeIngredients(List<RecipeIngredient> recipeIngredients);

    List<RecipeProcess> findRecipeProcessesByRecipe(Recipe recipe);

    void deleteRecipeIngredients(List<RecipeIngredient> recipeIngredients);

    List<Recipe> findRecipesOrderByFridgeIngredientCntDesc(List<Ingredient> ingredients, Pageable pageable);

    long countRecipeScrapByUser(User user);

    List<RecipeIngredient> findRecipeIngredientsByRecipe(Recipe recipe);

    List<RecipeIngredient> findRecipeIngredientsByRecipeIn(List<Recipe> recipes);
}
