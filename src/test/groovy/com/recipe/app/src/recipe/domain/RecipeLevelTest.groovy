package com.recipe.app.src.recipe.domain

import spock.lang.Specification

class RecipeLevelTest extends Specification {

    def "레시피 난이도 코드 가져오기"() {

        when:
        String result = level.getCode()

        then:
        result == expected

        where:
        level              || expected
        RecipeLevel.EASY   || "02"
        RecipeLevel.NORMAL || "01"
        RecipeLevel.HARD   || "00"
    }

    def "레시피 난이도 이름 가져오기"() {

        when:
        String result = level.getName()

        then:
        result == expected

        where:
        level              || expected
        RecipeLevel.EASY   || "초보환영"
        RecipeLevel.NORMAL || "보통"
        RecipeLevel.HARD   || "어려움"
    }

    def "코드로 레시피 난이도 찾기"() {

        when:
        RecipeLevel result = RecipeLevel.findRecipeLevelByCode(code)

        then:
        result == expected

        where:
        code || expected
        "00" || RecipeLevel.HARD
        "01" || RecipeLevel.NORMAL
        "02" || RecipeLevel.EASY
    }
}
