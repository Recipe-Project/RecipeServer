package com.recipe.app.src.ingredient.application

import com.recipe.app.src.ingredient.domain.IngredientCategory
import com.recipe.app.src.ingredient.exception.NotFoundIngredientCategoryException
import com.recipe.app.src.ingredient.infra.IngredientCategoryRepository
import spock.lang.Specification

class IngredientCategoryServiceTest extends Specification {

    private IngredientCategoryRepository ingredientCategoryRepository = Mock()
    private IngredientCategoryService ingredientCategoryService = new IngredientCategoryService(ingredientCategoryRepository)

    def "모든 카테고리 조회"() {

        given:
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

        ingredientCategoryRepository.findAll() >> categories

        when:
        List<IngredientCategory> result = ingredientCategoryService.findAll()

        then:
        result == categories
    }

    def "아이디로 재료 카테고리 조회"() {

        given:
        Long ingredientCategoryId = 1

        IngredientCategory category = IngredientCategory.builder()
                .ingredientCategoryId(ingredientCategoryId)
                .ingredientCategoryName("카테고리1")
                .build()

        ingredientCategoryRepository.findById(ingredientCategoryId) >> Optional.of(category)

        when:
        IngredientCategory result = ingredientCategoryService.findById(ingredientCategoryId)

        then:
        result == category
    }

    def "아이디로 재료 카테고리 조회 시 없는 경우 예외 발생"() {

        given:
        Long ingredientCategoryId = 1

        ingredientCategoryRepository.findById(ingredientCategoryId) >> Optional.empty()

        when:
        ingredientCategoryService.findById(ingredientCategoryId)

        then:
        def e = thrown(NotFoundIngredientCategoryException.class)
        e.message == "재료 카테고리인덱스를 찾을 수 없습니다."
    }
}
