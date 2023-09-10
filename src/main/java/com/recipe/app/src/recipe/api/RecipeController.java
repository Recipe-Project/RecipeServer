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
    public BaseResponse<List<RecipeDto.RecipeResponse>> getRecipes(final Authentication authentication, @RequestParam(value = "keyword") String keyword) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        List<RecipeDto.RecipeResponse> data = recipeService.getRecipes(keyword).stream()
                .map((recipe) -> RecipeDto.RecipeResponse.from(recipe, user))
                .collect(Collectors.toList());
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