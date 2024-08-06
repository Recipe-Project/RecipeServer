package com.recipe.app.src.recipe.domain.youtube;

import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class YoutubeRecipes {

    private final List<YoutubeRecipe> youtubeRecipes;

    public YoutubeRecipes(List<YoutubeRecipe> youtubeRecipes) {
        this.youtubeRecipes = youtubeRecipes;
    }

    public List<Long> getYoutubeRecipeIds() {

        return youtubeRecipes.stream()
                .map(YoutubeRecipe::getYoutubeRecipeId)
                .collect(Collectors.toList());
    }
}
