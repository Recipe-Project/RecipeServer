package com.recipe.app.src.recipe.application


import com.recipe.app.src.etc.application.BadWordService
import com.recipe.app.src.fridge.application.FridgeService
import com.recipe.app.src.recipe.application.dto.RecipeDetailResponse
import com.recipe.app.src.recipe.application.dto.RecipesResponse
import com.recipe.app.src.recipe.application.dto.RecommendedRecipesResponse
import com.recipe.app.src.recipe.domain.*
import com.recipe.app.src.recipe.exception.NotFoundRecipeException
import com.recipe.app.src.recipe.infra.RecipeRepository
import com.recipe.app.src.user.application.UserService
import com.recipe.app.src.user.domain.User
import spock.lang.Specification

import java.time.format.DateTimeFormatter

class RecipeSearchServiceTest extends Specification {

    private RecipeRepository recipeRepository = Mock()
    private FridgeService fridgeService = Mock()
    private UserService userService = Mock()
    private BadWordService badWordService = Mock()
    private RecipeScrapService recipeScrapService = Mock()
    private RecipeViewService recipeViewService = Mock()
    private RecipeSearchService recipeSearchService = new RecipeSearchService(recipeRepository, fridgeService, userService, badWordService,
            recipeScrapService, recipeViewService)

    def "레시피 키워드 검색 - 스크랩 수 정렬"() {

        given:
        List<User> users = [
                User.builder()
                        .userId(1)
                        .socialId("naver_1")
                        .nickname("테스터1")
                        .build(),
                User.builder()
                        .userId(2)
                        .socialId("naver_2")
                        .nickname("테스터2")
                        .build(),
        ]
        String keyword = "테스트"
        long lastRecipeId = 0
        int size = 10
        String sort = "recipeScraps"

        recipeRepository.countByKeyword(keyword) >> 2

        recipeScrapService.countByRecipeId(lastRecipeId) >> 0

        List<Recipe> recipes = [
                Recipe.builder()
                        .recipeId(1)
                        .recipeNm("제목1")
                        .introduction("설명1")
                        .level(RecipeLevel.NORMAL)
                        .userId(users.get(0).userId)
                        .isHidden(false)
                        .build(),
                Recipe.builder()
                        .recipeId(2)
                        .recipeNm("제목2")
                        .introduction("설명2")
                        .level(RecipeLevel.NORMAL)
                        .userId(users.get(1).userId)
                        .isHidden(true)
                        .build(),
        ]

        recipeRepository.findByKeywordLimitOrderByRecipeScrapCntDesc(keyword, lastRecipeId, 0, size) >> recipes

        userService.findByUserIds(users.userId) >> users

        recipeScrapService.findByRecipeIds(recipes.recipeId) >> [
                RecipeScrap.builder()
                        .userId(users.get(0).userId)
                        .recipeId(recipes.get(0).recipeId)
                        .build(),
                RecipeScrap.builder()
                        .userId(users.get(1).userId)
                        .recipeId(recipes.get(1).recipeId)
                        .build()
        ]

        when:
        RecipesResponse result = recipeSearchService.findRecipesByKeywordOrderBy(users.get(0), keyword, lastRecipeId, size, sort)

        then:
        result.totalCnt == 2
        result.recipes.recipeId == recipes.recipeId
        result.recipes.recipeName == recipes.recipeNm
        result.recipes.introduction == recipes.introduction
        result.recipes.thumbnailImgUrl == recipes.imgUrl
        result.recipes.postUserName == users.nickname
        result.recipes.postDate == [recipes.get(0).createdAt.format(DateTimeFormatter.ofPattern("yyyy.M.d")), recipes.get(1).createdAt.format(DateTimeFormatter.ofPattern("yyyy.M.d"))]
        result.recipes.isUserScrap == [true, false]
        result.recipes.scrapCnt == recipes.scrapCnt
        result.recipes.viewCnt == recipes.viewCnt
    }

    def "레시피 키워드 검색 - 조회 수 정렬"() {

        given:
        List<User> users = [
                User.builder()
                        .userId(1)
                        .socialId("naver_1")
                        .nickname("테스터1")
                        .build(),
                User.builder()
                        .userId(2)
                        .socialId("naver_2")
                        .nickname("테스터2")
                        .build(),
        ]
        String keyword = "테스트"
        long lastRecipeId = 0
        int size = 10
        String sort = "recipeViews"

        recipeRepository.countByKeyword(keyword) >> 2

        recipeViewService.countByRecipeId(lastRecipeId) >> 0

        List<Recipe> recipes = [
                Recipe.builder()
                        .recipeId(1)
                        .recipeNm("제목1")
                        .introduction("설명1")
                        .level(RecipeLevel.NORMAL)
                        .userId(users.get(0).userId)
                        .isHidden(false)
                        .build(),
                Recipe.builder()
                        .recipeId(2)
                        .recipeNm("제목2")
                        .introduction("설명2")
                        .level(RecipeLevel.NORMAL)
                        .userId(users.get(1).userId)
                        .isHidden(true)
                        .build(),
        ]

        recipeRepository.findByKeywordLimitOrderByRecipeViewCntDesc(keyword, lastRecipeId, 0, size) >> recipes

        userService.findByUserIds(users.userId) >> users

        recipeScrapService.findByRecipeIds(recipes.recipeId) >> [
                RecipeScrap.builder()
                        .userId(users.get(0).userId)
                        .recipeId(recipes.get(0).recipeId)
                        .build(),
                RecipeScrap.builder()
                        .userId(users.get(1).userId)
                        .recipeId(recipes.get(1).recipeId)
                        .build()
        ]

        when:
        RecipesResponse result = recipeSearchService.findRecipesByKeywordOrderBy(users.get(0), keyword, lastRecipeId, size, sort)

        then:
        result.totalCnt == 2
        result.recipes.recipeId == recipes.recipeId
        result.recipes.recipeName == recipes.recipeNm
        result.recipes.introduction == recipes.introduction
        result.recipes.thumbnailImgUrl == recipes.imgUrl
        result.recipes.postUserName == users.nickname
        result.recipes.postDate == [recipes.get(0).createdAt.format(DateTimeFormatter.ofPattern("yyyy.M.d")), recipes.get(1).createdAt.format(DateTimeFormatter.ofPattern("yyyy.M.d"))]
        result.recipes.isUserScrap == [true, false]
        result.recipes.scrapCnt == recipes.scrapCnt
        result.recipes.viewCnt == recipes.viewCnt
    }

    def "레시피 키워드 검색 - 최신순 정렬"() {

        given:
        List<User> users = [
                User.builder()
                        .userId(1)
                        .socialId("naver_1")
                        .nickname("테스터1")
                        .build(),
                User.builder()
                        .userId(2)
                        .socialId("naver_2")
                        .nickname("테스터2")
                        .build(),
        ]
        String keyword = "테스트"
        long lastRecipeId = 0
        int size = 10
        String sort = "newest"

        recipeRepository.countByKeyword(keyword) >> 2

        recipeRepository.findById(lastRecipeId) >> Optional.empty()

        List<Recipe> recipes = [
                Recipe.builder()
                        .recipeId(1)
                        .recipeNm("제목1")
                        .introduction("설명1")
                        .level(RecipeLevel.NORMAL)
                        .userId(users.get(0).userId)
                        .isHidden(false)
                        .build(),
                Recipe.builder()
                        .recipeId(2)
                        .recipeNm("제목2")
                        .introduction("설명2")
                        .level(RecipeLevel.NORMAL)
                        .userId(users.get(1).userId)
                        .isHidden(true)
                        .build(),
        ]

        recipeRepository.findById(lastRecipeId) >> Optional.empty()

        recipeRepository.findByKeywordLimitOrderByCreatedAtDesc(keyword, lastRecipeId, null, size) >> recipes

        userService.findByUserIds(users.userId) >> users

        recipeScrapService.findByRecipeIds(recipes.recipeId) >> [
                RecipeScrap.builder()
                        .userId(users.get(0).userId)
                        .recipeId(recipes.get(0).recipeId)
                        .build(),
                RecipeScrap.builder()
                        .userId(users.get(1).userId)
                        .recipeId(recipes.get(1).recipeId)
                        .build()
        ]

        when:
        RecipesResponse result = recipeSearchService.findRecipesByKeywordOrderBy(users.get(0), keyword, lastRecipeId, size, sort)

        then:
        result.totalCnt == 2
        result.recipes.recipeId == recipes.recipeId
        result.recipes.recipeName == recipes.recipeNm
        result.recipes.introduction == recipes.introduction
        result.recipes.thumbnailImgUrl == recipes.imgUrl
        result.recipes.postUserName == users.nickname
        result.recipes.postDate == [recipes.get(0).createdAt.format(DateTimeFormatter.ofPattern("yyyy.M.d")), recipes.get(1).createdAt.format(DateTimeFormatter.ofPattern("yyyy.M.d"))]
        result.recipes.isUserScrap == [true, false]
        result.recipes.scrapCnt == recipes.scrapCnt
        result.recipes.viewCnt == recipes.viewCnt
    }

    def "레시피 상세 조회"() {

        given:
        Long recipeId = 1

        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        Recipe recipe = Recipe.builder()
                .recipeId(1)
                .recipeNm("제목1")
                .introduction("설명1")
                .level(RecipeLevel.NORMAL)
                .userId(user.userId)
                .isHidden(false)
                .build()

        List<RecipeIngredient> ingredients = [
                RecipeIngredient.builder()
                        .recipeIngredientId(1)
                        .recipe(recipe)
                        .ingredientName("재료1")
                        .build()
        ]

        List<RecipeProcess> processes = [
                RecipeProcess.builder()
                        .recipeProcessId(1)
                        .recipe(recipe)
                        .cookingNo(1)
                        .cookingDescription("과정1")
                        .recipeProcessImgUrl("")
                        .build(),
                RecipeProcess.builder()
                        .recipeProcessId(2)
                        .recipe(recipe)
                        .cookingNo(2)
                        .cookingDescription("과정2")
                        .recipeProcessImgUrl("")
                        .build()
        ]

        recipeRepository.findRecipeDetail(recipeId, user.getUserId()) >> Optional.of(recipe)

        fridgeService.findIngredientNamesInFridge(user.getUserId()) >> ["재료"]

        recipeScrapService.existsByUserIdAndRecipeId(user.getUserId(), recipeId) >> true

        userService.findByUserId(user.getUserId()) >> user

        when:
        RecipeDetailResponse result = recipeSearchService.findRecipeDetail(user, recipeId)

        then:
        result.recipeId == recipe.recipeId
        result.recipeName == recipe.recipeNm
        result.introduction == recipe.introduction
        result.thumbnailImgUrl == recipe.imgUrl
        result.cookingTime == recipe.cookingTime
        result.level == recipe.level.getName()
        result.recipeIngredients.size() == ingredients.size()
        result.recipeIngredients.recipeIngredientId == ingredients.recipeIngredientId
        result.recipeProcesses.size() == processes.size()
        result.recipeProcesses.recipeProcessId == processes.recipeProcessId
        result.postUserId == user.userId
        result.postUserName == user.nickname
        result.isUserScrap == true
        result.scrapCnt == recipe.scrapCnt
        result.viewCnt == recipe.viewCnt
        result.isReported == recipe.isReported()
    }

    def "레시피 상세 조회 시 레시피 존재 하지 않으면 오류 발생"() {

        given:
        Long recipeId = 1

        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        recipeRepository.findRecipeDetail(recipeId, user.getUserId()) >> Optional.empty()

        when:
        recipeSearchService.findRecipeDetail(user, recipeId)

        then:
        def e = thrown(NotFoundRecipeException.class)
        e.message == "레시피 정보를 찾지 못하였습니다."
    }

    def "특정 유저가 스크랩한 레시피 목록 조회"() {

        given:
        long lastRecipeId = 1
        int size = 10

        List<User> users = [
                User.builder()
                        .userId(1)
                        .socialId("naver_1")
                        .nickname("테스터1")
                        .build(),
                User.builder()
                        .userId(2)
                        .socialId("naver_2")
                        .nickname("테스터2")
                        .build(),
        ]

        List<Recipe> recipes = [
                Recipe.builder()
                        .recipeId(1)
                        .recipeNm("제목1")
                        .introduction("설명1")
                        .level(RecipeLevel.NORMAL)
                        .userId(users.get(0).userId)
                        .isHidden(false)
                        .build(),
                Recipe.builder()
                        .recipeId(2)
                        .recipeNm("제목2")
                        .introduction("설명2")
                        .level(RecipeLevel.NORMAL)
                        .userId(users.get(1).userId)
                        .isHidden(true)
                        .build(),
        ]

        recipeScrapService.countByUserId(users.get(0).getUserId()) >> 2

        recipeScrapService.findByUserIdAndRecipeId(users.get(0).getUserId(), lastRecipeId) >> null

        recipeRepository.findUserScrapRecipesLimit(users.get(0).getUserId(), lastRecipeId, null, size) >> recipes

        userService.findByUserIds(users.userId) >> users

        recipeScrapService.findByRecipeIds(recipes.recipeId) >> [
                RecipeScrap.builder()
                        .userId(users.get(0).userId)
                        .recipeId(recipes.get(0).recipeId)
                        .build(),
                RecipeScrap.builder()
                        .userId(users.get(1).userId)
                        .recipeId(recipes.get(1).recipeId)
                        .build()
        ]

        when:
        RecipesResponse result = recipeSearchService.findScrapRecipes(users.get(0), lastRecipeId, size)

        then:
        result.totalCnt == 2
        result.recipes.recipeId == recipes.recipeId
        result.recipes.recipeName == recipes.recipeNm
        result.recipes.introduction == recipes.introduction
        result.recipes.thumbnailImgUrl == recipes.imgUrl
        result.recipes.postUserName == users.nickname
        result.recipes.postDate == [recipes.get(0).createdAt.format(DateTimeFormatter.ofPattern("yyyy.M.d")), recipes.get(1).createdAt.format(DateTimeFormatter.ofPattern("yyyy.M.d"))]
        result.recipes.isUserScrap == [true, false]
        result.recipes.scrapCnt == recipes.scrapCnt
        result.recipes.viewCnt == recipes.viewCnt
    }

    def "특정 유저가 작성한 레시피 목록 조회"() {

        given:
        Long lastRecipeId = 0
        int size = 10

        List<User> users = [
                User.builder()
                        .userId(1)
                        .socialId("naver_1")
                        .nickname("테스터1")
                        .build(),
                User.builder()
                        .userId(2)
                        .socialId("naver_2")
                        .nickname("테스터2")
                        .build(),
        ]

        List<Recipe> recipes = [
                Recipe.builder()
                        .recipeId(1)
                        .recipeNm("제목1")
                        .introduction("설명1")
                        .level(RecipeLevel.NORMAL)
                        .userId(users.get(0).userId)
                        .isHidden(false)
                        .build(),
                Recipe.builder()
                        .recipeId(2)
                        .recipeNm("제목2")
                        .introduction("설명2")
                        .level(RecipeLevel.NORMAL)
                        .userId(users.get(1).userId)
                        .isHidden(true)
                        .build(),
        ]

        recipeRepository.countByUserId(users.get(0).userId) >> 2

        recipeRepository.findLimitByUserId(users.get(0).getUserId(), lastRecipeId, size) >> recipes

        userService.findByUserIds(users.userId) >> users

        recipeScrapService.findByRecipeIds(recipes.recipeId) >> [
                RecipeScrap.builder()
                        .userId(users.get(0).userId)
                        .recipeId(recipes.get(0).recipeId)
                        .build(),
                RecipeScrap.builder()
                        .userId(users.get(1).userId)
                        .recipeId(recipes.get(1).recipeId)
                        .build()
        ]

        when:
        RecipesResponse result = recipeSearchService.findRecipesByUser(users.get(0), lastRecipeId, size)

        then:
        result.totalCnt == 2
        result.recipes.recipeId == recipes.recipeId
        result.recipes.recipeName == recipes.recipeNm
        result.recipes.introduction == recipes.introduction
        result.recipes.thumbnailImgUrl == recipes.imgUrl
        result.recipes.postUserName == users.nickname
        result.recipes.postDate == [recipes.get(0).createdAt.format(DateTimeFormatter.ofPattern("yyyy.M.d")), recipes.get(1).createdAt.format(DateTimeFormatter.ofPattern("yyyy.M.d"))]
        result.recipes.isUserScrap == [true, false]
        result.recipes.scrapCnt == recipes.scrapCnt
        result.recipes.viewCnt == recipes.viewCnt
    }

    def "냉장고 재료 일치하는 레시피 목록 조회"() {

        given:
        Long lastRecipeId = 0
        int size = 10

        List<User> users = [
                User.builder()
                        .userId(1)
                        .socialId("naver_1")
                        .nickname("테스터1")
                        .build(),
                User.builder()
                        .userId(2)
                        .socialId("naver_2")
                        .nickname("테스터2")
                        .build(),
        ]

        List<Recipe> recipes = [
                Recipe.builder()
                        .recipeId(1)
                        .recipeNm("제목1")
                        .introduction("설명1")
                        .level(RecipeLevel.NORMAL)
                        .userId(users.get(0).userId)
                        .isHidden(false)
                        .build(),
                Recipe.builder()
                        .recipeId(2)
                        .recipeNm("제목2")
                        .introduction("설명2")
                        .level(RecipeLevel.NORMAL)
                        .userId(users.get(1).userId)
                        .isHidden(true)
                        .build(),
        ]

        List<RecipeIngredient> ingredients = [
                RecipeIngredient.builder()
                        .recipeIngredientId(1)
                        .recipe(recipes.get(0))
                        .ingredientName("테스트1")
                        .build(),
                RecipeIngredient.builder()
                        .recipeIngredientId(2)
                        .recipe(recipes.get(1))
                        .ingredientName("재료1")
                        .build(),
                RecipeIngredient.builder()
                        .recipeIngredientId(3)
                        .recipe(recipes.get(1))
                        .ingredientName("재료2")
                        .build(),
                RecipeIngredient.builder()
                        .recipeIngredientId(4)
                        .recipe(recipes.get(1))
                        .ingredientName("테스트2")
                        .build(),
        ]

        List<String> ingredientNamesInFridge = ["테스트1", "테스트2"]

        fridgeService.findIngredientNamesInFridge(users.get(0).userId) >> ingredientNamesInFridge

        recipeRepository.findRecipesInFridge(ingredientNamesInFridge) >> recipes

        userService.findByUserIds(users.userId) >> users

        recipeScrapService.findByRecipeIds(recipes.recipeId) >> [
                RecipeScrap.builder()
                        .userId(users.get(0).userId)
                        .recipeId(recipes.get(0).recipeId)
                        .build(),
                RecipeScrap.builder()
                        .userId(users.get(1).userId)
                        .recipeId(recipes.get(1).recipeId)
                        .build()
        ]

        recipeRepository.findById(lastRecipeId) >> Optional.empty()

        when:
        RecommendedRecipesResponse result = recipeSearchService.findRecommendedRecipesByUserFridge(users.get(0), lastRecipeId, size)

        then:
        result.totalCnt == 2
        result.recipes.recipeId == recipes.recipeId
        result.recipes.recipeName == recipes.recipeNm
        result.recipes.introduction == recipes.introduction
        result.recipes.thumbnailImgUrl == recipes.imgUrl
        result.recipes.postUserName == users.nickname
        result.recipes.postDate == [recipes.get(0).createdAt.format(DateTimeFormatter.ofPattern("yyyy.M.d")), recipes.get(1).createdAt.format(DateTimeFormatter.ofPattern("yyyy.M.d"))]
        result.recipes.isUserScrap == [true, false]
        result.recipes.scrapCnt == recipes.scrapCnt
        result.recipes.viewCnt == recipes.viewCnt
        result.recipes.ingredientsMatchRate == [100, 33]
    }
}
