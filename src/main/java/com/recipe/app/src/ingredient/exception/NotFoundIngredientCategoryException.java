package com.recipe.app.src.ingredient.exception;

public class NotFoundIngredientCategoryException extends RuntimeException {

    public NotFoundIngredientCategoryException() {
        super("재료 카테고리인덱스를 찾을 수 없습니다.");
    }
}
