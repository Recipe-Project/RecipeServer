package com.recipe.app.src.common.exception;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.common.response.BaseResponseStatus;

public class NotFoundNoticeException extends BaseException {
    public NotFoundNoticeException() {
        super(BaseResponseStatus.NOT_FOUND_NOTICE);
    }
}
