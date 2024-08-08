package com.recipe.app.src.user.application

import com.recipe.app.src.fridge.application.FridgeService
import com.recipe.app.src.fridgeBasket.application.FridgeBasketService
import com.recipe.app.src.ingredient.application.IngredientService
import com.recipe.app.src.recipe.application.RecipeSearchService
import com.recipe.app.src.recipe.application.RecipeService
import com.recipe.app.src.recipe.application.blog.BlogScrapService
import com.recipe.app.src.recipe.application.blog.BlogViewService
import com.recipe.app.src.recipe.application.youtube.YoutubeScrapService
import com.recipe.app.src.recipe.application.youtube.YoutubeViewService
import com.recipe.app.src.recipe.domain.Recipe
import com.recipe.app.src.recipe.domain.RecipeLevel
import com.recipe.app.src.user.application.dto.UserProfileResponse
import com.recipe.app.src.user.domain.LoginProvider
import com.recipe.app.src.user.domain.User
import jakarta.servlet.http.HttpServletRequest
import spock.lang.Specification

class UserFacadeServiceTest extends Specification {

    private static final int USER_PROFILE_RECIPE_CNT = 6;
    private UserService userService = Mock()
    private RecipeService recipeService = Mock()
    private RecipeSearchService recipeSearchService = Mock()
    private YoutubeScrapService youtubeScrapService = Mock()
    private YoutubeViewService youtubeViewService = Mock()
    private BlogScrapService blogScrapService = Mock()
    private BlogViewService blogViewService = Mock()
    private FridgeService fridgeService = Mock()
    private FridgeBasketService fridgeBasketService = Mock()
    private IngredientService ingredientService = Mock()
    private UserFacadeService userFacadeService = new UserFacadeService(userService, recipeService, recipeSearchService,
            youtubeScrapService, youtubeViewService, blogScrapService, blogViewService, fridgeService, fridgeBasketService, ingredientService)

    def "유저 프로필 조회"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        long youtubeScrapCnt = 1
        long blogScrapCnt = 3
        long recipeScrapCnt = 5
        youtubeScrapService.countByUserId(user.userId) >> youtubeScrapCnt
        blogScrapService.countByUserId(user.userId) >> blogScrapCnt
        recipeService.countRecipeScrapByUserId(user.userId) >> recipeScrapCnt

        List<Recipe> recipes = [
                Recipe.builder()
                        .recipeId(1)
                        .recipeNm("제목1")
                        .introduction("설명1")
                        .level(RecipeLevel.NORMAL)
                        .userId(user.userId)
                        .isHidden(false)
                        .build(),
                Recipe.builder()
                        .recipeId(2)
                        .recipeNm("제목2")
                        .introduction("설명2")
                        .level(RecipeLevel.NORMAL)
                        .userId(user.userId)
                        .isHidden(true)
                        .build(),
        ]

        recipeSearchService.findLimitByUserId(user.userId, 0L, USER_PROFILE_RECIPE_CNT) >> recipes

        when:
        UserProfileResponse result = userFacadeService.findUserProfile(user)

        then:
        result.userId == user.userId;
        result.profileImgUrl == user.profileImgUrl;
        result.nickname == user.nickname;
        result.email == user.email;
        result.loginProvider == LoginProvider.NAVER
        result.youtubeScrapCnt == youtubeScrapCnt
        result.blogScrapCnt == blogScrapCnt
        result.recipeScrapCnt == recipeScrapCnt
        result.userRecipeTotalSize == recipes.size()
        result.userRecipes.recipeId == recipes.recipeId
        result.userRecipes.thumbnailImgUrl == recipes.imgUrl
    }

    def "유저 삭제"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        HttpServletRequest request = Mock()

        when:
        userFacadeService.deleteUser(user, request)

        then:
        1 * fridgeService.deleteAllByUserId(user.userId)
        1 * fridgeBasketService.deleteAllByUserId(user.userId)
        1 * recipeService.deleteAllByUserId(user.userId)
        1 * ingredientService.deleteAllByUserId(user.userId)
        1 * youtubeScrapService.deleteAllByUserId(user.userId)
        1 * youtubeViewService.deleteAllByUserId(user.userId)
        1 * blogScrapService.deleteAllByUserId(user.userId)
        1 * blogViewService.deleteAllByUserId(user.userId)
        1 * userService.withdraw(user, request)
    }
}
