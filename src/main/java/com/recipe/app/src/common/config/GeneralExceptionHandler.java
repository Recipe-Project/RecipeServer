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
import com.recipe.app.src.common.utils.ScanAttackMonitor;
import com.recipe.app.src.user.exception.ForbiddenAccessException;
import com.recipe.app.src.user.exception.NotFoundUserException;
import com.recipe.app.src.user.exception.UserTokenNotExistException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.util.StringUtils;
import org.springframework.web.ErrorResponse;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;

@ControllerAdvice
public class GeneralExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(getClass());

    // DiscordAlertService 는 prod 프로필에서만 빈으로 등록되므로 ObjectProvider 로 안전하게 주입한다.
    private final ObjectProvider<DiscordAlertService> discordAlertServiceProvider;
    private final ScanAttackMonitor scanAttackMonitor;

    public GeneralExceptionHandler(ObjectProvider<DiscordAlertService> discordAlertServiceProvider,
                                   ScanAttackMonitor scanAttackMonitor) {
        this.discordAlertServiceProvider = discordAlertServiceProvider;
        this.scanAttackMonitor = scanAttackMonitor;
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

    // 존재하지 않는 경로 요청(취약점 스캐너/봇 등). 404 로 조용히 처리하고 알림은 보내지 않는다.
    // 대량 공격 관찰용으로 IP·경로는 WARN 으로 남긴다.
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<?> handleNoResourceFound(NoResourceFoundException e, HttpServletRequest request) {
        String clientIp = clientIp(request);
        log.warn("No static resource. {} {} from {}", request.getMethod(), request.getRequestURI(), clientIp);
        scanAttackMonitor.record(clientIp);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // 방화벽(StrictHttpFirewall)이 막은 비정상 요청(잘못된 파라미터/경로 등). 400 으로 처리하고 알림은 보내지 않는다.
    @ExceptionHandler(RequestRejectedException.class)
    public ResponseEntity<?> handleRequestRejected(RequestRejectedException e, HttpServletRequest request) {
        String clientIp = clientIp(request);
        log.warn("Request rejected by firewall. {} {} from {} - {}", request.getMethod(), request.getRequestURI(), clientIp, e.getMessage());
        scanAttackMonitor.record(clientIp);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "잘못된 요청입니다."));
    }

    // Spring MVC 표준 예외(파라미터 누락·타입 불일치·미지원 메서드 등)는 예외가 가진 상태코드(400/405/415…)를
    // 그대로 내려준다. 클라이언트 잘못이므로 500 으로 뭉개지 않고 알림도 보내지 않는다.
    // ErrorResponse 는 인터페이스(Throwable 아님)라 @ExceptionHandler 로 직접 잡을 수 없어,
    // 실제 예외 타입을 나열해 잡고 상태코드는 런타임에 ErrorResponse 로 읽는다.
    @ExceptionHandler({
            ServletRequestBindingException.class,
            MethodArgumentTypeMismatchException.class,
            MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class,
            HttpRequestMethodNotSupportedException.class,
            HttpMediaTypeNotSupportedException.class,
            HttpMediaTypeNotAcceptableException.class
    })
    public ResponseEntity<?> handleSpringMvcException(Exception e, HttpServletRequest request) {
        HttpStatusCode status = (e instanceof ErrorResponse er) ? er.getStatusCode() : HttpStatus.BAD_REQUEST;
        log.warn("{} {} {} from {} - {}", status.value(), request.getMethod(), request.getRequestURI(),
                clientIp(request), e.getMessage());
        return ResponseEntity.status(status).body(Map.of("message", "잘못된 요청입니다."));
    }

    // 위에서 처리되지 않은 모든 예외 = 500. 로그를 남기고 (webhook 설정 시) Discord 로 알림을 보낸다.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleInternalServerError(Exception e, HttpServletRequest request) {
        String clientIp = clientIp(request);
        log.error("Internal Server Error. {} {} from {}", request.getMethod(), request.getRequestURI(), clientIp, e);

        DiscordAlertService discordAlertService = discordAlertServiceProvider.getIfAvailable();
        if (discordAlertService != null) {
            discordAlertService.sendErrorAlert(request.getMethod(), request.getRequestURI(), clientIp, e);
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "서버 오류가 발생했습니다."));
    }

    // 프록시(nginx/ELB) 뒤에 있을 수 있으므로 X-Forwarded-For 를 우선 확인한다.
    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(forwarded)) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
