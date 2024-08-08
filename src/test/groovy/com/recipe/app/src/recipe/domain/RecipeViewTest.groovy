package com.recipe.app.src.recipe.domain

import spock.lang.Specification

class RecipeViewTest extends Specification {

    def "레시피 조회 정보 생성"() {

        given:
        Long recipeViewId = 1L
        Long userId = 1L
        Long recipeId = 1L

        when:
        RecipeView recipeView = RecipeView.builder()
                .recipeViewId(recipeViewId)
                .userId(userId)
                .recipeId(recipeId)
                .build()

        then:
        recipeView.recipeViewId == recipeViewId
        recipeView.userId == userId
        recipeView.recipeId == recipeId
    }

    def "레시피 조회 정보 생성 시 null 인 요청값인 경우 예외 발생"() {

        when:
        RecipeView.builder()
                .userId(userId)
                .recipeId(recipeId)
                .build()

        then:
        def e = thrown(NullPointerException.class)
        e.message == expected

        where:
        userId | recipeId || expected
        null   | 1L       || "유저 아이디를 입력해주세요."
        null   | null     || "유저 아이디를 입력해주세요."
        1L     | null     || "레시피 아이디를 입력해주세요."
    }
}
