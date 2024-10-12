package com.recipe.app.src.recipe.api;

import com.recipe.app.src.common.aop.LoginCheck;
import com.recipe.app.src.recipe.application.RecipeSearchService;
import com.recipe.app.src.recipe.application.RecipeService;
import com.recipe.app.src.recipe.application.dto.RecipeDetailResponse;
import com.recipe.app.src.recipe.application.dto.RecipeRequest;
import com.recipe.app.src.recipe.application.dto.RecipesResponse;
import com.recipe.app.src.recipe.application.dto.RecommendedRecipesResponse;
import com.recipe.app.src.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "레시피 Controller")
@RestController
@RequestMapping("/recipes")
public class RecipeController {

    private final RecipeService recipeService;
    private final RecipeSearchService recipeSearchService;

    public RecipeController(RecipeService recipeService, RecipeSearchService recipeSearchService) {
        this.recipeService = recipeService;
        this.recipeSearchService = recipeSearchService;
    }

    @Operation(summary = "레시피 목록 조회 API")
    @GetMapping
    @LoginCheck
    public RecipesResponse getRecipes(@Parameter(hidden = true) User user,
                                      @Parameter(example = "감자", name = "검색어")
                                      @RequestParam(value = "keyword") String keyword,
                                      @Parameter(example = "0", name = "마지막 조회 레시피 아이디")
                                      @RequestParam(value = "startAfter") long startAfter,
                                      @Parameter(example = "20", name = "사이즈")
                                      @RequestParam(value = "size") int size,
                                      @Parameter(example = "조회수순(views) / 좋아요순(scraps) / 최신순(newest) = 기본값", name = "정렬")
                                      @RequestParam(value = "sort") String sort) {

        return recipeSearchService.findRecipesByKeywordOrderBy(user, keyword, startAfter, size, sort);
    }

    @Operation(summary = "레시피 상세 조회 API")
    @GetMapping("/{recipeId}")
    @LoginCheck
    public RecipeDetailResponse getRecipe(@Parameter(hidden = true) User user, @PathVariable long recipeId) {

        return recipeSearchService.findRecipeDetail(user, recipeId);
    }

    @Operation(summary = "레시피 스크랩 생성 API")
    @PostMapping("/{recipeId}/scraps")
    @LoginCheck
    public void postRecipeScrap(@Parameter(hidden = true) User user, @PathVariable long recipeId) {

        recipeService.createRecipeScrap(user, recipeId);
    }

    @Operation(summary = "레시피 스크랩 삭제 API")
    @DeleteMapping("/{recipeId}/scraps")
    @LoginCheck
    public void deleteRecipeScrap(@Parameter(hidden = true) User user, @PathVariable long recipeId) {

        recipeService.deleteRecipeScrap(user, recipeId);
    }

    @Operation(summary = "레시피 스크랩 목록 조회 API")
    @GetMapping("/scraps")
    @LoginCheck
    public RecipesResponse getScrapRecipes(@Parameter(hidden = true) User user,
                                           @Parameter(example = "0", name = "마지막 조회 레시피 아이디")
                                           @RequestParam(value = "startAfter") long startAfter,
                                           @Parameter(example = "20", name = "사이즈")
                                           @RequestParam(value = "size") int size) {

        return recipeSearchService.findScrapRecipes(user, startAfter, size);
    }

    @Operation(summary = "냉장고 파먹기 레시피 목록 조회 API")
    @GetMapping("/fridges-recommendation")
    @LoginCheck
    public RecommendedRecipesResponse getFridgesRecipes(@Parameter(hidden = true) User user,
                                                        @Parameter(example = "0", name = "마지막 조회 레시피 아이디")
                                                        @RequestParam(value = "startAfter") long startAfter,
                                                        @Parameter(example = "20", name = "사이즈")
                                                        @RequestParam(value = "size") int size) {

        return recipeSearchService.findRecommendedRecipesByUserFridge(user, startAfter, size);
    }

    @Operation(summary = "등록한 레시피 목록 조회 API")
    @GetMapping("/users")
    @LoginCheck
    public RecipesResponse getUserRecipes(@Parameter(hidden = true) User user,
                                          @Parameter(example = "0", name = "마지막 조회 레시피 아이디")
                                          @RequestParam(value = "startAfter") long startAfter,
                                          @Parameter(example = "20", name = "사이즈")
                                          @RequestParam(value = "size") int size) {

        return recipeSearchService.findRecipesByUser(user, startAfter, size);
    }

    @Operation(summary = "레시피 등록 API")
    @PostMapping("")
    @LoginCheck
    public void postRecipe(@Parameter(hidden = true) User user,
                           @Parameter(name = "레시피 등록 요청 정보")
                           @RequestBody RecipeRequest request) {

        recipeService.create(user, request);
    }

    @Operation(summary = "레시피 수정 API")
    @PatchMapping("/{recipeId}")
    @LoginCheck
    public void patchRecipe(@Parameter(hidden = true) User user,
                            @PathVariable Long recipeId,
                            @Parameter(name = "레시피 수정 요청 정보")
                            @RequestBody RecipeRequest request) {

        recipeService.update(user, recipeId, request);
    }

    @Operation(summary = "레시피 삭제 API")
    @DeleteMapping("/{recipeId}")
    @LoginCheck
    public void deleteRecipe(@Parameter(hidden = true) User user, @PathVariable Long recipeId) {

        recipeService.delete(user, recipeId);
    }

    @Operation(summary = "레시피 신고 API")
    @PostMapping("/{recipeId}/reports")
    @LoginCheck
    public void postRecipeReport(@Parameter(hidden = true) User user, @PathVariable Long recipeId) {

        recipeService.createRecipeReport(user, recipeId);
    }
}