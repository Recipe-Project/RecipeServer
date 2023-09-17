package com.recipe.app.src.recipe.api;

import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.src.fridge.application.FridgeService;
import com.recipe.app.src.fridge.domain.Fridge;
import com.recipe.app.src.recipe.application.RecipeService;
import com.recipe.app.src.recipe.application.SearchKeywordService;
import com.recipe.app.src.recipe.application.dto.RecipeDto;
import com.recipe.app.src.recipe.domain.Recipe;
import com.recipe.app.src.user.domain.SecurityUser;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.user.exception.UserTokenNotExistException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.recipe.app.common.response.BaseResponse.success;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recipes")
public class RecipeController {

    private final RecipeService recipeService;
    private final SearchKeywordService recipeKeywordService;
    private final FridgeService fridgeService;

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

    @GetMapping("/{recipeId}")
    public BaseResponse<RecipeDto.RecipeDetailResponse> getRecipe(final Authentication authentication, @PathVariable Long recipeId) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        List<Fridge> fridges = fridgeService.getFridges(user);
        Recipe recipe = recipeService.getRecipe(recipeId);
        recipeService.createRecipeView(recipeId, user);
        RecipeDto.RecipeDetailResponse data = RecipeDto.RecipeDetailResponse.from(recipe, user, fridges);

        return success(data);
    }

    @PostMapping("/{recipeId}/scrap")
    public BaseResponse<Void> postRecipeScrap(final Authentication authentication, @PathVariable Long recipeId) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        recipeService.createRecipeScrap(recipeId, user);

        return success();
    }

    @DeleteMapping("/{recipeId}/scrap")
    public BaseResponse<Void> deleteRecipeScrap(final Authentication authentication, @PathVariable Long recipeId) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        recipeService.deleteRecipeScrap(recipeId, user);

        return success();
    }

    @GetMapping("/scrap")
    public BaseResponse<List<RecipeDto.RecipeResponse>> getScrapRecipes(final Authentication authentication) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        List<RecipeDto.RecipeResponse> data = recipeService.getScrapRecipes(user).stream()
                .map((recipe) -> RecipeDto.RecipeResponse.from(recipe, user))
                .collect(Collectors.toList());

        return success(data);
    }

    @GetMapping("/fridges-recommendation")
    public BaseResponse<List<RecipeDto.RecipeResponse>> getFridgesRecipes(final Authentication authentication, Pageable pageable) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        List<RecipeDto.RecipeResponse> data = recipeService.retrieveFridgeRecipes(user, pageable).stream()
                .map((recipe) -> RecipeDto.RecipeResponse.from(recipe, user))
                .collect(Collectors.toList());

        return success(data);
    }

    @GetMapping("/registration")
    public BaseResponse<List<RecipeDto.RecipeResponse>> getRegisteredRecipes(final Authentication authentication) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        List<RecipeDto.RecipeResponse> data = recipeService.getRecipesByUser(user).stream()
                .map((recipe) -> RecipeDto.RecipeResponse.from(recipe, user))
                .collect(Collectors.toList());

        return success(data);
    }

    @PostMapping("")
    public BaseResponse<Void> postRecipe(final Authentication authentication, @RequestBody RecipeDto.RecipeRequest request) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        recipeService.createRecipe(user, request);

        return success();
    }

    @PatchMapping("/{recipeId}")
    public BaseResponse<Void> patchRecipe(final Authentication authentication, @PathVariable Long recipeId, @RequestBody RecipeDto.RecipeRequest request) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        recipeService.updateRecipe(user, recipeId, request);

        return success();
    }

    @DeleteMapping("/{recipeId}")
    public BaseResponse<Void> deleteRecipe(final Authentication authentication, @PathVariable Long recipeId) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        recipeService.deleteRecipe(user, recipeId);

        return success();
    }
}