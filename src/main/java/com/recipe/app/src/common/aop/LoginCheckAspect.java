package com.recipe.app.src.common.aop;

import com.recipe.app.src.user.domain.SecurityUser;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.user.exception.UserTokenNotExistException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
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

        if (authentication == null || !(authentication.getPrincipal() instanceof SecurityUser)) {
            throw new UserTokenNotExistException();
        }

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        log.info("Login User Id : " + user.getUserId());

        Object[] args = new Object[]{user};

        System.out.println(Arrays.toString(args));

        return proceedingJoinPoint.proceed(args);
    }
}
