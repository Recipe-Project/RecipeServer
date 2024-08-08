package com.recipe.app.src.recipe.application

import com.recipe.app.src.etc.application.BadWordService
import com.recipe.app.src.recipe.application.dto.RecipeIngredientRequest
import com.recipe.app.src.recipe.application.dto.RecipeProcessRequest
import com.recipe.app.src.recipe.application.dto.RecipeRequest
import com.recipe.app.src.recipe.domain.Recipe
import com.recipe.app.src.recipe.domain.RecipeLevel
import com.recipe.app.src.recipe.exception.NotFoundRecipeException
import com.recipe.app.src.recipe.infra.RecipeRepository
import com.recipe.app.src.user.domain.User
import spock.lang.Specification

class RecipeServiceTest extends Specification {

    private RecipeRepository recipeRepository = Mock()
    private BadWordService badWordService = Mock()
    private RecipeScrapService recipeScrapService = Mock()
    private RecipeViewService recipeViewService = Mock()
    private RecipeReportService recipeReportService = Mock()
    private RecipeService recipeService = new RecipeService(recipeRepository, badWordService, recipeScrapService, recipeViewService, recipeReportService)

    def "레시피 생성"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        String thumbnailImgUrl = ""
        String title = "제목"
        String introduction = "설명"
        int cookingTime = 10
        RecipeLevel level = RecipeLevel.NORMAL
        boolean isHidden = false

        List<RecipeIngredientRequest> ingredients = [
                RecipeIngredientRequest.builder()
                        .ingredientName("재료")
                        .ingredientIconId(1)
                        .quantity("1")
                        .unit("개")
                        .build()
        ]

        List<RecipeProcessRequest> processes = [
                RecipeProcessRequest.builder()
                        .cookingNo(1)
                        .cookingDescription("과정")
                        .recipeProcessImgUrl("")
                        .build()
        ]

        RecipeRequest request = RecipeRequest.builder()
                .thumbnailImgUrl(thumbnailImgUrl)
                .title(title)
                .introduction(introduction)
                .cookingTime(cookingTime)
                .level(level)
                .isHidden(isHidden)
                .ingredients(ingredients)
                .processes(processes)
                .build()

        when:
        recipeService.create(user, request)

        then:
        1 * recipeRepository.save(_) >> { args ->

            def recipe = args.get(0) as Recipe

            recipe.recipeNm == title
            recipe.introduction == introduction
            recipe.cookingTime == cookingTime.longValue()
            recipe.level == level
            recipe.isHidden() == isHidden
            recipe.ingredients.size() == ingredients.size()
            recipe.processes.size() == processes.size()
        }
    }

    def "레시피 생성 시 요청값이 null인 경우 예외 발생"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        String thumbnailImgUrl = ""
        String title = "제목"
        String introduction = "설명"
        int cookingTime = 10
        RecipeLevel level = RecipeLevel.NORMAL
        boolean isHidden = false

        RecipeRequest request = RecipeRequest.builder()
                .thumbnailImgUrl(thumbnailImgUrl)
                .title(title)
                .introduction(introduction)
                .cookingTime(cookingTime)
                .level(level)
                .isHidden(isHidden)
                .ingredients(ingredients)
                .processes(processes)
                .build()

        when:
        recipeService.create(user, request)

        then:
        def e = thrown(NullPointerException.class)
        e.message == expected

        where:
        ingredients         | processes          || expected
        null                | [RecipeProcessRequest.builder()
                                       .cookingNo(1)
                                       .cookingDescription("과정")
                                       .recipeProcessImgUrl("")
                                       .build()] || "레시피 재료 목록을 입력해주세요."
        null                | null               || "레시피 재료 목록을 입력해주세요."
        [RecipeIngredientRequest.builder()
                 .ingredientName("재료")
                 .ingredientIconId(1)
                 .quantity("1")
                 .unit("개")
                 .build()] || null               || "레시피 과정을 입력해주세요."

    }

    def "레시피 생성 시 유효하지 않은 요청값인 경우 예외 발생"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        String thumbnailImgUrl = ""
        String introduction = "설명"
        int cookingTime = 10
        RecipeLevel level = RecipeLevel.NORMAL
        boolean isHidden = false

        RecipeRequest request = RecipeRequest.builder()
                .thumbnailImgUrl(thumbnailImgUrl)
                .title(title)
                .introduction(introduction)
                .cookingTime(cookingTime)
                .level(level)
                .isHidden(isHidden)
                .ingredients(ingredients)
                .processes(processes)
                .build()

        when:
        recipeService.create(user, request)

        then:
        def e = thrown(IllegalArgumentException.class)
        e.message == expected

        where:
        title | ingredients         | processes          || expected
        null  | [RecipeIngredientRequest.builder()
                         .ingredientName("재료")
                         .ingredientIconId(1)
                         .quantity("1")
                         .unit("개")
                         .build()]  | [RecipeProcessRequest.builder()
                                               .cookingNo(1)
                                               .cookingDescription("과정")
                                               .recipeProcessImgUrl("")
                                               .build()] || "레시피 제목을 입력해주세요."
        ""    | [RecipeIngredientRequest.builder()
                         .ingredientName("재료")
                         .ingredientIconId(1)
                         .quantity("1")
                         .unit("개")
                         .build()]  | [RecipeProcessRequest.builder()
                                               .cookingNo(1)
                                               .cookingDescription("과정")
                                               .recipeProcessImgUrl("")
                                               .build()] || "레시피 제목을 입력해주세요."
        "제목"  | []                  | [RecipeProcessRequest.builder()
                                               .cookingNo(1)
                                               .cookingDescription("과정")
                                               .recipeProcessImgUrl("")
                                               .build()] || "레시피 재료 목록을 입력해주세요."
        "제목"  | []                  | []                 || "레시피 재료 목록을 입력해주세요."
        "제목"  | [RecipeIngredientRequest.builder()
                         .ingredientName("재료")
                         .ingredientIconId(1)
                         .quantity("1")
                         .unit("개")
                         .build()] || []                 || "레시피 과정을 입력해주세요."
    }

    def "레시피 수정"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        String thumbnailImgUrl = ""
        String title = "제목"
        String introduction = "설명"
        int cookingTime = 10
        RecipeLevel level = RecipeLevel.NORMAL
        boolean isHidden = false

        List<RecipeIngredientRequest> ingredients = [
                RecipeIngredientRequest.builder()
                        .ingredientName("재료")
                        .ingredientIconId(1)
                        .quantity("1")
                        .unit("개")
                        .build()
        ]

        List<RecipeProcessRequest> processes = [
                RecipeProcessRequest.builder()
                        .cookingNo(1)
                        .cookingDescription("과정")
                        .recipeProcessImgUrl("")
                        .build()
        ]

        RecipeRequest request = RecipeRequest.builder()
                .thumbnailImgUrl(thumbnailImgUrl)
                .title(title)
                .introduction(introduction)
                .cookingTime(cookingTime)
                .level(level)
                .isHidden(isHidden)
                .ingredients(ingredients)
                .processes(processes)
                .build()

        Recipe recipe = Recipe.builder()
                .recipeId(1)
                .recipeNm("제목")
                .introduction("설명")
                .level(RecipeLevel.NORMAL)
                .userId(1L)
                .isHidden(false)
                .build()

        recipeRepository.findByUserIdAndRecipeId(user.userId, recipe.recipeId) >> Optional.of(recipe)

        when:
        recipeService.update(user, recipe.recipeId, request)

        then:
        1 * recipeRepository.save(_) >> { args ->

            def updateRecipe = args.get(0) as Recipe

            updateRecipe.recipeId == recipe.recipeId
            updateRecipe.recipeNm == title
            updateRecipe.introduction == introduction
            updateRecipe.cookingTime == cookingTime.longValue()
            updateRecipe.level == level
            updateRecipe.isHidden() == isHidden
            updateRecipe.ingredients.size() == ingredients.size()
            updateRecipe.processes.size() == processes.size()
        }
    }

    def "레시피 수정 시 해당 레시피가 존재하지 않는 경우 예외 발생"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        String thumbnailImgUrl = ""
        String title = "제목"
        String introduction = "설명"
        int cookingTime = 10
        RecipeLevel level = RecipeLevel.NORMAL
        boolean isHidden = false

        List<RecipeIngredientRequest> ingredients = [
                RecipeIngredientRequest.builder()
                        .ingredientName("재료")
                        .ingredientIconId(1)
                        .quantity("1")
                        .unit("개")
                        .build()
        ]

        List<RecipeProcessRequest> processes = [
                RecipeProcessRequest.builder()
                        .cookingNo(1)
                        .cookingDescription("과정")
                        .recipeProcessImgUrl("")
                        .build()
        ]

        RecipeRequest request = RecipeRequest.builder()
                .thumbnailImgUrl(thumbnailImgUrl)
                .title(title)
                .introduction(introduction)
                .cookingTime(cookingTime)
                .level(level)
                .isHidden(isHidden)
                .ingredients(ingredients)
                .processes(processes)
                .build()

        Recipe recipe = Recipe.builder()
                .recipeId(1)
                .recipeNm("제목")
                .introduction("설명")
                .level(RecipeLevel.NORMAL)
                .userId(1L)
                .isHidden(false)
                .build()

        recipeRepository.findByUserIdAndRecipeId(user.userId, recipe.recipeId) >> Optional.empty()

        when:
        recipeService.update(user, recipe.recipeId, request)

        then:
        def e = thrown(NotFoundRecipeException.class)
        e.message == "레시피 정보를 찾지 못하였습니다."
    }


    def "레시피 수정 시 요청값이 null인 경우 예외 발생"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        String thumbnailImgUrl = ""
        String title = "제목"
        String introduction = "설명"
        int cookingTime = 10
        RecipeLevel level = RecipeLevel.NORMAL
        boolean isHidden = false

        RecipeRequest request = RecipeRequest.builder()
                .thumbnailImgUrl(thumbnailImgUrl)
                .title(title)
                .introduction(introduction)
                .cookingTime(cookingTime)
                .level(level)
                .isHidden(isHidden)
                .ingredients(ingredients)
                .processes(processes)
                .build()

        Recipe recipe = Recipe.builder()
                .recipeId(1)
                .recipeNm("제목")
                .introduction("설명")
                .level(RecipeLevel.NORMAL)
                .userId(1L)
                .isHidden(false)
                .build()

        when:
        recipeService.update(user, recipe.recipeId, request)

        then:
        def e = thrown(NullPointerException.class)
        e.message == expected

        where:
        ingredients         | processes          || expected
        null                | [RecipeProcessRequest.builder()
                                       .cookingNo(1)
                                       .cookingDescription("과정")
                                       .recipeProcessImgUrl("")
                                       .build()] || "레시피 재료 목록을 입력해주세요."
        null                | null               || "레시피 재료 목록을 입력해주세요."
        [RecipeIngredientRequest.builder()
                 .ingredientName("재료")
                 .ingredientIconId(1)
                 .quantity("1")
                 .unit("개")
                 .build()] || null               || "레시피 과정을 입력해주세요."
    }

    def "레시피 수정 시 유효하지 않은 요청값인 경우 예외 발생"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        String thumbnailImgUrl = ""
        String introduction = "설명"
        int cookingTime = 10
        RecipeLevel level = RecipeLevel.NORMAL
        boolean isHidden = false

        RecipeRequest request = RecipeRequest.builder()
                .thumbnailImgUrl(thumbnailImgUrl)
                .title(title)
                .introduction(introduction)
                .cookingTime(cookingTime)
                .level(level)
                .isHidden(isHidden)
                .ingredients(ingredients)
                .processes(processes)
                .build()

        Recipe recipe = Recipe.builder()
                .recipeId(1)
                .recipeNm("제목")
                .introduction("설명")
                .level(RecipeLevel.NORMAL)
                .userId(1L)
                .isHidden(false)
                .build()

        when:
        recipeService.update(user, recipe.recipeId, request)

        then:
        def e = thrown(IllegalArgumentException.class)
        e.message == expected

        where:
        title | ingredients         | processes          || expected
        null  | [RecipeIngredientRequest.builder()
                         .ingredientName("재료")
                         .ingredientIconId(1)
                         .quantity("1")
                         .unit("개")
                         .build()]  | [RecipeProcessRequest.builder()
                                               .cookingNo(1)
                                               .cookingDescription("과정")
                                               .recipeProcessImgUrl("")
                                               .build()] || "레시피 제목을 입력해주세요."
        ""    | [RecipeIngredientRequest.builder()
                         .ingredientName("재료")
                         .ingredientIconId(1)
                         .quantity("1")
                         .unit("개")
                         .build()]  | [RecipeProcessRequest.builder()
                                               .cookingNo(1)
                                               .cookingDescription("과정")
                                               .recipeProcessImgUrl("")
                                               .build()] || "레시피 제목을 입력해주세요."
        "제목"  | []                  | [RecipeProcessRequest.builder()
                                               .cookingNo(1)
                                               .cookingDescription("과정")
                                               .recipeProcessImgUrl("")
                                               .build()] || "레시피 재료 목록을 입력해주세요."
        "제목"  | []                  | []                 || "레시피 재료 목록을 입력해주세요."
        "제목"  | [RecipeIngredientRequest.builder()
                         .ingredientName("재료")
                         .ingredientIconId(1)
                         .quantity("1")
                         .unit("개")
                         .build()] || []                 || "레시피 과정을 입력해주세요."
    }

    def "레시피 삭제"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        Recipe recipe = Recipe.builder()
                .recipeId(1)
                .recipeNm("제목")
                .introduction("테스트설명")
                .level(RecipeLevel.NORMAL)
                .userId(1)
                .isHidden(false)
                .build()

        recipeRepository.findByUserIdAndRecipeId(user.userId, recipe.recipeId) >> Optional.of(recipe)

        when:
        recipeService.delete(user, recipe.recipeId)

        then:
        1 * recipeScrapService.deleteAllByRecipeId(recipe.recipeId)
        1 * recipeViewService.deleteAllByRecipeId(recipe.recipeId)
        1 * recipeReportService.deleteAllByRecipeId(recipe.recipeId)
        1 * recipeRepository.delete(recipe)
    }

    def "레시피 삭제 시 레시피 존재하지 않으면 예외 발생"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        Recipe recipe = Recipe.builder()
                .recipeId(1)
                .recipeNm("제목")
                .introduction("테스트설명")
                .level(RecipeLevel.NORMAL)
                .userId(1)
                .isHidden(false)
                .build()

        recipeRepository.findByUserIdAndRecipeId(user.userId, recipe.recipeId) >> Optional.empty()

        when:
        recipeService.delete(user, recipe.recipeId)

        then:
        def e = thrown(NotFoundRecipeException.class)
        e.message == "레시피 정보를 찾지 못하였습니다."
    }

    def "특정 유저의 레시피 목록 삭제"() {

        given:
        Long userId = 1

        List<Recipe> recipes = [
                Recipe.builder()
                        .recipeNm("제목")
                        .introduction("테스트설명")
                        .level(RecipeLevel.NORMAL)
                        .userId(1)
                        .isHidden(false)
                        .build(),
                Recipe.builder()
                        .recipeNm("제목")
                        .introduction("설명")
                        .level(RecipeLevel.NORMAL)
                        .userId(2)
                        .isHidden(true)
                        .build(),
        ]

        recipeRepository.findByUserId(userId) >> recipes

        when:
        recipeService.deleteAllByUserId(userId)

        then:
        1 * recipeScrapService.deleteAllByUserId(userId)
        1 * recipeViewService.deleteAllByUserId(userId)
        1 * recipeRepository.deleteAll(recipes)
    }

    def "특정 유저의 레시피 스크랩 수 조회"() {

        given:
        Long userId = 1
        recipeScrapService.countByUserId(userId) >> 5

        when:
        long result = recipeService.countRecipeScrapByUserId(userId)

        then:
        result == 5
    }

    def "레시피가 존재할 경우 레시피 스크랩 생성"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        Recipe recipe = Recipe.builder()
                .recipeId(1)
                .recipeNm("제목")
                .introduction("설명")
                .level(RecipeLevel.NORMAL)
                .userId(1L)
                .isHidden(false)
                .scrapCnt(1)
                .viewCnt(1)
                .build()

        recipeRepository.findById(recipe.recipeId) >> Optional.of(recipe)

        when:
        recipeService.createRecipeScrap(user, recipe.recipeId)

        then:
        1 * recipeScrapService.create(user.getUserId(), recipe.recipeId)
        recipe.scrapCnt == 2
    }

    def "레시피가 존재할 경우 레시피 스크랩 삭제"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        Recipe recipe = Recipe.builder()
                .recipeId(1)
                .recipeNm("제목")
                .introduction("설명")
                .level(RecipeLevel.NORMAL)
                .userId(1L)
                .isHidden(false)
                .scrapCnt(1)
                .viewCnt(1)
                .build()

        recipeRepository.findById(recipe.recipeId) >> Optional.of(recipe)

        when:
        recipeService.deleteRecipeScrap(user, recipe.recipeId)

        then:
        1 * recipeScrapService.delete(user.getUserId(), recipe.recipeId)
        recipe.scrapCnt == 0
    }

    def "레시피가 존재할 경우 레시피 조회 정보 생성"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        Recipe recipe = Recipe.builder()
                .recipeId(1)
                .recipeNm("제목")
                .introduction("설명")
                .level(RecipeLevel.NORMAL)
                .userId(1L)
                .isHidden(false)
                .scrapCnt(1)
                .viewCnt(1)
                .build()

        recipeRepository.findById(recipe.recipeId) >> Optional.of(recipe)

        when:
        recipeService.createRecipeView(user, recipe.recipeId)

        then:
        1 * recipeViewService.create(user.userId, recipe.recipeId)
        recipe.viewCnt == 2
    }

    def "레시피가 존재할 경우 레시피 신고 생성"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        Recipe recipe = Recipe.builder()
                .recipeId(1)
                .recipeNm("제목")
                .introduction("설명")
                .level(RecipeLevel.NORMAL)
                .userId(1L)
                .isHidden(false)
                .scrapCnt(1)
                .viewCnt(1)
                .build()

        recipeRepository.findById(recipe.recipeId) >> Optional.of(recipe)

        when:
        recipeService.createRecipeReport(user, recipe.recipeId)

        then:
        1 * recipeReportService.createRecipeReport(user.userId, recipe.recipeId)
    }

    def "레시피가 존재할 경우 레시피 신고 생성 후 신고 횟수가 5회 이상일 때 레시피 신고 처리"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        Recipe recipe = Recipe.builder()
                .recipeId(1)
                .recipeNm("제목")
                .introduction("설명")
                .level(RecipeLevel.NORMAL)
                .userId(1L)
                .isHidden(false)
                .scrapCnt(1)
                .viewCnt(1)
                .build()

        recipeRepository.findById(recipe.recipeId) >> Optional.of(recipe)

        recipeReportService.isRecipeReported(recipe.recipeId) >> true

        when:
        recipeService.createRecipeReport(user, recipe.recipeId)

        then:
        1 * recipeReportService.createRecipeReport(user.userId, recipe.recipeId)
        recipe.isHidden()
        recipe.isReported()
    }
}
