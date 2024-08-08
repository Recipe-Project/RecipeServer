package com.recipe.app.src.fridge.domain


import spock.lang.Specification

import java.time.LocalDate

class FridgeTest extends Specification {

    def "냉장고 생성"() {

        given:
        Long fridgeId = 1
        Long userId = 1
        Long ingredientId = 1
        LocalDate expiredAt = null
        float quantity = 1.5
        String unit = "개"

        when:
        Fridge fridge = Fridge.builder()
                .fridgeId(fridgeId)
                .userId(userId)
                .ingredientId(ingredientId)
                .expiredAt(expiredAt)
                .quantity(quantity)
                .unit(unit)
                .build()

        then:
        fridge.fridgeId == fridgeId
        fridge.userId == userId
        fridge.ingredientId == ingredientId
        fridge.expiredAt == expiredAt
        fridge.quantity == quantity
        fridge.unit == unit
    }

    def "냉장고 생성 시 요청값이 null 인 경우 예외 발생"() {

        when:
        Fridge.builder()
                .fridgeId(1)
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

    def "냉장고 수량 증가"() {

        given:
        Long fridgeId = 1
        Long userId = 1
        Long ingredientId = 1
        LocalDate expiredAt = null
        float quantity = 1.5
        String unit = "개"

        Fridge fridge = Fridge.builder()
                .fridgeId(fridgeId)
                .userId(userId)
                .ingredientId(ingredientId)
                .expiredAt(expiredAt)
                .quantity(quantity)
                .unit(unit)
                .build()

        float plusQuantity = 4

        when:
        fridge.plusQuantity(plusQuantity)

        then:
        fridge.quantity == quantity + plusQuantity
    }

    def "냉장고 수정"() {

        given:
        Long fridgeId = 1
        Long userId = 1
        Long ingredientId = 1
        LocalDate expiredAt = null
        float quantity = 1.5
        String unit = "개"

        Fridge fridge = Fridge.builder()
                .fridgeId(fridgeId)
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
        fridge.update(updateExpiredAt, updateQuantity, updateUnit)

        then:
        fridge.expiredAt == updateExpiredAt
        fridge.quantity == updateQuantity
        fridge.unit == updateUnit
    }
}
