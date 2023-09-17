package com.recipe.app.src.recipe.exception;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.common.response.BaseResponseStatus;

public class NotFoundRecipeException extends BaseException {
    public NotFoundRecipeException() {
        super(BaseResponseStatus.NOT_FOUND_RECIPE_INFO);
    }
}
