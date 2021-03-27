package com.recipe.app.src.receipt.models;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
public class ReceiptIngredientList {
    private String ingredientName;
    private String ingredientIcon;
    private Integer ingredientCategoryIdx;
    private String storageMethod;
    private String expiredAt;
    private Integer count;

}
