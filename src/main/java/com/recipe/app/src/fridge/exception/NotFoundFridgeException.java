package com.recipe.app.src.fridge.exception;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.common.response.BaseResponseStatus;

public class NotFoundFridgeException extends BaseException {

    public NotFoundFridgeException() {
        super(BaseResponseStatus.NOT_FOUND_FRIDGE);
    }
}
