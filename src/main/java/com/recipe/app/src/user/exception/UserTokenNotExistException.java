package com.recipe.app.src.user.exception;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.common.response.BaseResponseStatus;

public class UserTokenNotExistException extends BaseException {
    public UserTokenNotExistException() {
        super(BaseResponseStatus.INVALID_JWT);
    }
}
