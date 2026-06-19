package com.recipe.app.src.fridge.api;

import com.recipe.app.src.common.aop.LoginCheck;
import com.recipe.app.src.fridge.application.FridgeNotificationService;
import com.recipe.app.src.user.application.dto.UserDeviceTokenRequest;
import com.recipe.app.src.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * [테스트용] 유통기한 알림 수동 트리거 컨트롤러.
 *
 * <p>정오 스케줄을 기다리지 않고, 입력한 FCM 토큰으로 메시지가 실제 발송되는지 확인하기 위한 용도.
 * 운영 반영 전 제거하거나 프로파일로 가드하는 것을 권장한다.
 */
@Tag(name = "냉장고 알림 테스트 Controller")
@RestController
@RequestMapping("/fridges/notifications")
public class FridgeNotificationTestController {

    private final FridgeNotificationService fridgeNotificationService;

    public FridgeNotificationTestController(FridgeNotificationService fridgeNotificationService) {
        this.fridgeNotificationService = fridgeNotificationService;
    }

    @Operation(summary = "[테스트] 입력한 FCM 토큰으로 테스트 푸시 발송")
    @PostMapping("/test")
    @LoginCheck
    public void sendTestNotification(@Parameter(hidden = true) User user,
                                     @Parameter(name = "테스트 발송할 FCM 토큰", required = true)
                                     @RequestBody UserDeviceTokenRequest request) {

        fridgeNotificationService.sendTestNotification(request.getFcmToken());
    }
}
