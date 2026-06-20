package com.recipe.app.src.common.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LoginCheck {

    /**
     * true(기본): 인증 없으면 예외.
     * false: 인증 없어도 통과시키되 컨트롤러의 User 파라미터에 null 을 주입(선택적 로그인).
     */
    boolean required() default true;
}
