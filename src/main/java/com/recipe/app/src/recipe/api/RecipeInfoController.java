package com.recipe.app.src.recipe.api;


import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.src.ingredient.application.IngredientService;
import com.recipe.app.src.ingredient.domain.Ingredient;
import com.recipe.app.src.ingredient.infra.IngredientEntity;
import com.recipe.app.src.recipe.application.RecipeInfoService;
import com.recipe.app.src.recipe.application.dto.RecipeInfoDto;
import com.recipe.app.src.keyword.application.RecipeKeywordService;
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
public class RecipeInfoController {

    private final RecipeInfoService recipeInfoService;
    private final RecipeKeywordService recipeKeywordService;
    private final IngredientService ingredientService;

    @GetMapping("")
    public BaseResponse<List<RecipeInfoDto.RecipeResponse>> getRecipes(final Authentication authentication, @RequestParam(value = "keyword") String keyword) {
        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        List<RecipeInfoDto.RecipeResponse> data = recipeInfoService.retrieveRecipes(keyword).stream()
                .map((recipe) -> new RecipeInfoDto.RecipeResponse(recipe, user))
                .collect(Collectors.toList());
        recipeKeywordService.createRecipeKeyword(keyword);

        return success(data);
    }

    @GetMapping("/{recipeIdx}")
    public BaseResponse<RecipeInfoDto.RecipeDetailResponse> getRecipe(final Authentication authentication, @PathVariable Integer recipeIdx) {
        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        List<Ingredient> ingredients = ingredientService.getIngredients(null, user);
        RecipeInfoDto.RecipeDetailResponse data = new RecipeInfoDto.RecipeDetailResponse(recipeInfoService.retrieveRecipeInfo(recipeIdx), user, ingredients);

        return success(data);
    }

    @GetMapping("/blog")
    public BaseResponse<RecipeInfoDto.BlogRecipesResponse> getBlogRecipes(final Authentication authentication, @RequestParam(value = "keyword") String keyword, @RequestParam(value = "display") Integer display, @RequestParam(value = "start") Integer start) {
        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        RecipeInfoDto.BlogRecipesResponse data = new RecipeInfoDto.BlogRecipesResponse(50, recipeInfoService.retrieveBlogRecipes(user, keyword, display, start));
        recipeKeywordService.createRecipeKeyword(keyword);

        return success(data);
    }


}