package com.recipe.app.src.user.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MypageMyRecipeList {
    private final Integer myRecipeIdx;
    private final String myRecipeThumbnail;
}
