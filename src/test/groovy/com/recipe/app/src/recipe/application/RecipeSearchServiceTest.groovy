package com.recipe.app.src.recipe.application


import com.recipe.app.src.common.utils.BadWordFiltering
import com.recipe.app.src.fridge.application.FridgeService
import com.recipe.app.src.ingredient.application.IngredientSynonymCache
import com.recipe.app.src.recipe.application.dto.RecipeDetailResponse
import com.recipe.app.src.recipe.application.dto.RecipesResponse
import com.recipe.app.src.recipe.application.keyword.SearchKeywordService
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
    private BadWordFiltering badWordService = Mock()
    private RecipeScrapService recipeScrapService = Mock()
    private RecipeViewService recipeViewService = Mock()
    private IngredientSynonymCache ingredientSynonymCache = Mock()
    private SearchKeywordService searchKeywordService = Mock()
    private RecipeSearchService recipeSearchService = new RecipeSearchService(recipeRepository, fridgeService, userService, badWordService,
            recipeScrapService, recipeViewService, ingredientSynonymCache, searchKeywordService)

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
        String sort = "scraps"

        recipeRepository.countByKeyword(_) >> 2

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

        recipeRepository.findByKeywordLimitOrderByRecipeScrapCntDesc(_, lastRecipeId, _, 0, size) >> recipes

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
        String sort = "views"

        recipeRepository.countByKeyword(_) >> 2

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

        recipeRepository.findByKeywordLimitOrderByRecipeViewCntDesc(_, lastRecipeId, _, 0, size) >> recipes

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

        recipeRepository.countByKeyword(_) >> 2

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

        recipeRepository.findByKeywordLimitOrderByCreatedAtDesc(_, lastRecipeId, _, null, size) >> recipes

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

    def "비로그인(null user) 키워드 검색은 NPE 없이 isUserScrap=false 로 응답한다"() {

        given:
        Recipe recipe = Recipe.builder()
                .recipeId(1)
                .recipeNm("제목1")
                .introduction("설명1")
                .level(RecipeLevel.NORMAL)
                .userId(1L)
                .isHidden(false)
                .build()

        recipeRepository.countByKeyword(_) >> 1
        recipeRepository.findById(_) >> Optional.empty()
        recipeRepository.findByKeywordLimitOrderByCreatedAtDesc(_, _, _, _, _) >> [recipe]
        userService.findByUserIds(_) >> []
        recipeScrapService.findByRecipeIds(_) >> []

        when:
        RecipesResponse result = recipeSearchService.findRecipesByKeywordOrderBy(null, "테스트", 0L, 10, "newest")

        then:
        noExceptionThrown()
        result.recipes.size() == 1
        !result.recipes[0].isUserScrap
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

        fridgeService.findIngredientNamesInFridge(users.get(0).getUserId()) >> []

        when:
        RecipesResponse result = recipeSearchService.findRecipesByUser(users.get(0), null, lastRecipeId, size)

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

    def "등록한 레시피 조회 - keyword 있으면 내 레시피 검색 경로(userId 전달), 목록 경로 안 탐"() {

        given:
        User user = User.builder().userId(1).socialId("naver_1").nickname("테스터1").build()
        String keyword = "테스트"
        long lastRecipeId = 0
        int size = 10

        recipeRepository.countByKeywordAndUserId(_, user.userId) >> 1
        recipeRepository.findRelevanceScoreByRecipeId(_, lastRecipeId) >> null
        recipeRepository.findById(lastRecipeId) >> Optional.empty()

        Recipe recipe = Recipe.builder()
                .recipeId(1)
                .recipeNm("테스트제목")
                .introduction("설명")
                .level(RecipeLevel.NORMAL)
                .userId(user.userId)
                .isHidden(false)
                .build()

        userService.findByUserIds(_) >> [user]
        recipeScrapService.findByRecipeIds(_) >> []
        fridgeService.findIngredientNamesInFridge(user.userId) >> []

        when:
        RecipesResponse result = recipeSearchService.findRecipesByUser(user, keyword, lastRecipeId, size)

        then: "keyword 있으면 userId 로 내 레시피 검색 쿼리를 호출하고, 전체 목록 경로는 타지 않는다"
        1 * recipeRepository.findByKeywordAndUserIdLimitOrderByCreatedAtDesc(_, user.userId, lastRecipeId, _, _, size) >> [recipe]
        0 * recipeRepository.findLimitByUserId(_, _, _)
        result.totalCnt == 1
        result.recipes.recipeId == [recipe.recipeId]
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
        RecipesResponse result = recipeSearchService.findRecommendedRecipesByUserFridge(users.get(0), lastRecipeId, size)

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

    def "Public 추천 - 입력 재료를 동의어 확장 후 FULLTEXT 매칭한다"() {

        given:
        long lastRecipeId = 0
        int size = 10
        List<String> inputIngredientNames = ["새우"]
        Set<String> expandedSet = ["새우", "대하"] as Set

        List<Recipe> recipes = [
                Recipe.builder()
                        .recipeId(1L)
                        .recipeNm("대하구이")
                        .introduction("대하 들어간 레시피")
                        .level(RecipeLevel.NORMAL)
                        .userId(1L)
                        .isHidden(false)
                        .build()
        ]

        ingredientSynonymCache.expand(inputIngredientNames) >> expandedSet
        userService.findByUserIds(_) >> []
        recipeScrapService.findByRecipeIds(_) >> []
        recipeRepository.findById(lastRecipeId) >> Optional.empty()

        when:
        RecipesResponse result = recipeSearchService.findPublicRecommendedRecipesByIngredients(inputIngredientNames, lastRecipeId, size)

        then: "동의어 expand 결과(새우+대하)가 그대로 검색 인자로 전달된다"
        1 * recipeRepository.findRecipesInFridge({ Collection<String> arg -> arg as Set == expandedSet }) >> recipes
        result.totalCnt == 1
        result.recipes.recipeId == [1L]
    }

    def "Public 추천 - 빈 입력은 빈 결과를 반환한다"() {

        given:
        long lastRecipeId = 0
        int size = 10

        ingredientSynonymCache.expand([]) >> ([] as Set)
        recipeRepository.findRecipesInFridge(_) >> []
        userService.findByUserIds(_) >> []
        recipeScrapService.findByRecipeIds(_) >> []
        recipeRepository.findById(lastRecipeId) >> Optional.empty()

        when:
        RecipesResponse result = recipeSearchService.findPublicRecommendedRecipesByIngredients([], lastRecipeId, size)

        then:
        result.totalCnt == 0
        result.recipes.isEmpty()
    }

    def "레시피 키워드 검색 - 빈/공백 입력은 빈 결과를 반환한다"() {

        given:
        User user = User.builder().userId(1).socialId("naver_1").nickname("테스터1").build()
        userService.findByUserIds(_) >> []
        recipeScrapService.findByRecipeIds(_) >> []

        when:
        RecipesResponse result = recipeSearchService.findRecipesByKeywordOrderBy(user, input, 0L, 10, "newest")

        then:
        result.totalCnt == 0
        result.recipes.isEmpty()
        0 * recipeRepository.countByKeyword(_)
        0 * recipeRepository.findByKeywordLimitOrderByCreatedAtDesc(_, _, _, _, _)

        where:
        input << ["", "   ", "\t"]
    }
}
