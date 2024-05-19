package com.recipe.app.src.ingredient.exception;

public class NotFoundIngredientException extends RuntimeException {

    public NotFoundIngredientException() {
        super("재료를 찾을 수 없습니다.");
    }
}
