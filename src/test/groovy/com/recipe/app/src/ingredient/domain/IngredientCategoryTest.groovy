package com.recipe.app.src.ingredient.domain

import spock.lang.Specification

class IngredientCategoryTest extends Specification {

    def "재료 카테고리 생성"() {

        given:
        Long ingredientCategoryId = 1
        String ingredientCategoryName = "재료 카테고리"

        when:
        IngredientCategory ingredientCategory = IngredientCategory.builder()
                .ingredientCategoryId(ingredientCategoryId)
                .ingredientCategoryName(ingredientCategoryName)
                .build()

        then:
        ingredientCategory.ingredientCategoryId == ingredientCategoryId
        ingredientCategory.ingredientCategoryName == ingredientCategoryName
    }

    def "재료 카테고리 생성 시 유효하지 않은 요청값인 경우 예외 발생"() {

        when:
        IngredientCategory.builder()
                .ingredientCategoryId(1)
                .ingredientCategoryName(ingredientCategoryName)
                .build()

        then:
        def e = thrown(IllegalArgumentException.class)
        e.message == expected

        where:
        ingredientCategoryName || expected
        null                   || "재료 카테고리명을 입력해주세요."
        ""                     || "재료 카테고리명을 입력해주세요."
    }
}
