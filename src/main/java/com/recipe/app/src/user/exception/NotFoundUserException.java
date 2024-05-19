package com.recipe.app.src.user.exception;

public class NotFoundUserException extends RuntimeException {

    public NotFoundUserException() {
        super("존재하지 않는 회원입니다.");
    }
}
