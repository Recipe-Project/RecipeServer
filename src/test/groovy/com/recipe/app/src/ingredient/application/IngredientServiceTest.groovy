package com.recipe.app.src.ingredient.application

import com.recipe.app.src.ingredient.application.dto.IngredientCreateResponse
import com.recipe.app.src.ingredient.application.dto.IngredientRequest
import com.recipe.app.src.ingredient.domain.Ingredient
import com.recipe.app.src.ingredient.domain.IngredientCategory
import com.recipe.app.src.ingredient.exception.NotFoundIngredientException
import com.recipe.app.src.ingredient.infra.IngredientRepository
import com.recipe.app.src.user.domain.User
import spock.lang.Specification

class IngredientServiceTest extends Specification {

    private IngredientCategoryService ingredientCategoryService = Mock()
    private IngredientRepository ingredientRepository = Mock()
    private IngredientService ingredientService = new IngredientService(ingredientRepository, ingredientCategoryService)

    def "키워드로 재료 검색"() {

        given:
        Long userId = 1
        String keyword = "재료"

        List<Ingredient> ingredients = [
                Ingredient.builder()
                        .ingredientId(1)
                        .ingredientCategoryId(1)
                        .ingredientName("재료1")
                        .ingredientIconId(1)
                        .userId(1)
                        .build(),
                Ingredient.builder()
                        .ingredientId(2)
                        .ingredientCategoryId(2)
                        .ingredientName("재료2")
                        .ingredientIconId(1)
                        .userId(1)
                        .build()
        ]

        ingredientRepository.findDefaultIngredientsByKeyword(userId, keyword) >> ingredients

        when:
        List<Ingredient> result = ingredientService.findByKeyword(userId, keyword)

        then:
        result == ingredients
    }

    def "재료 아이디 목록 내 재료 목록 조회"() {

        given:
        List<Long> ingredientIds= [1L, 2L]

        List<Ingredient> ingredients = [
                Ingredient.builder()
                        .ingredientId(1)
                        .ingredientCategoryId(1)
                        .ingredientName("재료1")
                        .ingredientIconId(1)
                        .userId(1)
                        .build(),
                Ingredient.builder()
                        .ingredientId(2)
                        .ingredientCategoryId(2)
                        .ingredientName("재료2")
                        .ingredientIconId(1)
                        .userId(1)
                        .build()
        ]

        ingredientRepository.findByIngredientIdIn(ingredientIds) >> ingredients

        when:
        List<Ingredient> result = ingredientService.findByIngredientIds(ingredientIds)

        then:
        result == ingredients
    }

    def "재료 생성"() {

        given:
        IngredientRequest request = IngredientRequest.builder()
                .ingredientCategoryId(1)
                .ingredientName("재료1")
                .ingredientIconId(1)
                .build()

        IngredientCategory category = IngredientCategory.builder()
                .ingredientCategoryId(1)
                .ingredientCategoryName("카테고리1")
                .build()

        ingredientCategoryService.findById(request.ingredientCategoryId) >> category

        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        Ingredient ingredient = Ingredient.builder()
                .ingredientId(1)
                .ingredientCategoryId(1)
                .ingredientName("재료1")
                .ingredientIconId(1)
                .userId(1)
                .build()

        ingredientRepository.findByUserIdAndIngredientNameAndIngredientIconIdAndIngredientCategoryId(
                user.userId,
                request.ingredientName,
                request.ingredientIconId,
                category.ingredientCategoryId) >> Optional.of(ingredient)

        when:
        IngredientCreateResponse result = ingredientService.create(user.userId, request)

        then:
        result.ingredientId == ingredient.ingredientId
    }

    def "특정 유저의 재료 목록 제거"() {

        given:
        Long userId = 1
        List<Ingredient> ingredients =  [
                Ingredient.builder()
                        .ingredientId(1)
                        .ingredientCategoryId(1)
                        .ingredientName("재료1")
                        .ingredientIconId(1)
                        .userId(1)
                        .build(),
                Ingredient.builder()
                        .ingredientId(2)
                        .ingredientCategoryId(2)
                        .ingredientName("재료2")
                        .ingredientIconId(1)
                        .userId(1)
                        .build()
        ]

        ingredientRepository.findByUserId(userId) >> ingredients

        when:
        ingredientService.deleteAllByUserId(userId)

        then:
        1 * ingredientRepository.deleteAll(ingredients)
    }

    def "아이디로 재료 조회"() {

        given:
        Ingredient ingredient = Ingredient.builder()
                .ingredientId(1)
                .ingredientCategoryId(1)
                .ingredientName("재료1")
                .ingredientIconId(1)
                .userId(1)
                .build()

        ingredientRepository.findById(ingredient.ingredientId) >> Optional.of(ingredient)

        when:
        Ingredient result = ingredientService.findByIngredientId(ingredient.ingredientId)

        then:
        result == ingredient
    }

    def "아이디로 재료 조회 시 없는 경우 예외 발생"() {

        given:
        Long ingredientId = 1

        ingredientRepository.findById(ingredientId) >> Optional.empty()

        when:
        ingredientService.findByIngredientId(ingredientId)

        then:
        def e = thrown(NotFoundIngredientException.class)
        e.message == "재료를 찾을 수 없습니다."
    }

    def "재료 삭제"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        Ingredient ingredient = Ingredient.builder()
                .ingredientId(1)
                .ingredientCategoryId(1)
                .ingredientName("재료1")
                .ingredientIconId(1)
                .userId(1)
                .build()

        ingredientRepository.findByIngredientIdAndUserId(ingredient.ingredientId, user.userId) >> Optional.of(ingredient)

        when:
        ingredientService.delete(user, ingredient.ingredientId)

        then:
        1 * ingredientRepository.delete(ingredient)
    }

    def "재료 삭제 시 재료가 존재 하지 않는 경우 실행 안함"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        Long ingredientId = 1

        ingredientRepository.findByIngredientIdAndUserId(ingredientId, user.userId) >> Optional.empty()

        when:
        ingredientService.delete(user, ingredientId)

        then:
        0 * ingredientRepository.delete(_)
    }

    def "특정 유저의 재료 목록 조회"() {

        given:
        Long userId = 1

        List<Ingredient> ingredients =  [
                Ingredient.builder()
                        .ingredientId(1)
                        .ingredientCategoryId(1)
                        .ingredientName("재료1")
                        .ingredientIconId(1)
                        .userId(1)
                        .build(),
                Ingredient.builder()
                        .ingredientId(2)
                        .ingredientCategoryId(2)
                        .ingredientName("재료2")
                        .ingredientIconId(1)
                        .userId(1)
                        .build()
        ]

        ingredientRepository.findByUserId(userId) >> ingredients

        when:
        List<Ingredient> result = ingredientService.findByUserId(userId)

        then:
        result == ingredients
    }
}
