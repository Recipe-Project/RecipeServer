package com.recipe.app.src.fridge.exception;

public class FridgeSaveUnitNotMatchException extends RuntimeException {

    public FridgeSaveUnitNotMatchException(String ingredientName) {
        super(String.format("냉장고에 존재하는 재료(%s)의 단위와 냉장고 바구니의 재료 단위가 일치하지 않습니다", ingredientName));
    }
}
