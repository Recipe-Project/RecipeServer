package com.recipe.app.src.recipe.exception;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.common.response.BaseResponseStatus;

public class NotFoundRecipeLevelException extends BaseException {
    public NotFoundRecipeLevelException() {
        super(BaseResponseStatus.NOT_FOUND_RECIPE_LEVEL);
    }
}
