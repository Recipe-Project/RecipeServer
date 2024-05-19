package com.recipe.app.src.recipe.exception;

public class NotFoundRecipeLevelException extends RuntimeException {

    public NotFoundRecipeLevelException() {
        super("레시피 난이도 조회에 실패했습니다.");
    }
}
