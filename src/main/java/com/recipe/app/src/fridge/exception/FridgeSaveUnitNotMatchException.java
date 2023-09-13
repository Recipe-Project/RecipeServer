package com.recipe.app.src.fridge.exception;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.common.response.BaseResponseStatus;

public class FridgeSaveUnitNotMatchException extends BaseException {

    public FridgeSaveUnitNotMatchException(String ingredientName) {
        super(BaseResponseStatus.FRIDGE_SAVE_UNIT_NOT_MATCH, ingredientName);
    }
}
