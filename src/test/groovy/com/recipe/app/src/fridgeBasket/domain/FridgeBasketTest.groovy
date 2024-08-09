package com.recipe.app.src.fridgeBasket.domain

import com.recipe.app.src.fridge.domain.Fridge
import spock.lang.Specification

import java.time.LocalDate

class FridgeBasketTest extends Specification {

    def "냉장고 바구니 생성"() {

        given:
        Long fridgeBasketId = 1
        Long userId = 1
        Long ingredientId = 1
        LocalDate expiredAt = null
        float quantity = 1.5
        String unit = "개"

        when:
        FridgeBasket fridgeBasket = FridgeBasket.builder()
                .fridgeBasketId(fridgeBasketId)
                .userId(userId)
                .ingredientId(ingredientId)
                .expiredAt(expiredAt)
                .quantity(quantity)
                .unit(unit)
                .build()

        then:
        fridgeBasket.fridgeBasketId == fridgeBasketId
        fridgeBasket.userId == userId
        fridgeBasket.ingredientId == ingredientId
        fridgeBasket.expiredAt == expiredAt
        fridgeBasket.quantity == quantity
        fridgeBasket.unit == unit
    }

    def "냉장고 바구니 생성 시 요청값이 null 인 경우 예외 발생"() {

        when:
        FridgeBasket.builder()
                .fridgeBasketId(1)
                .userId(userId)
                .ingredientId(ingredientId)
                .expiredAt(null)
                .quantity(1.5)
                .unit("개")
                .build()

        then:
        def e = thrown(NullPointerException.class)
        e.message == expected

        where:
        userId | ingredientId || expected
        null   | 1            || "유저 아이디를 입력해주세요."
        null   | null         || "유저 아이디를 입력해주세요."
        1      | null         || "재료 아이디를 입력해주세요."
    }

    def "냉장고 바구니 수량 증가"() {

        given:
        Long fridgeBasketId = 1
        Long userId = 1
        Long ingredientId = 1
        LocalDate expiredAt = null
        float quantity = 1.5
        String unit = "개"

        FridgeBasket fridgeBasket = FridgeBasket.builder()
                .fridgeBasketId(fridgeBasketId)
                .userId(userId)
                .ingredientId(ingredientId)
                .expiredAt(expiredAt)
                .quantity(quantity)
                .unit(unit)
                .build()

        float plusQuantity = 4

        when:
        fridgeBasket.plusQuantity(plusQuantity)

        then:
        fridgeBasket.quantity == quantity + plusQuantity
    }

    def "냉장고 바구니 수정"() {

        given:
        Long fridgeBasketId = 1
        Long userId = 1
        Long ingredientId = 1
        LocalDate expiredAt = null
        float quantity = 1.5
        String unit = "개"

        FridgeBasket fridgeBasket = FridgeBasket.builder()
                .fridgeBasketId(fridgeBasketId)
                .userId(userId)
                .ingredientId(ingredientId)
                .expiredAt(expiredAt)
                .quantity(quantity)
                .unit(unit)
                .build()

        LocalDate updateExpiredAt = LocalDate.now()
        float updateQuantity = 3
        String updateUnit = ""

        when:
        fridgeBasket.update(updateExpiredAt, updateQuantity, updateUnit)

        then:
        fridgeBasket.expiredAt == updateExpiredAt
        fridgeBasket.quantity == updateQuantity
        fridgeBasket.unit == updateUnit
    }

    def "냉장고 바구니 정보로 냉장고 생성"() {

        given:
        Long fridgeBasketId = 1
        Long userId = 1
        Long ingredientId = 1
        LocalDate expiredAt = null
        float quantity = 1.5
        String unit = "개"

        FridgeBasket fridgeBasket = FridgeBasket.builder()
                .fridgeBasketId(fridgeBasketId)
                .userId(userId)
                .ingredientId(ingredientId)
                .expiredAt(expiredAt)
                .quantity(quantity)
                .unit(unit)
                .build()

        when:
        Fridge result = fridgeBasket.toFridge()

        then:
        result.userId == fridgeBasket.userId
        result.ingredientId == fridgeBasket.ingredientId
        result.expiredAt == fridgeBasket.expiredAt
        result.quantity == fridgeBasket.quantity
        result.unit == fridgeBasket.unit
    }
}
