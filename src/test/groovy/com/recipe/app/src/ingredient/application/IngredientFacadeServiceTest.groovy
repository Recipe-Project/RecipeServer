package com.recipe.app.src.ingredient.application

import com.recipe.app.src.fridge.application.FridgeService
import com.recipe.app.src.fridgeBasket.application.FridgeBasketService
import com.recipe.app.src.ingredient.application.dto.IngredientsResponse
import com.recipe.app.src.ingredient.domain.Ingredient
import com.recipe.app.src.ingredient.domain.IngredientCategory
import com.recipe.app.src.user.domain.User
import spock.lang.Specification

class IngredientFacadeServiceTest extends Specification {

    private IngredientService ingredientService = Mock()
    private IngredientCategoryService ingredientCategoryService = Mock()
    private FridgeBasketService fridgeBasketService = Mock()
    private FridgeService fridgeService = Mock()
    private IngredientFacadeService ingredientFacadeService = new IngredientFacadeService(ingredientService,
            ingredientCategoryService, fridgeBasketService, fridgeService)

    def "키워드로 재료 검색"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        fridgeBasketService.countByUserId(user.userId) >> 2

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

        List<Ingredient> ingredients = [
                Ingredient.builder()
                        .ingredientId(1)
                        .ingredientCategoryId(1)
                        .ingredientName("재료1")
                        .ingredientIconId(1)
                        .userId(user.userId)
                        .build(),
                Ingredient.builder()
                        .ingredientId(2)
                        .ingredientCategoryId(2)
                        .ingredientName("재료2")
                        .ingredientIconId(1)
                        .userId(user.userId)
                        .build(),
        ]

        String keyword = "재료"

        ingredientService.findByKeyword(user.userId, keyword) >> ingredients

        when:
        IngredientsResponse result = ingredientFacadeService.findIngredientsByKeyword(user, keyword)

        then:
        result.fridgeBasketCount == 2
        result.ingredientCategories.ingredientCategoryId == categories.ingredientCategoryId
        result.ingredientCategories.ingredientCategoryName == categories.ingredientCategoryName
        result.ingredientCategories.get(0).ingredients.ingredientId == [ingredients.get(0).ingredientId]
        result.ingredientCategories.get(0).ingredients.ingredientName == [ingredients.get(0).ingredientName]
        result.ingredientCategories.get(0).ingredients.ingredientIconId == [ingredients.get(0).ingredientIconId]
        result.ingredientCategories.get(1).ingredients.ingredientId == [ingredients.get(1).ingredientId]
        result.ingredientCategories.get(1).ingredients.ingredientName == [ingredients.get(1).ingredientName]
        result.ingredientCategories.get(1).ingredients.ingredientIconId == [ingredients.get(1).ingredientIconId]
        result.ingredientCategories.get(2).ingredients.ingredientId == []
        result.ingredientCategories.get(2).ingredients.ingredientName == []
        result.ingredientCategories.get(2).ingredients.ingredientIconId == []
    }

    def "나만의 재료 목록 조회"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        fridgeBasketService.countByUserId(user.userId) >> 2

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

        List<Ingredient> ingredients = [
                Ingredient.builder()
                        .ingredientId(1)
                        .ingredientCategoryId(1)
                        .ingredientName("재료1")
                        .ingredientIconId(1)
                        .userId(user.userId)
                        .build(),
                Ingredient.builder()
                        .ingredientId(2)
                        .ingredientCategoryId(2)
                        .ingredientName("재료2")
                        .ingredientIconId(1)
                        .userId(user.userId)
                        .build(),
        ]

        ingredientService.findByUserId(user.userId) >> ingredients

        when:
        IngredientsResponse result = ingredientFacadeService.findIngredientsByUser(user)

        then:
        result.fridgeBasketCount == 2
        result.ingredientCategories.ingredientCategoryId == categories.ingredientCategoryId
        result.ingredientCategories.ingredientCategoryName == categories.ingredientCategoryName
        result.ingredientCategories.get(0).ingredients.ingredientId == [ingredients.get(0).ingredientId]
        result.ingredientCategories.get(0).ingredients.ingredientName == [ingredients.get(0).ingredientName]
        result.ingredientCategories.get(0).ingredients.ingredientIconId == [ingredients.get(0).ingredientIconId]
        result.ingredientCategories.get(1).ingredients.ingredientId == [ingredients.get(1).ingredientId]
        result.ingredientCategories.get(1).ingredients.ingredientName == [ingredients.get(1).ingredientName]
        result.ingredientCategories.get(1).ingredients.ingredientIconId == [ingredients.get(1).ingredientIconId]
        result.ingredientCategories.get(2).ingredients.ingredientId == []
        result.ingredientCategories.get(2).ingredients.ingredientName == []
        result.ingredientCategories.get(2).ingredients.ingredientIconId == []
    }

    def "재료 삭제"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        Long ingredientId = 1

        when:
        ingredientFacadeService.deleteIngredient(user, ingredientId)

        then:
        fridgeService.deleteByUserIdAndIngredientId(user.getUserId(), ingredientId);
        fridgeBasketService.deleteByUserIdAndIngredientId(user.getUserId(), ingredientId);
        ingredientService.delete(user, ingredientId)
    }
}
