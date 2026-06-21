package com.recipe.app.src.fridge.api;

import com.recipe.app.src.fridge.application.FridgeNotificationService;
import com.recipe.app.src.user.application.dto.UserDeviceTokenRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * [테스트용] 유통기한 알림 FCM 발송 수동 트리거.
 *
 * <p>정오 스케줄/로그인 없이, 입력한 FCM 토큰으로 메시지가 실제 발송되는지 확인하기 위한 용도.
 * 토큰을 직접 받으므로 로그인 불필요(/test/** 는 보안설정상 permitAll).
 * 운영 반영 전 제거하거나 dev 프로파일로 가드하는 것을 권장한다.
 */
@Tag(name = "알림 테스트 Controller")
@Profile("!prod")  // 운영(prod) 프로파일에서는 빈 자체가 등록되지 않아 노출되지 않음
@RestController
@RequestMapping("/test/notifications")
public class FridgeNotificationTestController {

    private final FridgeNotificationService fridgeNotificationService;

    public FridgeNotificationTestController(FridgeNotificationService fridgeNotificationService) {
        this.fridgeNotificationService = fridgeNotificationService;
    }

    @Operation(summary = "[테스트] 입력한 FCM 토큰으로 테스트 푸시 발송 (로그인 불필요)")
    @PostMapping("/fcm")
    public void sendTestNotification(@RequestBody UserDeviceTokenRequest request) {

        fridgeNotificationService.sendTestNotification(request.getFcmToken());
    }
}
