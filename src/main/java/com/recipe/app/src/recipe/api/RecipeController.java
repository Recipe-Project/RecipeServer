package com.recipe.app.src.recipe.api;

import com.recipe.app.src.recipe.application.RecipeSearchService;
import com.recipe.app.src.recipe.application.RecipeService;
import com.recipe.app.src.recipe.application.dto.RecipeDetailResponse;
import com.recipe.app.src.recipe.application.dto.RecipeRequest;
import com.recipe.app.src.recipe.application.dto.RecipesResponse;
import com.recipe.app.src.recipe.application.dto.RecommendedRecipesResponse;
import com.recipe.app.src.user.domain.SecurityUser;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.user.exception.UserTokenNotExistException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Api(tags = {"레시피 Controller"})
@RestController
@RequestMapping("/recipes")
public class RecipeController {

    private final RecipeService recipeService;
    private final RecipeSearchService recipeSearchService;

    public RecipeController(RecipeService recipeService, RecipeSearchService recipeSearchService) {
        this.recipeService = recipeService;
        this.recipeSearchService = recipeSearchService;
    }

    @ApiOperation(value = "레시피 목록 조회 API")
    @GetMapping
    public RecipesResponse getRecipes(@ApiIgnore final Authentication authentication,
                                      @ApiParam(name = "keyword", type = "String", example = "감자", value = "검색어")
                                      @RequestParam(value = "keyword") String keyword,
                                      @ApiParam(name = "startAfter", type = "long", example = "0", value = "마지막 조회 레시피 아이디")
                                      @RequestParam(value = "startAfter") long startAfter,
                                      @ApiParam(name = "size", type = "int", example = "20", value = "사이즈")
                                      @RequestParam(value = "size") int size,
                                      @ApiParam(name = "sort", type = "String", example = "조회수순(recipeViews) / 좋아요순(recipeScraps) / 최신순(newest) = 기본값", value = "정렬")
                                      @RequestParam(value = "sort") String sort) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        return recipeSearchService.findRecipesByKeywordOrderBy(user, keyword, startAfter, size, sort);
    }

    @ApiOperation(value = "레시피 상세 조회 API")
    @GetMapping("/{recipeId}")
    public RecipeDetailResponse getRecipe(@ApiIgnore final Authentication authentication, @PathVariable long recipeId) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        recipeService.createRecipeView(user, recipeId);

        return recipeSearchService.findRecipeDetail(user, recipeId);
    }

    @ApiOperation(value = "레시피 스크랩 생성 API")
    @PostMapping("/{recipeId}/scraps")
    public void postRecipeScrap(@ApiIgnore final Authentication authentication, @PathVariable long recipeId) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        recipeService.createRecipeScrap(user, recipeId);
    }

    @ApiOperation(value = "레시피 스크랩 삭제 API")
    @DeleteMapping("/{recipeId}/scraps")
    public void deleteRecipeScrap(@ApiIgnore final Authentication authentication, @PathVariable long recipeId) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        recipeService.deleteRecipeScrap(user, recipeId);
    }

    @ApiOperation(value = "레시피 스크랩 목록 조회 API")
    @GetMapping("/scraps")
    public RecipesResponse getScrapRecipes(@ApiIgnore final Authentication authentication,
                                           @ApiParam(name = "startAfter", type = "long", example = "0", value = "마지막 조회 레시피 아이디")
                                           @RequestParam(value = "startAfter") long startAfter,
                                           @ApiParam(name = "size", type = "int", example = "20", value = "사이즈")
                                           @RequestParam(value = "size") int size) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        return recipeSearchService.findScrapRecipes(user, startAfter, size);
    }

    @ApiOperation(value = "냉장고 파먹기 레시피 목록 조회 API")
    @GetMapping("/fridges-recommendation")
    public RecommendedRecipesResponse getFridgesRecipes(@ApiIgnore final Authentication authentication,
                                                        @ApiParam(name = "startAfter", type = "long", example = "0", value = "마지막 조회 레시피 아이디")
                                                        @RequestParam(value = "startAfter") long startAfter,
                                                        @ApiParam(name = "size", type = "int", example = "20", value = "사이즈")
                                                        @RequestParam(value = "size") int size) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        return recipeSearchService.findRecommendedRecipesByUserFridge(user, startAfter, size);
    }

    @ApiOperation(value = "등록한 레시피 목록 조회 API")
    @GetMapping("/users")
    public RecipesResponse getUserRecipes(@ApiIgnore final Authentication authentication,
                                          @ApiParam(name = "startAfter", type = "long", example = "0", value = "마지막 조회 레시피 아이디")
                                          @RequestParam(value = "startAfter") long startAfter,
                                          @ApiParam(name = "size", type = "int", example = "20", value = "사이즈")
                                          @RequestParam(value = "size") int size) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        return recipeSearchService.findRecipesByUser(user, startAfter, size);
    }

    @ApiOperation(value = "레시피 등록 API")
    @PostMapping("")
    public void postRecipe(@ApiIgnore final Authentication authentication,
                           @ApiParam(value = "레시피 등록 요청 정보")
                           @RequestBody RecipeRequest request) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        recipeService.createRecipe(user, request);
    }

    @ApiOperation(value = "레시피 수정 API")
    @PatchMapping("/{recipeId}")
    public void patchRecipe(@ApiIgnore final Authentication authentication, @PathVariable Long recipeId,
                            @ApiParam(value = "레시피 수정 요청 정보")
                            @RequestBody RecipeRequest request) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        recipeService.updateRecipe(user, recipeId, request);
    }

    @ApiOperation(value = "레시피 삭제 API")
    @DeleteMapping("/{recipeId}")
    public void deleteRecipe(@ApiIgnore final Authentication authentication, @PathVariable Long recipeId) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        recipeService.deleteRecipe(user, recipeId);
    }

    @ApiOperation(value = "레시피 신고 API")
    @PostMapping("/{recipeId}/reports")
    public void postRecipeReport(@ApiIgnore final Authentication authentication, @PathVariable Long recipeId) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        recipeService.createRecipeReport(user, recipeId);
    }
}