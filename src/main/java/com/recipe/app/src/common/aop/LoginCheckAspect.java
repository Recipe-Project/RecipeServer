package com.recipe.app.src.common.aop;

import com.recipe.app.src.user.domain.SecurityUser;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.user.exception.UserTokenNotExistException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Slf4j
@Aspect
public class LoginCheckAspect {

    @Around("@annotation(com.recipe.app.src.common.aop.LoginCheck)")
    public Object loginCheck(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean authenticated = authentication != null && authentication.getPrincipal() instanceof SecurityUser;

        if (!authenticated) {
            LoginCheck loginCheck = ((MethodSignature) proceedingJoinPoint.getSignature()).getMethod().getAnnotation(LoginCheck.class);
            if (loginCheck.required()) {
                throw new UserTokenNotExistException();
            }
            // 선택적 로그인: 비로그인 요청은 User 파라미터를 null 로 채워 통과시킨다.
            return proceedingJoinPoint.proceed(replaceUserArg(proceedingJoinPoint, null));
        }

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        log.info("Login User Id : " + user.getUserId());

        return proceedingJoinPoint.proceed(replaceUserArg(proceedingJoinPoint, user));
    }

    private Object[] replaceUserArg(ProceedingJoinPoint proceedingJoinPoint, User user) {
        return Arrays.stream(proceedingJoinPoint.getArgs())
                .map(arg -> arg instanceof User ? user : arg)
                .toArray();
    }
}
