package com.recipe.app.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BaseException extends Exception {
    private BaseResponseStatus status;
    private String ingredientName;

    public BaseException(BaseResponseStatus status) {
        this.status = status;
    }
}
