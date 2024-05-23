package com.recipe.app.src.fridge.exception;

public class FridgeSaveExpiredDateNotMatchException extends RuntimeException {

    public FridgeSaveExpiredDateNotMatchException(String ingredientName) {
        super(String.format("냉장고에 존재하는 재료(%s)의 유통 기한과 냉장고 바구니의 재료 유통 기한이 일치하지 않습니다", ingredientName));
    }
}
