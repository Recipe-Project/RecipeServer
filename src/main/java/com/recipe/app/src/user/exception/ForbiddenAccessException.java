package com.recipe.app.src.user.exception;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.common.response.BaseResponseStatus;

public class ForbiddenAccessException extends BaseException {
    public ForbiddenAccessException() {
        super(BaseResponseStatus.FORBIDDEN_ACCESS);
    }
}
