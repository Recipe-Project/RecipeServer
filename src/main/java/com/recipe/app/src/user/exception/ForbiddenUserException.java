package com.recipe.app.src.user.exception;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.common.response.BaseResponseStatus;

public class ForbiddenUserException extends BaseException {
    public ForbiddenUserException() {
        super(BaseResponseStatus.FORBIDDEN_USER);
    }
}
