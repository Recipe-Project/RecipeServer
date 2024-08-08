package com.recipe.app.src.recipe.domain

import spock.lang.Specification

class RecipeProcessTest extends Specification {

    def "레시피 과정 생성 성공"() {

        given:
        Recipe recipe = Recipe.builder()
                .recipeNm("제목")
                .introduction("설명")
                .level(RecipeLevel.NORMAL)
                .userId(1L)
                .isHidden(false)
                .build()

        Long recipeProcessId = 1L
        Integer cookingNo = 1
        String cookingDescription = "과정"
        String recipeProcessImgUrl = ""

        when:
        RecipeProcess recipeProcess = RecipeProcess.builder()
                .recipeProcessId(recipeProcessId)
                .recipe(recipe)
                .cookingNo(cookingNo)
                .cookingDescription(cookingDescription)
                .recipeProcessImgUrl(recipeProcessImgUrl)
                .build()

        then:
        recipeProcess.recipeProcessId == recipeProcessId
        recipeProcess.recipe == recipe
        recipeProcess.cookingNo == cookingNo
        recipeProcess.cookingDescription == cookingDescription
        recipeProcess.recipeProcessImgUrl == recipeProcessImgUrl
    }

    def "레시피 과정 생성 시 요청 값이 null인 경우 예외 발생"() {

        given:
        Recipe recipe = Recipe.builder()
                .recipeNm("제목")
                .introduction("설명")
                .level(RecipeLevel.NORMAL)
                .userId(1L)
                .isHidden(false)
                .build()

        when:
        RecipeProcess.builder()
                .recipe(recipe)
                .cookingNo(null)
                .cookingDescription("과정")
                .recipeProcessImgUrl("")
                .build()

        then:
        def e = thrown(NullPointerException.class)
        e.message == "레시피 요리 순서를 입력해주세요."
    }

    def "레시피 과정 생성 시 유효하지 않은 요청 값인 경우 예외 발생"() {

        given:
        Recipe recipe = Recipe.builder()
                .recipeNm("제목")
                .introduction("설명")
                .level(RecipeLevel.NORMAL)
                .userId(1L)
                .isHidden(false)
                .build()

        when:
        RecipeProcess.builder()
                .recipe(recipe)
                .cookingNo(1)
                .cookingDescription(cookingDescription)
                .recipeProcessImgUrl("")
                .build()

        then:
        def e = thrown(IllegalArgumentException.class)
        e.message == expected

        where:
        cookingDescription || expected
        ""                 || "레시피 요리 과정 설명을 입력해주세요."
        null               || "레시피 요리 과정 설명을 입력해주세요."
    }

    def "레시피 과정 삭제"() {

        given:
        Recipe recipe = Recipe.builder()
                .recipeNm("제목")
                .introduction("설명")
                .level(RecipeLevel.NORMAL)
                .userId(1L)
                .isHidden(false)
                .build()

        RecipeProcess process = RecipeProcess.builder()
                .recipe(recipe)
                .cookingNo(1)
                .cookingDescription("과정")
                .recipeProcessImgUrl("")
                .build()

        when:
        process.delete()

        then:
        process.recipe == null
    }
}
