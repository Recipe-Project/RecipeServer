package com.recipe.app.src.etc.exception;

public class BadWordException extends RuntimeException {

    public BadWordException(String word) {
        super(String.format("금칙어 설정된 단어(%s)를 포함하고 있습니다.", word));
    }
}
