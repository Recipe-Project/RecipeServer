package com.recipe.app.src.userRecipe.exception;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.common.response.BaseResponseStatus;

public class NotFoundUserRecipeException extends BaseException {
    public NotFoundUserRecipeException() {
        super(BaseResponseStatus.NOT_FOUND_USER_RECIPE);
    }
}
