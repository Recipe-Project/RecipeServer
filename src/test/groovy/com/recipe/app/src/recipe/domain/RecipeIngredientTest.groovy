package com.recipe.app.src.recipe.domain

import spock.lang.Specification

class RecipeIngredientTest extends Specification {

    def "레시피 재료 생성 성공"() {

        given:
        Recipe recipe = Recipe.builder()
                .recipeNm("제목")
                .introduction("설명")
                .level(RecipeLevel.NORMAL)
                .userId(1L)
                .isHidden(false)
                .build()

        Long recipeIngredientId = 1L
        String ingredientName = "재료"
        Long ingredientIconId = 1L
        String quantity = "1"
        String unit = "개"

        when:
        RecipeIngredient ingredient = RecipeIngredient.builder()
                .recipeIngredientId(recipeIngredientId)
                .recipe(recipe)
                .ingredientName(ingredientName)
                .ingredientIconId(ingredientIconId)
                .quantity(quantity)
                .unit(unit)
                .build()

        then:
        ingredient.recipeIngredientId == recipeIngredientId
        ingredient.recipe == recipe
        ingredient.ingredientName == ingredientName
        ingredient.ingredientIconId == ingredientIconId
        ingredient.quantity == quantity
        ingredient.unit == unit
    }

    def "레시피 재료 생성 시 유효하지 않은 요청값인 경우 예외 발생"() {

        given:
        Recipe recipe = Recipe.builder()
                .recipeNm("제목")
                .introduction("설명")
                .level(RecipeLevel.NORMAL)
                .userId(1L)
                .isHidden(false)
                .build()

        when:
        RecipeIngredient.builder()
                .recipeIngredientId(1L)
                .recipe(recipe)
                .ingredientName(ingredientName)
                .ingredientIconId(1L)
                .quantity("1")
                .unit("개")
                .build()

        then:
        def e = thrown(IllegalArgumentException.class)
        e.message == expected

        where:
        ingredientName || expected
        ""             || "레시피 재료명을 입력해주세요."
        null           || "레시피 재료명을 입력해주세요."
    }

    def "레시피 재료 삭제"() {

        given:
        Recipe recipe = Recipe.builder()
                .recipeNm("제목")
                .introduction("설명")
                .level(RecipeLevel.NORMAL)
                .userId(1L)
                .isHidden(false)
                .build()

        RecipeIngredient ingredient = RecipeIngredient.builder()
                .recipeIngredientId(1L)
                .recipe(recipe)
                .ingredientName("재료")
                .ingredientIconId(1L)
                .quantity("1")
                .unit("개")
                .build()

        when:
        ingredient.delete()

        then:
        ingredient.recipe == null
    }

    def "이름이 일치하는 레시피 재료가 냉장고 존재하는지 확인"() {

        given:

        Recipe recipe = Recipe.builder()
                .recipeNm("제목")
                .introduction("설명")
                .level(RecipeLevel.NORMAL)
                .userId(1L)
                .isHidden(false)
                .build()

        RecipeIngredient ingredient = RecipeIngredient.builder()
                .recipeIngredientId(1L)
                .recipe(recipe)
                .ingredientName(ingredientName)
                .ingredientIconId(1L)
                .quantity("1")
                .unit("개")
                .build()

        List<String> ingredientNamesInFridge = ["돼지고기", "김치"]

        when:
        boolean result = ingredient.hasInFridge(ingredientNamesInFridge)

        then:
        result == expected

        where:
        ingredientName || expected
        "돼지고기"         || true
        "김치"           || true
        "돼지"           || false
        "김"            || false
        "김치찌개"         || false
    }
}
