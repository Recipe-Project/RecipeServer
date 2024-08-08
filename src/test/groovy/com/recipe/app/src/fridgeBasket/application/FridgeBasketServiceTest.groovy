package com.recipe.app.src.fridgeBasket.application

import com.recipe.app.src.fridgeBasket.application.dto.FridgeBasketCountResponse
import com.recipe.app.src.fridgeBasket.application.dto.FridgeBasketRequest
import com.recipe.app.src.fridgeBasket.application.dto.FridgeBasketsRequest
import com.recipe.app.src.fridgeBasket.application.dto.FridgeBasketsResponse
import com.recipe.app.src.fridgeBasket.domain.FridgeBasket
import com.recipe.app.src.fridgeBasket.exception.NotFoundFridgeBasketException
import com.recipe.app.src.fridgeBasket.infra.FridgeBasketRepository
import com.recipe.app.src.ingredient.application.IngredientCategoryService
import com.recipe.app.src.ingredient.application.IngredientService
import com.recipe.app.src.ingredient.domain.Ingredient
import com.recipe.app.src.ingredient.domain.IngredientCategory
import com.recipe.app.src.user.domain.User
import spock.lang.Specification

import java.time.LocalDate

class FridgeBasketServiceTest extends Specification {

    private FridgeBasketRepository fridgeBasketRepository = Mock()
    private IngredientService ingredientService = Mock()
    private IngredientCategoryService ingredientCategoryService = Mock()
    private FridgeBasketService fridgeBasketService = new FridgeBasketService(fridgeBasketRepository, ingredientService, ingredientCategoryService)

    def "냉장고 바구니 생성"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        List<Long> ingredientIds = [1L, 2L]

        FridgeBasketsRequest request = FridgeBasketsRequest.builder()
                .ingredientIds(ingredientIds)
                .build()

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
                        .build(),
                Ingredient.builder()
                        .ingredientId(3)
                        .ingredientCategoryId(3)
                        .ingredientName("재료3")
                        .ingredientIconId(1)
                        .userId(1)
                        .build()
        ]

        ingredientService.findByIngredientIds(ingredientIds) >> ingredients

        List<FridgeBasket> fridgeBaskets = [
                FridgeBasket.builder()
                        .fridgeBasketId(1)
                        .userId(user.userId)
                        .ingredientId(1)
                        .expiredAt(null)
                        .quantity(1.5)
                        .unit("개")
                        .build(),
                FridgeBasket.builder()
                        .fridgeBasketId(1)
                        .userId(user.userId)
                        .ingredientId(3)
                        .expiredAt(null)
                        .quantity(1.5)
                        .unit("개")
                        .build()
        ]

        fridgeBasketRepository.findByUserId(user.userId) >> fridgeBaskets

        when:
        fridgeBasketService.create(user, request)

        then:
        1 * fridgeBasketRepository.saveAll(_)
    }

    def "냉장고 바구니 삭제"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        FridgeBasket fridgeBasket = FridgeBasket.builder()
                .fridgeBasketId(1)
                .userId(user.userId)
                .ingredientId(1)
                .expiredAt(null)
                .quantity(1.5)
                .unit("개")
                .build()

        fridgeBasketRepository.findByUserIdAndFridgeBasketId(user.userId, fridgeBasket.fridgeBasketId) >> Optional.of(fridgeBasket)

        when:
        fridgeBasketService.delete(user, fridgeBasket.fridgeBasketId)

        then:
        1 * fridgeBasketRepository.delete(fridgeBasket)
    }

    def "냉장고 바구니 삭제 시 냉장고 바구니 존재하지 않으면 예외 발생"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        Long fridgeBasketId = 1

        fridgeBasketRepository.findByUserIdAndFridgeBasketId(user.userId, fridgeBasketId) >> Optional.empty()

        when:
        fridgeBasketService.delete(user, fridgeBasketId)

        then:
        def e = thrown(NotFoundFridgeBasketException.class)
        e.message == "냉장고 바구니 조회에 실패했습니다."
    }

    def "냉장고 바구니 수정"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        FridgeBasket fridgeBasket = FridgeBasket.builder()
                .fridgeBasketId(1)
                .userId(user.userId)
                .ingredientId(1)
                .expiredAt(null)
                .quantity(1.5)
                .unit("개")
                .build()

        LocalDate updateExpiredAt = LocalDate.now()
        float updateQuantity = 3
        String updateUnit = ""

        FridgeBasketRequest request = FridgeBasketRequest.builder()
                .expiredAt(updateExpiredAt)
                .quantity(updateQuantity)
                .unit(updateUnit)
                .build()

        fridgeBasketRepository.findByUserIdAndFridgeBasketId(user.userId, fridgeBasket.fridgeBasketId) >> Optional.of(fridgeBasket)

        when:
        fridgeBasketService.update(user, fridgeBasket.fridgeBasketId, request)

        then:
        1 * fridgeBasketRepository.save(fridgeBasket)
        fridgeBasket.expiredAt == updateExpiredAt
        fridgeBasket.quantity == updateQuantity
        fridgeBasket.unit == updateUnit
    }

    def "냉장고 바구니 수정 시 냉장고 바구니 존재하지 않으면 예외 발생"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        FridgeBasket fridgeBasket = FridgeBasket.builder()
                .fridgeBasketId(1)
                .userId(user.userId)
                .ingredientId(1)
                .expiredAt(null)
                .quantity(1.5)
                .unit("개")
                .build()

        LocalDate updateExpiredAt = LocalDate.now()
        float updateQuantity = 3
        String updateUnit = ""

        FridgeBasketRequest request = FridgeBasketRequest.builder()
                .expiredAt(updateExpiredAt)
                .quantity(updateQuantity)
                .unit(updateUnit)
                .build()

        fridgeBasketRepository.findByUserIdAndFridgeBasketId(user.userId, fridgeBasket.fridgeBasketId) >> Optional.empty()

        when:
        fridgeBasketService.update(user, fridgeBasket.fridgeBasketId, request)

        then:
        def e = thrown(NotFoundFridgeBasketException.class)
        e.message == "냉장고 바구니 조회에 실패했습니다."
    }

    def "특정 유저의 냉장고 바구니 갯수 조회"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        fridgeBasketRepository.countByUserId(user.userId) >> 5

        when:
        FridgeBasketCountResponse result = fridgeBasketService.countFridgeBasketByUser(user)

        then:
        result.fridgesBasketCount == 5
    }

    def "특정 유저의 냉장고 바구니 목록 조회"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        List<FridgeBasket> fridgeBaskets = [
                FridgeBasket.builder()
                        .fridgeBasketId(1)
                        .userId(user.userId)
                        .ingredientId(1)
                        .expiredAt(null)
                        .quantity(1.5)
                        .unit("개")
                        .build(),
                FridgeBasket.builder()
                        .fridgeBasketId(2)
                        .userId(user.userId)
                        .ingredientId(2)
                        .expiredAt(null)
                        .quantity(1.5)
                        .unit("개")
                        .build()
        ]

        fridgeBasketRepository.findByUserId(user.userId) >> fridgeBaskets

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

        ingredientService.findByIngredientIds(fridgeBaskets.ingredientId) >> ingredients

        List<IngredientCategory> categories = [
                IngredientCategory.builder()
                        .ingredientCategoryId(1)
                        .ingredientCategoryName("카테고리1")
                        .build(),
                IngredientCategory.builder()
                        .ingredientCategoryId(2)
                        .ingredientCategoryName("카테고리2")
                        .build(),
                IngredientCategory.builder()
                        .ingredientCategoryId(3)
                        .ingredientCategoryName("카테고리3")
                        .build()
        ]

        ingredientCategoryService.findAll() >> categories

        when:
        FridgeBasketsResponse result = fridgeBasketService.findAllByUser(user)

        then:
        result.fridgeBasketCount == fridgeBaskets.size()
        result.ingredientCategories.get(0).ingredientCategoryId == 1
        result.ingredientCategories.get(0).ingredientCategoryName == "카테고리1"
        result.ingredientCategories.get(0).fridgeBaskets.fridgeBasketId == [1L]
        result.ingredientCategories.get(1).ingredientCategoryId == 2
        result.ingredientCategories.get(1).ingredientCategoryName == "카테고리2"
        result.ingredientCategories.get(1).fridgeBaskets.fridgeBasketId == [2L]
        result.ingredientCategories.get(2).ingredientCategoryId == 3
        result.ingredientCategories.get(2).ingredientCategoryName == "카테고리3"
        result.ingredientCategories.get(2).fridgeBaskets.fridgeBasketId == []
    }

    def "특정 유저의 냉장고 바구니 목록 제거"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        List<FridgeBasket> fridgeBaskets = [
                FridgeBasket.builder()
                        .fridgeBasketId(1)
                        .userId(user.userId)
                        .ingredientId(1)
                        .expiredAt(null)
                        .quantity(1.5)
                        .unit("개")
                        .build(),
                FridgeBasket.builder()
                        .fridgeBasketId(2)
                        .userId(user.userId)
                        .ingredientId(2)
                        .expiredAt(null)
                        .quantity(1.5)
                        .unit("개")
                        .build()
        ]

        fridgeBasketRepository.findByUserId(user.userId) >> fridgeBaskets

        when:
        fridgeBasketService.deleteAllByUserId(user.userId)

        then:
        1 * fridgeBasketRepository.deleteAll(fridgeBaskets)
    }

    def "유저 아이디와 재료 아이디로 특정 냉장고 바구니 삭제"() {

        given:
        Long userId = 1
        Long ingredientId = 1

        FridgeBasket fridgeBasket = FridgeBasket.builder()
                .fridgeBasketId(1)
                .userId(userId)
                .ingredientId(ingredientId)
                .expiredAt(null)
                .quantity(1.5)
                .unit("개")
                .build()

        fridgeBasketRepository.findByIngredientIdAndUserId(ingredientId, userId) >> Optional.of(fridgeBasket)

        when:
        fridgeBasketService.deleteByUserIdAndIngredientId(userId, ingredientId)

        then:
        1 * fridgeBasketRepository.delete(fridgeBasket)
    }

    def "유저 아이디와 재료 아이디로 특정 냉장고 바구니 삭제 시 존재 하지 않는 경우 실행 안함"() {

        given:
        Long userId = 1
        Long ingredientId = 1

        fridgeBasketRepository.findByIngredientIdAndUserId(ingredientId, userId) >> Optional.empty()

        when:
        fridgeBasketService.deleteByUserIdAndIngredientId(userId, ingredientId)

        then:
        0 * fridgeBasketRepository.delete(_)
    }
}
