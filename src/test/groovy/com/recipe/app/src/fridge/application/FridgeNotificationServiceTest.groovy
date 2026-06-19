package com.recipe.app.src.fridge.application

import com.recipe.app.src.common.utils.FirebaseCloudMessageService
import com.recipe.app.src.fridge.domain.Fridge
import com.recipe.app.src.fridge.infra.FridgeRepository
import com.recipe.app.src.ingredient.application.IngredientService
import com.recipe.app.src.ingredient.domain.Ingredient
import com.recipe.app.src.user.domain.User
import com.recipe.app.src.user.infra.UserRepository
import spock.lang.Specification

import java.time.LocalDate
import java.time.ZoneId

class FridgeNotificationServiceTest extends Specification {

    FridgeRepository fridgeRepository = Mock()
    IngredientService ingredientService = Mock()
    UserRepository userRepository = Mock()
    FirebaseCloudMessageService firebaseCloudMessageService = Mock()

    FridgeNotificationService service = new FridgeNotificationService(
            fridgeRepository, ingredientService, userRepository, firebaseCloudMessageService)

    def kstTargetDate = LocalDate.now(ZoneId.of("Asia/Seoul")).plusDays(3)

    def fridge(long userId, long ingredientId) {
        Stub(Fridge) { getUserId() >> userId; getIngredientId() >> ingredientId }
    }

    def user(long userId, String deviceToken) {
        Stub(User) { getUserId() >> userId; getDeviceToken() >> deviceToken }
    }

    def ingredient(long ingredientId, String name) {
        Stub(Ingredient) { getIngredientId() >> ingredientId; getIngredientName() >> name }
    }

    def "유통기한 3일 전 재료 보유자에게 알림을 발송한다"() {

        when:
        service.notifyExpiringIngredients()

        then: "정확히 today+3(KST) 로 조회"
        1 * fridgeRepository.findByExpiredAt(kstTargetDate) >> [fridge(1L, 10L), fridge(2L, 11L)]
        userRepository.findAllById(_) >> [user(1L, "tokenA"), user(2L, "tokenB")]
        ingredientService.findByIngredientIds(_) >> [ingredient(10L, "양파"), ingredient(11L, "두부")]

        and: "각 재료별로 알림 발송"
        1 * firebaseCloudMessageService.sendMessageTo("tokenA", "유통기한 알림", "양파의 유통기한이 3일 남았습니다.")
        1 * firebaseCloudMessageService.sendMessageTo("tokenB", "유통기한 알림", "두부의 유통기한이 3일 남았습니다.")
    }

    def "deviceToken 이 없는 사용자는 발송 대상에서 제외된다"() {

        when:
        service.notifyExpiringIngredients()

        then:
        1 * fridgeRepository.findByExpiredAt(_) >> [fridge(1L, 10L), fridge(2L, 11L)]
        userRepository.findAllById(_) >> [user(1L, "tokenA"), user(2L, null)]
        ingredientService.findByIngredientIds(_) >> [ingredient(10L, "양파"), ingredient(11L, "두부")]

        and:
        1 * firebaseCloudMessageService.sendMessageTo("tokenA", _, _)
        0 * firebaseCloudMessageService.sendMessageTo({ it == null }, _, _)
    }

    def "대상 재료가 없으면 아무 발송도 하지 않는다"() {

        when:
        service.notifyExpiringIngredients()

        then:
        1 * fridgeRepository.findByExpiredAt(_) >> []
        0 * userRepository.findAllById(_)
        0 * ingredientService.findByIngredientIds(_)
        0 * firebaseCloudMessageService.sendMessageTo(_, _, _)
    }

    def "한 건 발송이 실패해도 나머지는 계속 발송된다"() {

        when:
        service.notifyExpiringIngredients()

        then:
        1 * fridgeRepository.findByExpiredAt(_) >> [fridge(1L, 10L), fridge(2L, 11L)]
        userRepository.findAllById(_) >> [user(1L, "tokenA"), user(2L, "tokenB")]
        ingredientService.findByIngredientIds(_) >> [ingredient(10L, "양파"), ingredient(11L, "두부")]

        and: "tokenA 발송이 IOException 으로 실패해도"
        1 * firebaseCloudMessageService.sendMessageTo("tokenA", _, _) >> { throw new IOException("fcm down") }

        and: "tokenB 는 정상 발송된다"
        1 * firebaseCloudMessageService.sendMessageTo("tokenB", _, _)
    }

    def "테스트 발송: 입력한 토큰으로 발송한다"() {

        when:
        service.sendTestNotification("someToken")

        then:
        1 * firebaseCloudMessageService.sendMessageTo("someToken", _, _)
    }

    def "테스트 발송: 토큰이 비어있으면(#desc) 예외를 던지고 발송하지 않는다"() {

        when:
        service.sendTestNotification(token)

        then:
        thrown(IllegalStateException)
        0 * firebaseCloudMessageService.sendMessageTo(_, _, _)

        where:
        desc      | token
        "null"     | null
        "빈문자열" | ""
        "공백"     | "   "
    }
}
