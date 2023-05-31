package com.recipe.app.common.exception;

import com.recipe.app.common.response.BaseResponseStatus;
import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {
    private final BaseResponseStatus status;

    public BaseException(BaseResponseStatus status) {
        super(status.getMessage());
        this.status = status;
    }

    public BaseException(BaseResponseStatus status, String ingredientName) {
        super(status.getMessage(ingredientName));
        this.status = status;
    }
}
