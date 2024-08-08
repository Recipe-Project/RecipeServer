package com.recipe.app.src.recipe.domain

import spock.lang.Specification

class RecipeTest extends Specification {

    def "레시피 생성 성공"() {

        given:
        Long recipeId = 1L
        String recipeNm = "제목"
        String introduction = "설명"
        Long cookingTime = 10
        RecipeLevel level = RecipeLevel.NORMAL
        String imgUrl = "http://test.jpg"
        Long quantity = 1L
        Long calorie = 300L
        Long userId = 1L
        boolean isHidden = false
        long scrapCnt = 1L
        long viewCnt = 1L


        when:
        Recipe recipe = Recipe.builder()
                .recipeId(recipeId)
                .recipeNm(recipeNm)
                .introduction(introduction)
                .cookingTime(cookingTime)
                .level(level)
                .imgUrl(imgUrl)
                .quantity(quantity)
                .calorie(calorie)
                .userId(userId)
                .isHidden(isHidden)
                .scrapCnt(scrapCnt)
                .viewCnt(viewCnt)
                .build()

        then:
        recipe.recipeId == recipeId
        recipe.recipeNm == recipeNm
        recipe.introduction == introduction
        recipe.cookingTime == cookingTime
        recipe.level == level
        recipe.imgUrl == imgUrl
        recipe.quantity == quantity
        recipe.calorie == calorie
        recipe.userId == userId
        recipe.hiddenYn == (isHidden ? "Y" : "N")
        recipe.scrapCnt == scrapCnt
        recipe.viewCnt == viewCnt
    }

    def "레시피 생성 시 요청 값이 null인 경우 예외 발생"() {

        when:
        Recipe.builder()
                .recipeNm("제목")
                .introduction("설명")
                .level(RecipeLevel.NORMAL)
                .userId(null)
                .isHidden(false)
                .build()

        then:
        def e = thrown(NullPointerException.class)
        e.message == "유저 아이디를 입력해주세요."
    }

    def "레시피 생성 시 유효하지 않은 요청값인 경우 예외 발생"() {

        when:
        Recipe.builder()
                .recipeNm(recipeNm)
                .introduction("설명")
                .level(RecipeLevel.NORMAL)
                .userId(1L)
                .isHidden(false)
                .build()

        then:
        def e = thrown(IllegalArgumentException.class)
        e.message == expected

        where:
        recipeNm || expected
        ""       || "레시피명을 입력해주세요."
        null     || "레시피명을 입력해주세요."
    }

    def "레시피 수정 성공"() {

        given:
        Recipe recipe = Recipe.builder()
                .recipeNm("제목")
                .introduction("설명")
                .level(RecipeLevel.NORMAL)
                .userId(1L)
                .isHidden(false)
                .build()

        RecipeIngredient.builder()
                .recipe(recipe)
                .ingredientName("재료1")
                .build()
        RecipeIngredient.builder()
                .recipe(recipe)
                .ingredientName("재료2")
                .build()

        RecipeProcess.builder()
                .recipe(recipe)
                .cookingNo(1)
                .cookingDescription("과정")
                .recipeProcessImgUrl("")
                .build()

        String updateRecipeNm = "수정제목"
        String updateIntroduction = "수정설명"
        Long updateCookingTime = 10L
        RecipeLevel updateLevel = RecipeLevel.HARD
        String updateImgUrl = "update"
        boolean updateHidden = true

        Recipe updateRecipe = Recipe.builder()
                .recipeNm(updateRecipeNm)
                .introduction(updateIntroduction)
                .cookingTime(updateCookingTime)
                .level(updateLevel)
                .imgUrl(updateImgUrl)
                .userId(1L)
                .isHidden(updateHidden)
                .build()

        RecipeIngredient.builder()
                .recipe(updateRecipe)
                .ingredientName("재료1")
                .build()

        RecipeProcess.builder()
                .recipe(updateRecipe)
                .cookingNo(1)
                .cookingDescription("과정1")
                .recipeProcessImgUrl("")
                .build()
        RecipeProcess.builder()
                .recipe(updateRecipe)
                .cookingNo(2)
                .cookingDescription("과정2")
                .recipeProcessImgUrl("")
                .build()

        when:
        recipe.updateRecipe(updateRecipe)

        then:
        recipe.recipeNm == updateRecipeNm
        recipe.introduction == updateIntroduction
        recipe.cookingTime == updateCookingTime
        recipe.level == updateLevel
        recipe.imgUrl == updateImgUrl
        recipe.hiddenYn == (updateHidden ? "Y" : "N")
        recipe.ingredients.size() == 1
        recipe.processes.size() == 2
    }

    def "레시피 공개 여부 확인"() {

        given:
        Recipe recipe = Recipe.builder()
                .recipeNm("제목")
                .introduction("설명")
                .level(RecipeLevel.NORMAL)
                .userId(1L)
                .isHidden(isHidden)
                .build()

        when:
        boolean result = recipe.isHidden()

        then:
        result == expected

        where:
        isHidden || expected
        false    || false
        true     || true
    }

    def "레시피 신고하기"() {

        given:
        Recipe recipe = Recipe.builder()
                .recipeNm("제목")
                .introduction("설명")
                .level(RecipeLevel.NORMAL)
                .userId(1L)
                .isHidden(false)
                .build()

        when:
        recipe.report()

        then:
        recipe.isHidden()
        recipe.isReported()
    }

    def "레시피 스크랩 수 증가"() {

        given:
        Recipe recipe = Recipe.builder()
                .recipeNm("제목")
                .introduction("설명")
                .level(RecipeLevel.NORMAL)
                .userId(1L)
                .isHidden(false)
                .scrapCnt(1L)
                .build()

        when:
        recipe.plusScrapCnt()

        then:
        recipe.scrapCnt == 2
    }

    def "레시피 스크랩 수 감소"() {

        given:
        Recipe recipe = Recipe.builder()
                .recipeNm("제목")
                .introduction("설명")
                .level(RecipeLevel.NORMAL)
                .userId(1L)
                .isHidden(false)
                .scrapCnt(1L)
                .build()

        when:
        recipe.minusScrapCnt()

        then:
        recipe.scrapCnt == 0
    }

    def "레시피 조회수 증가"() {

        given:
        Recipe recipe = Recipe.builder()
                .recipeNm("제목")
                .introduction("설명")
                .level(RecipeLevel.NORMAL)
                .userId(1L)
                .isHidden(false)
                .viewCnt(1L)
                .build()

        when:
        recipe.plusViewCnt()

        then:
        recipe.viewCnt == 2
    }

    def "재료 일치율 계산"() {

        given:
        Recipe recipe = Recipe.builder()
                .recipeNm("제목")
                .introduction("설명")
                .level(RecipeLevel.NORMAL)
                .userId(1L)
                .isHidden(false)
                .build()

        RecipeIngredient.builder()
                .recipe(recipe)
                .ingredientName("김치")
                .build()
        RecipeIngredient.builder()
                .recipe(recipe)
                .ingredientName("닭다리살")
                .build()
        RecipeIngredient.builder()
                .recipe(recipe)
                .ingredientName("닭가슴살")
                .build()
        RecipeIngredient.builder()
                .recipe(recipe)
                .ingredientName("닭날개")
                .build()
        RecipeIngredient.builder()
                .recipe(recipe)
                .ingredientName("소고기")
                .build()
        RecipeIngredient.builder()
                .recipe(recipe)
                .ingredientName("오리고기")
                .build()
        RecipeIngredient.builder()
                .recipe(recipe)
                .ingredientName("삼겹살")
                .build()

        List<String> ingredientNamesInFridge = ["돼지고기", "오리고기", "김치", "소고기"]

        when:
        long result = recipe.calculateIngredientMatchRate(ingredientNamesInFridge)

        then:
        result == 43
    }
}
