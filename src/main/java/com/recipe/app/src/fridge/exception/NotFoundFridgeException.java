package com.recipe.app.src.fridge.exception;

public class NotFoundFridgeException extends RuntimeException {

    public NotFoundFridgeException() {
        super("냉장고 조회에 실패했습니다.");
    }
}
