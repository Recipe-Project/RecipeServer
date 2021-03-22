package com.recipe.app.src.fridgeBasket.models;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
public class PostFridgesDirectBasketReq {
    private String ingredientName;
    private String ingredientIcon;
    private Integer ingredientCategoryIdx;
}
