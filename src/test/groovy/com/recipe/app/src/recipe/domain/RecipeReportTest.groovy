package com.recipe.app.src.recipe.domain

import spock.lang.Specification

class RecipeReportTest extends Specification {

    def "레시피 신고 생성"() {

        given:
        Long reportId = 1L
        Long userId = 1L
        Long recipeId = 1L

        when:
        RecipeReport report = RecipeReport.builder()
                .reportId(reportId)
                .userId(userId)
                .recipeId(recipeId)
                .build()

        then:
        report.reportId == reportId
        report.userId == userId
        report.recipeId == recipeId
    }

    def "레시피 신고 생성 시 null인 요청 값인 경우 예외 발생"() {

        when:
        RecipeReport.builder()
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
