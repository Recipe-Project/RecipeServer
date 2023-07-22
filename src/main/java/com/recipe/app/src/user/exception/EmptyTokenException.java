package com.recipe.app.src.user.exception;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.common.response.BaseResponseStatus;

public class EmptyTokenException extends BaseException {
    public EmptyTokenException() {
        super(BaseResponseStatus.EMPTY_TOKEN);
    }
}
