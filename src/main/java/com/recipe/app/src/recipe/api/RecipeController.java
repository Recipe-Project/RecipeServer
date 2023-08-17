package com.recipe.app.src.recipe.api;

import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.src.recipe.application.RecipeService;
import com.recipe.app.src.recipe.application.SearchKeywordService;
import com.recipe.app.src.recipe.application.dto.RecipeDto;
import com.recipe.app.src.recipe.domain.Recipe;
import com.recipe.app.src.user.domain.SecurityUser;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.user.exception.UserTokenNotExistException;
import lombok.RequiredArgsConstructor;
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
        Recipe recipe = recipeService.getRecipe(recipeId);
        recipeService.createRecipeView(recipeId, user);
        RecipeDto.RecipeDetailResponse data = RecipeDto.RecipeDetailResponse.from(recipe, user);

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

    /*
    @GetMapping("/fridges-recommendation")
    public BaseResponse<List<RecipeDto.RecipeResponse>> getFridgesRecips(final Authentication authentication, @RequestParam(value = "start") Integer start, @RequestParam(value = "display") Integer display) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        FridgeDto.FridgeRecipesResponse data = new FridgeDto.FridgeRecipesResponse(fridgeService.countFridgeRecipes(user), fridgeService.retrieveFridgeRecipes(user, start, display));

        return success(data);
    }
     */
}