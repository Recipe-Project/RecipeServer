package com.recipe.app.src.ingredient.domain

import spock.lang.Specification

class IngredientTest extends Specification {

    def "재료 생성"() {

        given:
        Long ingredientId = 1
        Long ingredientCategoryId = 1
        String ingredientName = "재료"
        Long ingredientIconId = 1
        Long userId = 1

        when:
        Ingredient ingredient = Ingredient.builder()
                .ingredientId(ingredientId)
                .ingredientCategoryId(ingredientCategoryId)
                .ingredientName(ingredientName)
                .ingredientIconId(ingredientIconId)
                .userId(userId)
                .build()

        then:
        ingredient.ingredientId == ingredientId
        ingredient.ingredientCategoryId == ingredientCategoryId
        ingredient.ingredientName == ingredientName
        ingredient.ingredientIconId == ingredientIconId
        ingredient.userId == userId
    }

    def "재료 생성 시 요청값이 null인 경우 예외 발생"() {

        when:
        Ingredient.builder()
                .ingredientId(1)
                .ingredientCategoryId(null)
                .ingredientName("재료")
                .ingredientIconId(1)
                .userId(1)
                .build()

        then:
        def e = thrown(NullPointerException.class)
        e.message == "재료 카테고리 아이디를 입력해주세요."
    }

    def "재료 생성 시 유효하지 않은 요청값인 경우 예외 발생"() {

        when:
        Ingredient.builder()
                .ingredientId(1)
                .ingredientCategoryId(1)
                .ingredientName(ingredientName)
                .ingredientIconId(1)
                .userId(1)
                .build()

        then:
        def e = thrown(IllegalArgumentException.class)
        e.message == expected

        where:
        ingredientName || expected
        null           || "재료명을 입력해주세요."
        ""             || "재료명을 입력해주세요."
    }
}
