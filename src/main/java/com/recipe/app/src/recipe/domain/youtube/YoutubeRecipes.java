package com.recipe.app.src.recipe.domain.youtube;

import java.util.List;

public class YoutubeRecipes {

    private List<YoutubeRecipe> youtubeRecipes;

    public YoutubeRecipes(List<YoutubeRecipe> youtubeRecipes) {
        this.youtubeRecipes = youtubeRecipes;
    }

    public List<YoutubeRecipe> getYoutubeRecipes() {
        return this.youtubeRecipes;
    }
}
