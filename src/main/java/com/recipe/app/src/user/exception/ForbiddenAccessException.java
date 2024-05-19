package com.recipe.app.src.user.exception;

public class ForbiddenAccessException extends RuntimeException {

    public ForbiddenAccessException() {
        super("접근 권한이 없습니다.");
    }
}
