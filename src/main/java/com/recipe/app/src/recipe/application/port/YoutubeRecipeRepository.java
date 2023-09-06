package com.recipe.app.src.recipe.application.port;

import com.recipe.app.src.recipe.domain.YoutubeRecipe;
import com.recipe.app.src.user.domain.User;

import java.util.List;
import java.util.Optional;

public interface YoutubeRecipeRepository {

    List<YoutubeRecipe> getYoutubeRecipes(String keyword);

    Optional<YoutubeRecipe> getYoutubeRecipe(Long youtubeRecipeId);

    void saveYoutubeRecipeView(YoutubeRecipe youtubeRecipe, User user);

    void saveYoutubeRecipeScrap(YoutubeRecipe youtubeRecipe, User user);

    void deleteYoutubeRecipeScrap(YoutubeRecipe youtubeRecipe, User user);

    List<YoutubeRecipe> findYoutubeRecipesByUser(User user);

    List<YoutubeRecipe> saveYoutubeRecipes(List<YoutubeRecipe> youtubeRecipes);
}
