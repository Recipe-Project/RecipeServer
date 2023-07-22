package com.recipe.app.src.user.exception;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.common.response.BaseResponseStatus;

public class NotFoundUserException extends BaseException {
    public NotFoundUserException() {
        super(BaseResponseStatus.NOT_FOUND_USER);
    }
}
