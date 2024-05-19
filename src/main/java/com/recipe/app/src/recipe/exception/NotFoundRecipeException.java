package com.recipe.app.src.recipe.exception;

public class NotFoundRecipeException extends RuntimeException {

    public NotFoundRecipeException() {
        super("레시피 정보를 찾지 못하였습니다.");
    }
}
