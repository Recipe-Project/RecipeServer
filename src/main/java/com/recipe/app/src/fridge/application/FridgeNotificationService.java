package com.recipe.app.src.fridge.application;

import com.recipe.app.src.common.utils.FirebaseCloudMessageService;
import com.recipe.app.src.fridge.domain.Fridge;
import com.recipe.app.src.fridge.infra.FridgeRepository;
import com.recipe.app.src.ingredient.application.IngredientService;
import com.recipe.app.src.ingredient.domain.Ingredient;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.user.infra.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 유통기한 임박 재료 푸시 알림 스케줄러.
 *
 * <p>매일 정오(KST)에 유통기한이 {@link #NOTIFY_DAYS_BEFORE_EXPIRY}일 남은 냉장고 재료를 찾아,
 * 해당 재료를 보유한 사용자에게 FCM 푸시 알림을 발송한다.
 * deviceToken 이 없는 사용자(미로그인/탈퇴 등 — 탈퇴 시 deviceToken 이 null 로 비워짐)는 제외한다.
 */
@Service
public class FridgeNotificationService {

    private static final Logger log = LoggerFactory.getLogger(FridgeNotificationService.class);
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private static final int NOTIFY_DAYS_BEFORE_EXPIRY = 3;

    private final FridgeRepository fridgeRepository;
    private final IngredientService ingredientService;
    private final UserRepository userRepository;
    private final FirebaseCloudMessageService firebaseCloudMessageService;

    public FridgeNotificationService(FridgeRepository fridgeRepository, IngredientService ingredientService,
                                     UserRepository userRepository, FirebaseCloudMessageService firebaseCloudMessageService) {
        this.fridgeRepository = fridgeRepository;
        this.ingredientService = ingredientService;
        this.userRepository = userRepository;
        this.firebaseCloudMessageService = firebaseCloudMessageService;
    }

    @Scheduled(cron = "0 0 12 * * *", zone = "Asia/Seoul")
    public void notifyExpiringIngredients() {

        LocalDate targetDate = LocalDate.now(KST).plusDays(NOTIFY_DAYS_BEFORE_EXPIRY);

        List<Fridge> fridges = fridgeRepository.findByExpiredAt(targetDate);
        if (fridges.isEmpty()) {
            return;
        }

        Map<Long, String> deviceTokenByUserId = userRepository.findAllById(
                        fridges.stream().map(Fridge::getUserId).distinct().collect(Collectors.toList()))
                .stream()
                .filter(user -> StringUtils.hasText(user.getDeviceToken()))
                .collect(Collectors.toMap(User::getUserId, User::getDeviceToken));

        Map<Long, String> ingredientNameById = ingredientService.findByIngredientIds(
                        fridges.stream().map(Fridge::getIngredientId).distinct().collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(Ingredient::getIngredientId, Ingredient::getIngredientName));

        int sent = 0;
        for (Fridge fridge : fridges) {

            String deviceToken = deviceTokenByUserId.get(fridge.getUserId());
            String ingredientName = ingredientNameById.get(fridge.getIngredientId());
            if (deviceToken == null || ingredientName == null) {
                continue;
            }

            try {
                firebaseCloudMessageService.sendMessageTo(
                        deviceToken,
                        "유통기한 알림",
                        ingredientName + "의 유통기한이 " + NOTIFY_DAYS_BEFORE_EXPIRY + "일 남았습니다.");
                sent++;
            } catch (IOException e) {
                log.warn("유통기한 알림 발송 실패. userId={}, ingredientName={}", fridge.getUserId(), ingredientName, e);
            }
        }

        log.info("유통기한 알림 발송 완료. 대상 재료={}건, 발송 성공={}건", fridges.size(), sent);
    }

    /**
     * [테스트용] 입력한 디바이스 토큰으로 즉시 테스트 푸시를 발송한다.
     * 유통기한 데이터(D-3) 없이도 FCM 연동/토큰/수신 여부를 확인하기 위한 용도.
     */
    public void sendTestNotification(String deviceToken) {

        if (!StringUtils.hasText(deviceToken)) {
            throw new IllegalStateException("FCM 디바이스 토큰을 입력해주세요.");
        }

        try {
            firebaseCloudMessageService.sendMessageTo(
                    deviceToken,
                    "유통기한 알림 테스트",
                    "테스트 알림입니다. 이 메시지가 보이면 FCM 설정이 정상입니다.");
        } catch (IOException e) {
            throw new IllegalStateException("FCM 발송 실패: " + e.getMessage(), e);
        }
    }
}
