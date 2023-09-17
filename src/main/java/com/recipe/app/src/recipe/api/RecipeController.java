package com.recipe.app.src.recipe.api;

import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.src.fridge.application.FridgeService;
import com.recipe.app.src.fridge.domain.Fridge;
import com.recipe.app.src.recipe.application.RecipeService;
import com.recipe.app.src.recipe.application.SearchKeywordService;
import com.recipe.app.src.recipe.application.dto.RecipeDto;
import com.recipe.app.src.recipe.domain.Recipe;
import com.recipe.app.src.recipe.domain.RecipeIngredient;
import com.recipe.app.src.recipe.domain.RecipeProcess;
import com.recipe.app.src.user.domain.SecurityUser;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.user.exception.UserTokenNotExistException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.recipe.app.common.response.BaseResponse.success;

@Api(tags = {"레시피 Controller"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/recipes")
public class RecipeController {

    private final RecipeService recipeService;
    private final SearchKeywordService recipeKeywordService;
    private final FridgeService fridgeService;

    @ApiOperation(value = "레시피 목록 조회 API")
    @GetMapping("")
    public BaseResponse<RecipeDto.RecipesResponse> getRecipes(@ApiIgnore final Authentication authentication,
                                                              @ApiParam(name = "keyword", type = "String", example = "감자", value = "검색어")
                                                              @RequestParam(value = "keyword") String keyword,
                                                              @ApiParam(name = "page", type = "int", example = "0", value = "페이지")
                                                              @RequestParam(value = "page") int page,
                                                              @ApiParam(name = "size", type = "int", example = "20", value = "사이즈")
                                                              @RequestParam(value = "size") int size,
                                                              @ApiParam(name = "sort", type = "String", example = "조회수순(recipeViews) / 좋아요순(recipeScraps) / 최신순(createdAt) = 기본값", value = "정렬")
                                                              @RequestParam(value = "sort") String sort) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        Page<Recipe> recipes = recipeService.getRecipes(keyword, page, size, sort);
        RecipeDto.RecipesResponse data = new RecipeDto.RecipesResponse(recipes.getTotalElements(), recipes.stream()
                .map((recipe) -> RecipeDto.RecipeResponse.from(recipe, user))
                .collect(Collectors.toList()));
        recipeKeywordService.createSearchKeyword(keyword, user);

        return success(data);
    }

    @ApiOperation(value = "레시피 상세 조회 API")
    @GetMapping("/{recipeId}")
    public BaseResponse<RecipeDto.RecipeDetailResponse> getRecipe(@ApiIgnore final Authentication authentication, @PathVariable Long recipeId) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        List<Fridge> fridges = fridgeService.getFridges(user);
        Recipe recipe = recipeService.getRecipe(recipeId, user.getUserId());
        List<RecipeIngredient> recipeIngredients = recipeService.getRecipeIngredientsByRecipe(recipe);
        List<RecipeProcess> recipeProcesses = recipeService.getRecipeProcessesByRecipe(recipe);
        recipeService.createRecipeView(recipeId, user);
        RecipeDto.RecipeDetailResponse data = RecipeDto.RecipeDetailResponse.from(recipe, user, fridges, recipeIngredients, recipeProcesses);

        return success(data);
    }

    @ApiOperation(value = "레시피 스크랩 생성 API")
    @PostMapping("/{recipeId}/scrap")
    public BaseResponse<Void> postRecipeScrap(@ApiIgnore final Authentication authentication, @PathVariable Long recipeId) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        recipeService.createRecipeScrap(recipeId, user);

        return success();
    }

    @ApiOperation(value = "레시피 스크랩 삭제 API")
    @DeleteMapping("/{recipeId}/scrap")
    public BaseResponse<Void> deleteRecipeScrap(@ApiIgnore final Authentication authentication, @PathVariable Long recipeId) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        recipeService.deleteRecipeScrap(recipeId, user);

        return success();
    }

    @ApiOperation(value = "레시피 스크랩 목록 조회 API")
    @GetMapping("/scrap")
    public BaseResponse<RecipeDto.RecipesResponse> getScrapRecipes(@ApiIgnore final Authentication authentication,
                                                                   @ApiParam(name = "page", type = "int", example = "0", value = "페이지")
                                                                   @RequestParam(value = "page") int page,
                                                                   @ApiParam(name = "size", type = "int", example = "20", value = "사이즈")
                                                                   @RequestParam(value = "size") int size) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        Page<Recipe> recipes = recipeService.getScrapRecipes(user, page, size);
        RecipeDto.RecipesResponse data = new RecipeDto.RecipesResponse(recipes.getTotalElements(), recipes.stream()
                .map((recipe) -> RecipeDto.RecipeResponse.from(recipe, user))
                .collect(Collectors.toList()));

        return success(data);
    }

    @ApiOperation(value = "냉장고 파먹기 레시피 목록 조회 API")
    @GetMapping("/fridges-recommendation")
    public BaseResponse<RecipeDto.RecipesResponse> getFridgesRecipes(@ApiIgnore final Authentication authentication,
                                                                     @ApiParam(name = "page", type = "int", example = "0", value = "페이지")
                                                                     @RequestParam(value = "page") int page,
                                                                     @ApiParam(name = "size", type = "int", example = "20", value = "사이즈")
                                                                     @RequestParam(value = "size") int size) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        Page<Recipe> recipes = recipeService.retrieveFridgeRecipes(user, page, size);
        Map<Recipe, Integer> ingredientsMatchRateMapByRecipe = recipeService.getIngredientsMatchRateByRecipes(user, recipes.toList());
        RecipeDto.RecipesResponse data = new RecipeDto.RecipesResponse(recipes.getTotalElements(), recipes.stream()
                .map((recipe) -> RecipeDto.RecipeResponse.from(recipe, user, ingredientsMatchRateMapByRecipe.get(recipe)))
                .collect(Collectors.toList()));

        return success(data);
    }

    @ApiOperation(value = "등록한 레시피 목록 조회 API")
    @GetMapping("/users")
    public BaseResponse<RecipeDto.RecipesResponse> getUserRecipes(@ApiIgnore final Authentication authentication,
                                                                        @ApiParam(name = "page", type = "int", example = "0", value = "페이지")
                                                                        @RequestParam(value = "page") int page,
                                                                        @ApiParam(name = "size", type = "int", example = "20", value = "사이즈")
                                                                        @RequestParam(value = "size") int size) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        Page<Recipe> recipes = recipeService.getRecipesByUser(user, page, size);
        RecipeDto.RecipesResponse data = new RecipeDto.RecipesResponse(recipes.getTotalElements(), recipes.stream()
                .map((recipe) -> RecipeDto.RecipeResponse.from(recipe, user))
                .collect(Collectors.toList()));

        return success(data);
    }

    @ApiOperation(value = "레시피 등록 API")
    @PostMapping("")
    public BaseResponse<Void> postRecipe(@ApiIgnore final Authentication authentication,
                                         @ApiParam(value = "레시피 등록 요청 정보")
                                         @RequestBody RecipeDto.RecipeRequest request) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        recipeService.createRecipe(user, request);

        return success();
    }

    @ApiOperation(value = "레시피 수정 API")
    @PatchMapping("/{recipeId}")
    public BaseResponse<Void> patchRecipe(@ApiIgnore final Authentication authentication, @PathVariable Long recipeId,
                                          @ApiParam(value = "레시피 수정 요청 정보")
                                          @RequestBody RecipeDto.RecipeRequest request) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        recipeService.updateRecipe(user, recipeId, request);

        return success();
    }

    @ApiOperation(value = "레시피 삭제 API")
    @DeleteMapping("/{recipeId}")
    public BaseResponse<Void> deleteRecipe(@ApiIgnore final Authentication authentication, @PathVariable Long recipeId) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        recipeService.deleteRecipe(user, recipeId);

        return success();
    }
}