package com.recipe.app.src.userRecipe.models;

import lombok.AccessLevel;



import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
public class PostMyRecipeReq {
    private  List photoUrlList;
    private String thumbnail;
    private String title;
    private String content;
    private List ingredientList;
}