package com.recipe.app.src.recipe.application.port;

import com.recipe.app.src.recipe.domain.*;
import com.recipe.app.src.user.domain.User;
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

    List<BlogRecipe> getBlogRecipes(String keyword);

    Optional<BlogRecipe> getBlogRecipe(Long blogRecipeId);

    void saveBlogRecipeView(BlogRecipe blogRecipe, User user);

    void saveBlogRecipeScrap(BlogRecipe blogRecipe, User user);

    void deleteBlogRecipeScrap(BlogRecipe blogRecipe, User user);

    List<BlogRecipe> findBlogRecipesByUser(User user);

    List<YoutubeRecipe> getYoutubeRecipes(String keyword);

    Optional<YoutubeRecipe> getYoutubeRecipe(Long youtubeRecipeId);

    void saveYoutubeRecipeView(YoutubeRecipe youtubeRecipe, User user);

    void saveYoutubeRecipeScrap(YoutubeRecipe youtubeRecipe, User user);

    void deleteYoutubeRecipeScrap(YoutubeRecipe youtubeRecipe, User user);

    List<YoutubeRecipe> findYoutubeRecipesByUser(User user);

    List<BlogRecipe> saveBlogRecipes(List<BlogRecipe> blogs);

    List<YoutubeRecipe> saveYoutubeRecipes(List<YoutubeRecipe> youtubeRecipes);

    List<Recipe> findByUser(User user);

    void delete(Recipe recipe);

    Recipe save(Recipe recipe);

    void saveRecipeProcess(RecipeProcess recipeProcess);

    void saveRecipeIngredients(List<RecipeIngredient> recipeIngredients);

    RecipeProcess findRecipeProcessByRecipe(Recipe recipe);

    void deleteRecipeIngredients(List<RecipeIngredient> recipeIngredients);
}
