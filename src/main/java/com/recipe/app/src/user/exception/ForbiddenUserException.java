package com.recipe.app.src.user.exception;

public class ForbiddenUserException extends RuntimeException {

    public ForbiddenUserException() {
        super("해당 정보에 접근할 수 없는 회원입니다.");
    }
}
