package com.recipe.app.src.ingredient.models;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
public class PostIngredientsReq {
    private List ingredientList;
}
