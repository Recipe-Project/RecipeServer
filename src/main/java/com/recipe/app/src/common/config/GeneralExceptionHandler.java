package com.recipe.app.src.common.config;

import com.recipe.app.src.common.exception.BadWordException;
import com.recipe.app.src.notice.exception.NotFoundNoticeException;
import com.recipe.app.src.fridge.exception.NotFoundFridgeException;
import com.recipe.app.src.fridgeBasket.exception.NotFoundFridgeBasketException;
import com.recipe.app.src.ingredient.exception.NotFoundIngredientCategoryException;
import com.recipe.app.src.ingredient.exception.NotFoundIngredientException;
import com.recipe.app.src.recipe.exception.NotFoundRecipeException;
import com.recipe.app.src.recipe.exception.NotFoundRecipeLevelException;
import com.recipe.app.src.common.utils.DiscordAlertService;
import com.recipe.app.src.user.exception.ForbiddenAccessException;
import com.recipe.app.src.user.exception.NotFoundUserException;
import com.recipe.app.src.user.exception.UserTokenNotExistException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GeneralExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(getClass());

    // DiscordAlertService 는 prod 프로필에서만 빈으로 등록되므로 ObjectProvider 로 안전하게 주입한다.
    private final ObjectProvider<DiscordAlertService> discordAlertServiceProvider;

    public GeneralExceptionHandler(ObjectProvider<DiscordAlertService> discordAlertServiceProvider) {
        this.discordAlertServiceProvider = discordAlertServiceProvider;
    }

    @ExceptionHandler({NotFoundFridgeException.class, NotFoundIngredientException.class, NotFoundFridgeBasketException.class,
            NotFoundIngredientCategoryException.class, NotFoundNoticeException.class, NotFoundRecipeLevelException.class,
            NotFoundRecipeException.class, NotFoundUserException.class})
    public ResponseEntity<?> handleNotFoundException(Exception e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e);
    }

    @ExceptionHandler({UserTokenNotExistException.class, ForbiddenAccessException.class})
    public ResponseEntity<?> handleUnauthorizedException(Exception e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e);
    }

    @ExceptionHandler({BadWordException.class, IllegalArgumentException.class, NullPointerException.class})
    public ResponseEntity<?> handleBadRequestException(Exception e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
    }

    // 위에서 처리되지 않은 모든 예외 = 500. 로그를 남기고 (prod 한정) Discord 로 알림을 보낸다.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleInternalServerError(Exception e, HttpServletRequest request) {
        log.error("Internal Server Error. {} {}", request.getMethod(), request.getRequestURI(), e);

        DiscordAlertService discordAlertService = discordAlertServiceProvider.getIfAvailable();
        if (discordAlertService != null) {
            discordAlertService.sendErrorAlert(request.getMethod(), request.getRequestURI(), e);
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "서버 오류가 발생했습니다."));
    }
}
