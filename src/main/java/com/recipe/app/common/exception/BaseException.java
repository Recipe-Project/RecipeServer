package com.recipe.app.common.exception;

import com.recipe.app.common.response.BaseResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BaseException extends RuntimeException {
    private BaseResponseStatus status;
    private String ingredientName;

    public BaseException(BaseResponseStatus status) {
        this.status = status;
    }
}
