package com.recipe.app.src.recipe.application.port;

import com.recipe.app.src.recipe.domain.YoutubeRecipe;
import com.recipe.app.src.user.domain.User;

import java.util.List;
import java.util.Optional;

public interface YoutubeRecipeRepository {

    Page<YoutubeRecipe> getYoutubeRecipesOrderByCreatedAtDesc(String keyword, Pageable pageable);

    Page<YoutubeRecipe> getYoutubeRecipesOrderByYoutubeScrapSizeDesc(String keyword, Pageable pageable);

    Page<YoutubeRecipe> getYoutubeRecipesOrderByYoutubeViewSizeDesc(String keyword, Pageable pageable);

    Optional<YoutubeRecipe> getYoutubeRecipe(Long youtubeRecipeId);

    void saveYoutubeRecipeView(YoutubeRecipe youtubeRecipe, User user);

    void saveYoutubeRecipeScrap(YoutubeRecipe youtubeRecipe, User user);

    void deleteYoutubeRecipeScrap(YoutubeRecipe youtubeRecipe, User user);

    Page<YoutubeRecipe> findYoutubeRecipesByUser(User user, Pageable pageable);

    List<YoutubeRecipe> saveYoutubeRecipes(List<YoutubeRecipe> youtubeRecipes);

    long countYoutubeScrapByUser(User user);
}
