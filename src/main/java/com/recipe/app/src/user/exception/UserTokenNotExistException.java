package com.recipe.app.src.user.exception;

public class UserTokenNotExistException extends RuntimeException {

    public UserTokenNotExistException() {
        super("유효하지 않은 JWT입니다.");
    }
}
