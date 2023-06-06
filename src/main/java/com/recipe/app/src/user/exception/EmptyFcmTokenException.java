package com.recipe.app.src.user.exception;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.common.response.BaseResponseStatus;

public class EmptyFcmTokenException extends BaseException {
    public EmptyFcmTokenException() {
        super(BaseResponseStatus.EMPTY_FCM_TOKEN);
    }
}
