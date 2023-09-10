package com.recipe.app.src.recipe.application.port;

import com.recipe.app.src.recipe.domain.BlogRecipe;
import com.recipe.app.src.user.domain.User;

import java.util.List;
import java.util.Optional;

public interface BlogRecipeRepository {

    List<BlogRecipe> getBlogRecipes(String keyword);

    Optional<BlogRecipe> getBlogRecipe(Long blogRecipeId);

    void saveBlogRecipeView(BlogRecipe blogRecipe, User user);

    void saveBlogRecipeScrap(BlogRecipe blogRecipe, User user);

    void deleteBlogRecipeScrap(BlogRecipe blogRecipe, User user);

    List<BlogRecipe> findBlogRecipesByUser(User user);

    List<BlogRecipe> saveBlogRecipes(List<BlogRecipe> blogs);

    long countBlogScrapByUser(User user);
}
