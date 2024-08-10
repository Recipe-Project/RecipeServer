package com.recipe.app.src.recipe.api;

import com.recipe.app.src.common.aop.LoginCheck;
import com.recipe.app.src.recipe.application.RecipeSearchService;
import com.recipe.app.src.recipe.application.RecipeService;
import com.recipe.app.src.recipe.application.dto.RecipeDetailResponse;
import com.recipe.app.src.recipe.application.dto.RecipeRequest;
import com.recipe.app.src.recipe.application.dto.RecipesResponse;
import com.recipe.app.src.recipe.application.dto.RecommendedRecipesResponse;
import com.recipe.app.src.user.domain.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
    @LoginCheck
    public RecipesResponse getRecipes(@ApiIgnore User user,
                                      @ApiParam(name = "keyword", type = "String", example = "감자", value = "검색어")
                                      @RequestParam(value = "keyword") String keyword,
                                      @ApiParam(name = "startAfter", type = "long", example = "0", value = "마지막 조회 레시피 아이디")
                                      @RequestParam(value = "startAfter") long startAfter,
                                      @ApiParam(name = "size", type = "int", example = "20", value = "사이즈")
                                      @RequestParam(value = "size") int size,
                                      @ApiParam(name = "sort", type = "String", example = "조회수순(recipeViews) / 좋아요순(recipeScraps) / 최신순(newest) = 기본값", value = "정렬")
                                      @RequestParam(value = "sort") String sort) {

        return recipeSearchService.findRecipesByKeywordOrderBy(user, keyword, startAfter, size, sort);
    }

    @ApiOperation(value = "레시피 상세 조회 API")
    @GetMapping("/{recipeId}")
    @LoginCheck
    public RecipeDetailResponse getRecipe(@ApiIgnore User user, @PathVariable long recipeId) {

        return recipeSearchService.findRecipeDetail(user, recipeId);
    }

    @ApiOperation(value = "레시피 스크랩 생성 API")
    @PostMapping("/{recipeId}/scraps")
    @LoginCheck
    public void postRecipeScrap(@ApiIgnore User user, @PathVariable long recipeId) {

        recipeService.createRecipeScrap(user, recipeId);
    }

    @ApiOperation(value = "레시피 스크랩 삭제 API")
    @DeleteMapping("/{recipeId}/scraps")
    @LoginCheck
    public void deleteRecipeScrap(@ApiIgnore User user, @PathVariable long recipeId) {

        recipeService.deleteRecipeScrap(user, recipeId);
    }

    @ApiOperation(value = "레시피 스크랩 목록 조회 API")
    @GetMapping("/scraps")
    @LoginCheck
    public RecipesResponse getScrapRecipes(@ApiIgnore User user,
                                           @ApiParam(name = "startAfter", type = "long", example = "0", value = "마지막 조회 레시피 아이디")
                                           @RequestParam(value = "startAfter") long startAfter,
                                           @ApiParam(name = "size", type = "int", example = "20", value = "사이즈")
                                           @RequestParam(value = "size") int size) {

        return recipeSearchService.findScrapRecipes(user, startAfter, size);
    }

    @ApiOperation(value = "냉장고 파먹기 레시피 목록 조회 API")
    @GetMapping("/fridges-recommendation")
    @LoginCheck
    public RecommendedRecipesResponse getFridgesRecipes(@ApiIgnore User user,
                                                        @ApiParam(name = "startAfter", type = "long", example = "0", value = "마지막 조회 레시피 아이디")
                                                        @RequestParam(value = "startAfter") long startAfter,
                                                        @ApiParam(name = "size", type = "int", example = "20", value = "사이즈")
                                                        @RequestParam(value = "size") int size) {

        return recipeSearchService.findRecommendedRecipesByUserFridge(user, startAfter, size);
    }

    @ApiOperation(value = "등록한 레시피 목록 조회 API")
    @GetMapping("/users")
    @LoginCheck
    public RecipesResponse getUserRecipes(@ApiIgnore User user,
                                          @ApiParam(name = "startAfter", type = "long", example = "0", value = "마지막 조회 레시피 아이디")
                                          @RequestParam(value = "startAfter") long startAfter,
                                          @ApiParam(name = "size", type = "int", example = "20", value = "사이즈")
                                          @RequestParam(value = "size") int size) {

        return recipeSearchService.findRecipesByUser(user, startAfter, size);
    }

    @ApiOperation(value = "레시피 등록 API")
    @PostMapping("")
    @LoginCheck
    public void postRecipe(@ApiIgnore User user,
                           @ApiParam(value = "레시피 등록 요청 정보")
                           @RequestBody RecipeRequest request) {

        recipeService.create(user, request);
    }

    @ApiOperation(value = "레시피 수정 API")
    @PatchMapping("/{recipeId}")
    @LoginCheck
    public void patchRecipe(@ApiIgnore User user,
                            @PathVariable Long recipeId,
                            @ApiParam(value = "레시피 수정 요청 정보")
                            @RequestBody RecipeRequest request) {

        recipeService.update(user, recipeId, request);
    }

    @ApiOperation(value = "레시피 삭제 API")
    @DeleteMapping("/{recipeId}")
    @LoginCheck
    public void deleteRecipe(@ApiIgnore User user, @PathVariable Long recipeId) {

        recipeService.delete(user, recipeId);
    }

    @ApiOperation(value = "레시피 신고 API")
    @PostMapping("/{recipeId}/reports")
    @LoginCheck
    public void postRecipeReport(@ApiIgnore User user, @PathVariable Long recipeId) {

        recipeService.createRecipeReport(user, recipeId);
    }
}