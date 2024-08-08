package com.recipe.app.src.fridge.application

import com.recipe.app.src.fridge.application.dto.FridgeRequest
import com.recipe.app.src.fridge.application.dto.FridgeResponse
import com.recipe.app.src.fridge.application.dto.FridgesResponse
import com.recipe.app.src.fridge.domain.Freshness
import com.recipe.app.src.fridge.domain.Fridge
import com.recipe.app.src.fridge.exception.NotFoundFridgeException
import com.recipe.app.src.fridge.infra.FridgeRepository
import com.recipe.app.src.fridgeBasket.application.FridgeBasketService
import com.recipe.app.src.fridgeBasket.domain.FridgeBasket
import com.recipe.app.src.ingredient.application.IngredientCategoryService
import com.recipe.app.src.ingredient.application.IngredientService
import com.recipe.app.src.ingredient.domain.Ingredient
import com.recipe.app.src.ingredient.domain.IngredientCategory
import com.recipe.app.src.user.domain.User
import spock.lang.Specification

import java.time.LocalDate

class FridgeServiceTest extends Specification {

    private FridgeRepository fridgeRepository = Mock()
    private FridgeBasketService fridgeBasketService = Mock()
    private IngredientService ingredientService = Mock()
    private IngredientCategoryService ingredientCategoryService = Mock()
    private FridgeService fridgeService = new FridgeService(fridgeRepository, fridgeBasketService, ingredientService, ingredientCategoryService)

    def "냉장고 생성"() {

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

        fridgeBasketService.findByUserId(user.userId) >> fridgeBaskets

        List<Fridge> fridges = [
                Fridge.builder()
                        .fridgeId(1)
                        .userId(user.userId)
                        .ingredientId(3)
                        .expiredAt(null)
                        .quantity(1.5)
                        .unit("개")
                        .build(),
                Fridge.builder()
                        .fridgeId(2)
                        .userId(user.userId)
                        .ingredientId(4)
                        .expiredAt(null)
                        .quantity(1.5)
                        .unit("개")
                        .build()
        ]

        fridgeRepository.findByUserId(user.userId) >> fridges

        when:
        fridgeService.create(user)

        then:
        1 * fridgeRepository.saveAll(_) >> { args ->

            List<Fridge> savedFridges = args.get(0) as List<Fridge>

            assert savedFridges.get(0).fridgeId == null
            assert savedFridges.get(0).userId == user.userId
            assert savedFridges.get(0).ingredientId == 1
            assert savedFridges.get(0).expiredAt == null
            assert savedFridges.get(0).quantity == 1.5f
            assert savedFridges.get(0).unit == "개"
            assert savedFridges.get(1).fridgeId == null
            assert savedFridges.get(1).userId == user.userId
            assert savedFridges.get(1).ingredientId == 2
            assert savedFridges.get(1).expiredAt == null
            assert savedFridges.get(1).quantity == 1.5f
            assert savedFridges.get(1).unit == "개"
        }
        1 * fridgeBasketService.deleteAll(_)
    }

    def "냉장고 생성 시 이미 같은 재료가 존재하는 경우 단위와 유통기한 일치할 때 수량 증가"() {

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

        fridgeBasketService.findByUserId(user.userId) >> fridgeBaskets

        List<Fridge> fridges = [
                Fridge.builder()
                        .fridgeId(1)
                        .userId(user.userId)
                        .ingredientId(1)
                        .expiredAt(null)
                        .quantity(1.5)
                        .unit("개")
                        .build(),
                Fridge.builder()
                        .fridgeId(2)
                        .userId(user.userId)
                        .ingredientId(2)
                        .expiredAt(null)
                        .quantity(1.5)
                        .unit("개")
                        .build()
        ]

        fridgeRepository.findByUserId(user.userId) >> fridges

        when:
        fridgeService.create(user)

        then:
        1 * fridgeRepository.saveAll(_) >> { args ->

            List<Fridge> savedFridges = args.get(0) as List<Fridge>

            assert savedFridges.get(0).fridgeId == 1
            assert savedFridges.get(0).userId == user.userId
            assert savedFridges.get(0).ingredientId == 1
            assert savedFridges.get(0).expiredAt == null
            assert savedFridges.get(0).quantity == 3f
            assert savedFridges.get(0).unit == "개"
            assert savedFridges.get(1).fridgeId == 2
            assert savedFridges.get(1).userId == user.userId
            assert savedFridges.get(1).ingredientId == 2
            assert savedFridges.get(1).expiredAt == null
            assert savedFridges.get(1).quantity == 3f
            assert savedFridges.get(1).unit == "개"
        }
        1 * fridgeBasketService.deleteAll(_)
    }

    def "냉장고 생성 시 이미 같은 재료가 존재하는 경우 단위나 유통기한 일치하지 않을 때 기존 데이터 삭제 후 새로운 데이터 추가"() {

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
                        .expiredAt(fridgeBasketExpiredAt)
                        .quantity(1.5)
                        .unit(fridgeBasketUnit)
                        .build(),
                FridgeBasket.builder()
                        .fridgeBasketId(2)
                        .userId(user.userId)
                        .ingredientId(2)
                        .expiredAt(fridgeBasketExpiredAt)
                        .quantity(1.5)
                        .unit(fridgeBasketUnit)
                        .build()
        ]

        fridgeBasketService.findByUserId(user.userId) >> fridgeBaskets

        List<Fridge> fridges = [
                Fridge.builder()
                        .fridgeId(1)
                        .userId(user.userId)
                        .ingredientId(1)
                        .expiredAt(fridgeExpiredAt)
                        .quantity(1.5)
                        .unit(fridgeUnit)
                        .build(),
                Fridge.builder()
                        .fridgeId(2)
                        .userId(user.userId)
                        .ingredientId(2)
                        .expiredAt(fridgeExpiredAt)
                        .quantity(1.5)
                        .unit(fridgeUnit)
                        .build()
        ]

        fridgeRepository.findByUserId(user.userId) >> fridges

        when:
        fridgeService.create(user)

        then:
        2 * fridgeRepository.delete(_)
        1 * fridgeRepository.saveAll(_) >> { args ->

            List<Fridge> savedFridges = args.get(0) as List<Fridge>

            assert savedFridges.get(0).fridgeId == null
            assert savedFridges.get(0).userId == user.userId
            assert savedFridges.get(0).ingredientId == 1
            assert savedFridges.get(0).expiredAt == fridgeBasketExpiredAt
            assert savedFridges.get(0).quantity == 1.5f
            assert savedFridges.get(0).unit == fridgeBasketUnit
            assert savedFridges.get(1).fridgeId == null
            assert savedFridges.get(1).userId == user.userId
            assert savedFridges.get(1).ingredientId == 2
            assert savedFridges.get(1).expiredAt == fridgeBasketExpiredAt
            assert savedFridges.get(1).quantity == 1.5f
            assert savedFridges.get(1).unit == fridgeBasketUnit
        }
        1 * fridgeBasketService.deleteAll(_)

        where:
        fridgeExpiredAt            | fridgeUnit | fridgeBasketExpiredAt | fridgeBasketUnit
        null                       | null       | null                  | ""
        null                       | "개"        | null                  | "접시"
        LocalDate.of(2024, 10, 10) | null       | null                  | null
        LocalDate.of(2024, 10, 10) | "개"        | null                  | "개"
        LocalDate.of(2024, 10, 10) | "개"        | null                  | "접시"
    }

    def "냉장고 삭제"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        Fridge fridge = Fridge.builder()
                .fridgeId(1)
                .userId(user.userId)
                .ingredientId(3)
                .expiredAt(null)
                .quantity(1.5)
                .unit("개")
                .build()

        fridgeRepository.findByUserIdAndFridgeId(user.userId, fridge.fridgeId) >> Optional.of(fridge)

        when:
        fridgeService.delete(user, fridge.fridgeId)

        then:
        1 * fridgeRepository.delete(fridge)
    }

    def "냉장고 삭제 시 냉장고 존재하지 않으면 예외 발생"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        Long fridgeId = 1

        fridgeRepository.findByUserIdAndFridgeId(user.userId, fridgeId) >> Optional.empty()

        when:
        fridgeService.delete(user, fridgeId)

        then:
        def e = thrown(NotFoundFridgeException.class)
        e.message == "냉장고 조회에 실패했습니다."
    }

    def "냉장고 수정"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        Fridge fridge = Fridge.builder()
                .fridgeId(1)
                .userId(user.userId)
                .ingredientId(3)
                .expiredAt(null)
                .quantity(1.5)
                .unit("개")
                .build()

        LocalDate updateExpiredAt = LocalDate.now()
        float updateQuantity = 3
        String updateUnit = ""

        FridgeRequest request = FridgeRequest.builder()
                .expiredAt(updateExpiredAt)
                .quantity(updateQuantity)
                .unit(updateUnit)
                .build()

        fridgeRepository.findByUserIdAndFridgeId(user.userId, fridge.fridgeId) >> Optional.of(fridge)

        when:
        fridgeService.update(user, fridge.fridgeId, request)

        then:
        1 * fridgeRepository.save(fridge)
        fridge.expiredAt == updateExpiredAt
        fridge.quantity == updateQuantity
        fridge.unit == updateUnit
    }

    def "냉장고 수정 시 냉장고 존재하지 않으면 예외 발생"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        Long fridgeId = 1

        LocalDate updateExpiredAt = LocalDate.now()
        float updateQuantity = 3
        String updateUnit = ""

        FridgeRequest request = FridgeRequest.builder()
                .expiredAt(updateExpiredAt)
                .quantity(updateQuantity)
                .unit(updateUnit)
                .build()


        fridgeRepository.findByUserIdAndFridgeId(user.userId, fridgeId) >> Optional.empty()

        when:
        fridgeService.update(user, fridgeId, request)

        then:
        def e = thrown(NotFoundFridgeException.class)
        e.message == "냉장고 조회에 실패했습니다."
    }

    def "특정 유저의 냉장고 목록 조회"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        fridgeBasketService.countByUserId(user.userId) >> 5

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

        List<Fridge> fridges = [
                Fridge.builder()
                        .fridgeId(1)
                        .userId(user.userId)
                        .ingredientId(1)
                        .expiredAt(null)
                        .quantity(1.5)
                        .unit("개")
                        .build(),
                Fridge.builder()
                        .fridgeId(2)
                        .userId(user.userId)
                        .ingredientId(2)
                        .expiredAt(null)
                        .quantity(1.5)
                        .unit("개")
                        .build()
        ]

        fridgeRepository.findByUserId(user.userId) >> fridges

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

        ingredientService.findByIngredientIds(fridges.ingredientId) >> ingredients

        when:
        FridgesResponse result = fridgeService.getFridges(user)

        then:
        result.fridgeBasketCount == 5
        result.fridgeIngredientCategories.get(0).ingredientCategoryId == 1
        result.fridgeIngredientCategories.get(0).ingredientCategoryName == "카테고리1"
        result.fridgeIngredientCategories.get(0).fridges.fridgeId == [1L]
        result.fridgeIngredientCategories.get(1).ingredientCategoryId == 2
        result.fridgeIngredientCategories.get(1).ingredientCategoryName == "카테고리2"
        result.fridgeIngredientCategories.get(1).fridges.fridgeId == [2L]
        result.fridgeIngredientCategories.get(2).ingredientCategoryId == 3
        result.fridgeIngredientCategories.get(2).ingredientCategoryName == "카테고리3"
        result.fridgeIngredientCategories.get(2).fridges.fridgeId == []
    }

    def "아이디로 냉장고 조회"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        Fridge fridge = Fridge.builder()
                .fridgeId(1)
                .userId(user.userId)
                .ingredientId(3)
                .expiredAt(null)
                .quantity(1.5)
                .unit("개")
                .build()

        fridgeRepository.findByUserIdAndFridgeId(user.userId, fridge.fridgeId) >> Optional.of(fridge)

        Ingredient ingredient = Ingredient.builder()
                .ingredientId(1)
                .ingredientCategoryId(1)
                .ingredientName("재료1")
                .ingredientIconId(1)
                .userId(1)
                .build()

        ingredientService.findByIngredientId(fridge.ingredientId) >> ingredient

        when:
        FridgeResponse result = fridgeService.getFridge(user, fridge.fridgeId)

        then:
        result.fridgeId == fridge.fridgeId
        result.ingredientName == ingredient.ingredientName
        result.ingredientIconId == ingredient.ingredientIconId
        result.expiredAt == fridge.expiredAt
        result.quantity == fridge.quantity
        result.unit == fridge.unit
        result.freshness == Freshness.getFreshnessByExpiredAt(fridge.expiredAt)
    }

    def "특정 유저의 냉장고 목록 제거"() {

        given:
        Long userId = 1

        List<Fridge> fridges = [
                Fridge.builder()
                        .fridgeId(1)
                        .userId(userId)
                        .ingredientId(1)
                        .expiredAt(null)
                        .quantity(1.5)
                        .unit("개")
                        .build(),
                Fridge.builder()
                        .fridgeId(2)
                        .userId(userId)
                        .ingredientId(2)
                        .expiredAt(null)
                        .quantity(1.5)
                        .unit("개")
                        .build()
        ]

        fridgeRepository.findByUserId(userId) >> fridges

        when:
        fridgeService.deleteAllByUserId(userId)

        then:
        1 * fridgeRepository.deleteAll(fridges)
    }

    def "냉장고에 든 재료명 목록 조회"() {

        given:
        Long userId = 1

        List<Fridge> fridges = [
                Fridge.builder()
                        .fridgeId(1)
                        .userId(userId)
                        .ingredientId(1)
                        .expiredAt(null)
                        .quantity(1.5)
                        .unit("개")
                        .build(),
                Fridge.builder()
                        .fridgeId(2)
                        .userId(userId)
                        .ingredientId(2)
                        .expiredAt(null)
                        .quantity(1.5)
                        .unit("개")
                        .build()
        ]

        fridgeRepository.findByUserId(userId) >> fridges

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

        ingredientService.findByIngredientIds(fridges.ingredientId) >> ingredients

        when:
        List<String> result = fridgeService.findIngredientNamesInFridge(userId)

        then:
        result == ["재료1", "재료2"]
    }

    def "유저 아이디와 재료 아이디로 냉장고 삭제"() {

        given:
        Long userId = 1
        Long ingredientId = 1

        Fridge fridge = Fridge.builder()
                .fridgeId(1)
                .userId(userId)
                .ingredientId(3)
                .expiredAt(null)
                .quantity(1.5)
                .unit("개")
                .build()

        fridgeRepository.findByUserIdAndIngredientId(userId, ingredientId) >> Optional.of(fridge)

        when:
        fridgeService.deleteByUserIdAndIngredientId(userId, ingredientId)

        then:
        1 * fridgeRepository.delete(fridge)
    }

    def "유저 아이디와 재료 아이디로 냉장고 삭제 시 존재하지 않는 경우 실행 안함"() {

        given:
        Long userId = 1
        Long ingredientId = 1

        fridgeRepository.findByUserIdAndIngredientId(userId, ingredientId) >> Optional.empty()

        when:
        fridgeService.deleteByUserIdAndIngredientId(userId, ingredientId)

        then:
        0 * fridgeRepository.delete(_)
    }
}
