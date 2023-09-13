package com.recipe.app.src.fridge.exception;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.common.response.BaseResponseStatus;

public class FridgeSaveExpiredDateNotMatchException extends BaseException {

    public FridgeSaveExpiredDateNotMatchException(String ingredientName) {
        super(BaseResponseStatus.FRIDGE_SAVE_EXPIRED_DATE_NOT_MATCH, ingredientName);
    }
}
