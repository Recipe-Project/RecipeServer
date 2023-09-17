package com.recipe.app.src.recipe.application.port;

import com.recipe.app.src.recipe.domain.BlogRecipe;
import com.recipe.app.src.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface BlogRecipeRepository {

    Page<BlogRecipe> getBlogRecipesOrderByCreatedAtDesc(String keyword, Pageable pageable);

    Page<BlogRecipe> getBlogRecipesOrderByBlogScrapSizeDesc(String keyword, Pageable pageable);

    Page<BlogRecipe> getBlogRecipesOrderByBlogViewSizeDesc(String keyword, Pageable pageable);

    Optional<BlogRecipe> getBlogRecipe(Long blogRecipeId);

    void saveBlogRecipeView(BlogRecipe blogRecipe, User user);

    void saveBlogRecipeScrap(BlogRecipe blogRecipe, User user);

    void deleteBlogRecipeScrap(BlogRecipe blogRecipe, User user);

    Page<BlogRecipe> findBlogRecipesByUser(User user, Pageable pageable);

    List<BlogRecipe> saveBlogRecipes(List<BlogRecipe> blogs);

    long countBlogScrapByUser(User user);

    List<BlogRecipe> findBlogRecipesByBlogUrlIn(List<String> blogUrls);
}
