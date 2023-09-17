package com.recipe.app.src.fridgeBasket.exception;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.common.response.BaseResponseStatus;

public class NotFoundFridgeBasketException extends BaseException {
    public NotFoundFridgeBasketException() {
        super(BaseResponseStatus.NOT_FOUND_FRIDGE_BASKET);
    }
}
