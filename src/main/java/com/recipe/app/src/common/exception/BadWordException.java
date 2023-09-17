package com.recipe.app.src.common.exception;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.common.response.BaseResponseStatus;

public class BadWordException extends BaseException {
    public BadWordException(String word) {
        super(BaseResponseStatus.BAD_WORD_CONTAIN, word);
    }
}
