package com.recipe.app.src.ingredient.exception;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.common.response.BaseResponseStatus;

public class NotFoundIngredientCategoryException extends BaseException {
    public NotFoundIngredientCategoryException() {
        super(BaseResponseStatus.NOT_FOUND_INGREDIENT_CATEGORY);
    }
}
