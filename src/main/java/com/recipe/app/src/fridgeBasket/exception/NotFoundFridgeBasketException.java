package com.recipe.app.src.fridgeBasket.exception;

public class NotFoundFridgeBasketException extends RuntimeException {

    public NotFoundFridgeBasketException() {
        super("냉장고 바구니 조회에 실패했습니다.");
    }
}
