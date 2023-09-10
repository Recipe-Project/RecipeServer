package com.recipe.app.src.recipe.application.port;

import com.recipe.app.src.ingredient.domain.Ingredient;
import com.recipe.app.src.recipe.domain.*;
import com.recipe.app.src.user.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository {
    Optional<Recipe> findById(Long recipeId);

    List<Recipe> getRecipes(String keyword);

    void saveRecipeScrap(Recipe recipe, User user);

    void deleteRecipeScrap(Recipe recipe, User user);

    List<Recipe> findScrapRecipesByUser(User user);

    void saveRecipeView(Recipe recipe, User user);

    List<Recipe> findByUser(User user);

    void delete(Recipe recipe);

    Recipe save(Recipe recipe);

    void saveRecipeProcess(RecipeProcess recipeProcess);

    void saveRecipeIngredients(List<RecipeIngredient> recipeIngredients);

    RecipeProcess findRecipeProcessByRecipe(Recipe recipe);

    void deleteRecipeIngredients(List<RecipeIngredient> recipeIngredients);

    List<Recipe> findRecipesOrderByFridgeIngredientCntDesc(List<Ingredient> ingredients, Pageable pageable);

    long countRecipeScrapByUser(User user);
}
