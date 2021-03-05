package com.recipe.app.src.userRecipe.models;




import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class PatchMyRecipeRes {
    private final List photoUrlList;
    private final String thumbnail;
    private final String title;
    private final String content;
}

