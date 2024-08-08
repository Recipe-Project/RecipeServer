package com.recipe.app.src.recipe.application

import com.recipe.app.src.recipe.domain.RecipeScrap
import com.recipe.app.src.recipe.infra.RecipeScrapRepository
import spock.lang.Specification

class RecipeScrapServiceTest extends Specification {

    private RecipeScrapRepository recipeScrapRepository = Mock()
    private RecipeScrapService recipeScrapService = new RecipeScrapService(recipeScrapRepository)

    def "레시피 스크랩 새로 생성"() {

        given:
        Long userId = 1
        Long recipeId = 1
        recipeScrapRepository.findByUserIdAndRecipeId(userId, recipeId) >> Optional.empty()

        when:
        recipeScrapService.create(userId, recipeId)

        then:
        1 * recipeScrapRepository.save(_)
    }

    def "레시피 스크랩 생성 시 이미 스크랩한 경우 생성하지 않음"() {

        given:
        Long userId = 1
        Long recipeId = 1
        RecipeScrap recipeScrap = RecipeScrap.builder()
                .userId(userId)
                .recipeId(recipeId)
                .build()

        recipeScrapRepository.findByUserIdAndRecipeId(userId, recipeId) >> Optional.of(recipeScrap)

        when:
        recipeScrapService.create(userId, recipeId)

        then:
        0 * recipeScrapRepository.save(recipeScrap)
    }

    def "유저 아이디와 레시피 아이디에 해당하는 레시피 스크랩 제거"() {

        given:
        Long userId = 1
        Long recipeId = 1
        RecipeScrap recipeScrap = RecipeScrap.builder()
                .userId(userId)
                .recipeId(recipeId)
                .build()

        recipeScrapRepository.findByUserIdAndRecipeId(userId, recipeId) >> Optional.of(recipeScrap)

        when:
        recipeScrapService.delete(userId, recipeId)

        then:
        1 * recipeScrapRepository.delete(recipeScrap)
    }

    def "유저 아이디와 레시피 아이디에 해당하는 레시피 스크랩 없는 경우 제거하지 않음"() {

        given:
        Long userId = 1
        Long recipeId = 1
        RecipeScrap recipeScrap = RecipeScrap.builder()
                .userId(userId)
                .recipeId(recipeId)
                .build()

        recipeScrapRepository.findByUserIdAndRecipeId(userId, recipeId) >> Optional.empty()

        when:
        recipeScrapService.delete(userId, recipeId)

        then:
        0 * recipeScrapRepository.delete(recipeScrap)
    }

    def "특정 레시피의 레시피 스크랩 목록 제거"() {

        given:
        Long recipeId = 1
        List<RecipeScrap> recipeScraps = [
                RecipeScrap.builder()
                        .userId(1)
                        .recipeId(recipeId)
                        .build(),
                RecipeScrap.builder()
                        .userId(2)
                        .recipeId(recipeId)
                        .build()
        ]

        recipeScrapRepository.findByRecipeId(recipeId) >> recipeScraps

        when:
        recipeScrapService.deleteAllByRecipeId(recipeId)

        then:
        1 * recipeScrapRepository.deleteAll(recipeScraps)
    }

    def "특정 유저의 레시피 스크랩 수 조회"() {

        given:
        Long userId = 1
        recipeScrapRepository.countByUserId(userId) >> 5

        when:
        long result = recipeScrapService.countByUserId(userId)

        then:
        result == 5
    }

    def "레시피 아이디 목록에 해당하는 레시피 스크랩 목록 조회"() {

        given:
        List<Long> recipeIds = [1L, 2L]
        List<RecipeScrap> recipeScraps = [
                RecipeScrap.builder()
                        .userId(1)
                        .recipeId(1)
                        .build(),
                RecipeScrap.builder()
                        .userId(2)
                        .recipeId(2)
                        .build()
        ]

        recipeScrapRepository.findByRecipeIdIn(recipeIds) >> recipeScraps

        when:
        List<RecipeScrap> result = recipeScrapService.findByRecipeIds(recipeIds)

        then:
        result == recipeScraps
    }

    def "특정 레시피의 레시피 스크랩 수 조회"() {

        given:
        Long recipeId = 1
        recipeScrapRepository.countByRecipeId(recipeId) >> 5

        when:
        long result = recipeScrapService.countByRecipeId(recipeId)

        then:
        result == 5
    }

    def "유저 아이디와 레시피 아이디에 해당하는 레시피 스크랩 조회"() {

        given:
        Long userId = 1
        Long recipeId = 1
        RecipeScrap recipeScrap = RecipeScrap.builder()
                .userId(userId)
                .recipeId(recipeId)
                .build()

        recipeScrapRepository.findByUserIdAndRecipeId(userId, recipeId) >> Optional.of(recipeScrap)

        when:
        RecipeScrap result = recipeScrapService.findByUserIdAndRecipeId(userId, recipeId)

        then:
        result == recipeScrap
    }

    def "유저 아이디와 레시피 아이디에 해당하는 레시피 스크랩 조회 시 없는 경우 null 반환"() {

        given:
        Long userId = 1
        Long recipeId = 1

        recipeScrapRepository.findByUserIdAndRecipeId(userId, recipeId) >> Optional.empty()

        when:
        RecipeScrap result = recipeScrapService.findByUserIdAndRecipeId(userId, recipeId)

        then:
        result == null
    }

    def "유저 아이디와 레시피 아이디로 해당하는 레시피 스크랩 있는지 확인"() {

        given:
        Long userId = 1
        Long recipeId = 1
        recipeScrapRepository.existsByUserIdAndRecipeId(userId, recipeId) >> existsRecipeScrap

        when:
        boolean result = recipeScrapService.existsByUserIdAndRecipeId(userId, recipeId)

        then:
        result == expected

        where:
        existsRecipeScrap || expected
        true              || true
        false             || false
    }

    def "특정 유저의 레시피 스크랩 목록 제거"() {

        given:
        Long userId = 1
        List<RecipeScrap> recipeScraps = [
                RecipeScrap.builder()
                        .userId(userId)
                        .recipeId(1)
                        .build(),
                RecipeScrap.builder()
                        .userId(userId)
                        .recipeId(2)
                        .build()
        ]

        recipeScrapRepository.findByUserId(userId) >> recipeScraps

        when:
        recipeScrapService.deleteAllByUserId(userId)

        then:
        1 * recipeScrapRepository.deleteAll(recipeScraps)
    }
}
