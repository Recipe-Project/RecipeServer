package com.recipe.app.src.recipe.exception;

public class NotFoundScrapException extends RuntimeException {

    public NotFoundScrapException() {
        super("레시피 스크랩 정보를 찾을 수 없습니다.");
    }
}
