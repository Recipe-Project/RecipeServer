package com.recipe.app.src.recipe.domain

import spock.lang.Specification

class RecipeScrapTest extends Specification {

    def "레시피 스크랩 정보 생성"() {

        given:
        Long recipeScrapId = 1L
        Long userId = 1L
        Long recipeId = 1L

        when:
        RecipeScrap recipeScrap = RecipeScrap.builder()
                .recipeScrapId(recipeScrapId)
                .userId(userId)
                .recipeId(recipeId)
                .build()

        then:
        recipeScrap.recipeScrapId == recipeScrapId
        recipeScrap.userId == userId
        recipeScrap.recipeId == recipeId
    }

    def "레시피 스크랩 정보 생성 시 null 인 요청값인 경우 예외 발생"() {

        when:
        RecipeScrap.builder()
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
