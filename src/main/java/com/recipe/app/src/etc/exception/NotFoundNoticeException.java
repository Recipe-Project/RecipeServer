package com.recipe.app.src.etc.exception;

public class NotFoundNoticeException extends RuntimeException {

    public NotFoundNoticeException() {
        super("공지가 존재하지 않습니다.");
    }
}
