package com.recipe.app.src.recipe.application

import com.recipe.app.src.recipe.domain.RecipeReport
import com.recipe.app.src.recipe.infra.RecipeReportRepository
import spock.lang.Specification

class RecipeReportServiceTest extends Specification {

    private RecipeReportRepository recipeReportRepository = Mock()
    private RecipeReportService recipeReportService = new RecipeReportService(recipeReportRepository)

    def "레시피 신고 생성"() {

        given:
        Long userId = 1
        Long recipeId = 1
        recipeReportRepository.findByUserIdAndRecipeId(userId, recipeId) >> Optional.empty()

        when:
        recipeReportService.createRecipeReport(userId, recipeId)

        then:
        1 * recipeReportRepository.save(_)
    }

    def "레시피 신고 생성 시 이미 생성된 경우 새로 생성하지 않음"() {

        given:
        Long userId = 1
        Long recipeId = 1
        RecipeReport recipeReport = RecipeReport.builder()
                .reportId(1)
                .userId(userId)
                .recipeId(recipeId)
                .build()

        recipeReportRepository.findByUserIdAndRecipeId(userId, recipeId) >> Optional.of(recipeReport)

        when:
        recipeReportService.createRecipeReport(userId, recipeId)

        then:
        0 * recipeReportRepository.save(recipeReport)
    }

    def "신고 횟수에 따라 신고된 레시피인지 여부 확인"() {

        given:
        Long recipeId = 1
        recipeReportRepository.countByRecipeId(recipeId) >> count

        when:
        boolean result = recipeReportService.isRecipeReported(recipeId)

        then:
        result == expected

        where:
        count || expected
        4     || false
        5     || true
        6     || true
    }

    def "특정 레시피의 레시피 신고 목록 제거"() {

        given:
        Long recipeId = 1
        List<RecipeReport> recipeReports = [
                RecipeReport.builder()
                        .reportId(1)
                        .userId(1)
                        .recipeId(recipeId)
                        .build(),
                RecipeReport.builder()
                        .reportId(2)
                        .userId(2)
                        .recipeId(recipeId)
                        .build()
        ]
        recipeReportRepository.findByRecipeId(recipeId) >> recipeReports

        when:
        recipeReportService.deleteAllByRecipeId(recipeId)

        then:
        1 * recipeReportRepository.deleteAll(recipeReports)
    }
}
