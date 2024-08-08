package com.recipe.app.src.recipe.domain

import spock.lang.Specification

class RecipesTest extends Specification {

    def "레시피 목록 사이즈 구하기"() {

        given:
        Recipes recipes = new Recipes([
                Recipe.builder()
                        .recipeId(1L)
                        .recipeNm("테스트제목")
                        .introduction("설명")
                        .level(RecipeLevel.NORMAL)
                        .userId(1L)
                        .isHidden(false)
                        .build(),
                Recipe.builder()
                        .recipeId(2L)
                        .recipeNm("제목")
                        .introduction("테스트설명")
                        .level(RecipeLevel.NORMAL)
                        .userId(2L)
                        .isHidden(false)
                        .build(),
                Recipe.builder()
                        .recipeId(3L)
                        .recipeNm("제목")
                        .introduction("설명")
                        .level(RecipeLevel.NORMAL)
                        .userId(3L)
                        .isHidden(false)
                        .build(),
        ])

        when:
        int result = recipes.size()

        then:
        result == 3
    }

    def "레시피 목록 내 회원 아이디 목록 가져오기"() {

        given:
        Recipes recipes = new Recipes([
                Recipe.builder()
                        .recipeId(1L)
                        .recipeNm("테스트제목")
                        .introduction("설명")
                        .level(RecipeLevel.NORMAL)
                        .userId(1L)
                        .isHidden(false)
                        .build(),
                Recipe.builder()
                        .recipeId(2L)
                        .recipeNm("제목")
                        .introduction("테스트설명")
                        .level(RecipeLevel.NORMAL)
                        .userId(2L)
                        .isHidden(false)
                        .build(),
                Recipe.builder()
                        .recipeId(3L)
                        .recipeNm("제목")
                        .introduction("설명")
                        .level(RecipeLevel.NORMAL)
                        .userId(3L)
                        .isHidden(false)
                        .build(),
        ])

        when:
        List<Long> userIds = recipes.getUserIds()

        then:
        userIds == [1L, 2L, 3L]
    }

    def "레시피 목록 내 레시피 아이디 목록 가져오기"() {

        given:
        Recipes recipes = new Recipes([
                Recipe.builder()
                        .recipeId(1L)
                        .recipeNm("테스트제목")
                        .introduction("설명")
                        .level(RecipeLevel.NORMAL)
                        .userId(1L)
                        .isHidden(false)
                        .build(),
                Recipe.builder()
                        .recipeId(2L)
                        .recipeNm("제목")
                        .introduction("테스트설명")
                        .level(RecipeLevel.NORMAL)
                        .userId(2L)
                        .isHidden(false)
                        .build(),
                Recipe.builder()
                        .recipeId(3L)
                        .recipeNm("제목")
                        .introduction("설명")
                        .level(RecipeLevel.NORMAL)
                        .userId(3L)
                        .isHidden(false)
                        .build(),
        ])

        when:
        List<Long> recipeIds = recipes.getRecipeIds()

        then:
        recipeIds == [1L, 2L, 3L]
    }
}
