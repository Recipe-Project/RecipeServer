package com.recipe.app.src.recipe.application

import com.recipe.app.src.recipe.domain.RecipeView
import com.recipe.app.src.recipe.infra.RecipeViewRepository
import spock.lang.Specification

class RecipeViewServiceTest extends Specification {

    private RecipeViewRepository recipeViewRepository = Mock()
    private RecipeViewService recipeViewService = new RecipeViewService(recipeViewRepository)

    def "레시피 조회 정보 새로 생성"() {

        given:
        Long userId = 1
        Long recipeId = 1
        recipeViewRepository.findByUserIdAndRecipeId(userId, recipeId) >> Optional.empty()

        when:
        recipeViewService.create(userId, recipeId)

        then:
        1 * recipeViewRepository.save(_)
    }

    def "레시피 조회 정보 생성 시 이미 조회한 경우에는 생성하지 않음"() {

        given:
        Long userId = 1
        Long recipeId = 1
        RecipeView recipeView = RecipeView.builder()
                .userId(userId)
                .recipeId(recipeId)
                .build()

        recipeViewRepository.findByUserIdAndRecipeId(userId, recipeId) >> Optional.of(recipeView)

        when:
        recipeViewService.create(userId, recipeId)

        then:
        0 * recipeViewRepository.save(recipeView)
    }

    def "특정 레시피의 레시피 조회 목록 제거"() {

        given:
        Long recipeId = 1
        List<RecipeView> recipeViews = [
                RecipeView.builder()
                        .userId(1)
                        .recipeId(recipeId)
                        .build(),
                RecipeView.builder()
                        .userId(2)
                        .recipeId(recipeId)
                        .build()
        ]

        recipeViewRepository.findByRecipeId(recipeId) >> recipeViews

        when:
        recipeViewService.deleteAllByRecipeId(recipeId)

        then:
        1 * recipeViewRepository.deleteAll(recipeViews)
    }

    def "특정 레시피의 레시피 조회 정보 목록 가져오기"() {

        given:
        Long recipeId = 1
        List<RecipeView> recipeViews = [
                RecipeView.builder()
                        .userId(1)
                        .recipeId(recipeId)
                        .build(),
                RecipeView.builder()
                        .userId(2)
                        .recipeId(recipeId)
                        .build()
        ]

        recipeViewRepository.findByRecipeId(recipeId) >> recipeViews

        when:
        List<RecipeView> result = recipeViewService.findByRecipeId(recipeId)

        then:
        result == recipeViews
    }

    def "특정 레시피의 레시피 조회 수 가져오기"() {

        given:
        Long recipeId = 1
        recipeViewRepository.countByRecipeId(recipeId) >> 5

        when:
        long result = recipeViewService.countByRecipeId(recipeId)

        then:
        result == 5
    }

    def "특정 유저의 레시피 조회 목록 제거"() {

        given:
        Long userId = 1
        List<RecipeView> recipeViews = [
                RecipeView.builder()
                        .userId(userId)
                        .recipeId(1)
                        .build(),
                RecipeView.builder()
                        .userId(userId)
                        .recipeId(2)
                        .build()
        ]

        recipeViewRepository.findByUserId(userId) >> recipeViews

        when:
        recipeViewService.deleteAllByUserId(userId)

        then:
        1 * recipeViewRepository.deleteAll(recipeViews)
    }
}
