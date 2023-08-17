package com.recipe.app.src.recipe.api;

import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.src.recipe.application.SearchKeywordService;
import com.recipe.app.src.recipe.application.YoutubeRecipeService;
import com.recipe.app.src.recipe.application.dto.RecipeDto;
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
@RequestMapping("/recipes/youtube")
public class YoutubeRecipeController {

    private final YoutubeRecipeService youtubeRecipeService;
    private final SearchKeywordService recipeKeywordService;

    @GetMapping("")
    public BaseResponse<List<RecipeDto.RecipeResponse>> getYoutubeRecipes(final Authentication authentication, @RequestParam(value = "keyword") String keyword) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        List<RecipeDto.RecipeResponse> data = youtubeRecipeService.getYoutubeRecipes(keyword).stream()
                .map((recipe) -> RecipeDto.RecipeResponse.from(recipe, user))
                .collect(Collectors.toList());
        recipeKeywordService.createSearchKeyword(keyword, user);

        return success(data);
    }

    @GetMapping("/{youtubeRecipeId}")
    public BaseResponse<Void> getYoutubeRecipe(final Authentication authentication, @PathVariable Long youtubeRecipeId) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        youtubeRecipeService.createYoutubeView(youtubeRecipeId, user);

        return success();
    }

    @GetMapping("/scrap")
    public BaseResponse<List<RecipeDto.RecipeResponse>> getScrapYoutubeRecipes(final Authentication authentication) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        List<RecipeDto.RecipeResponse> data = youtubeRecipeService.getScrapYoutubeRecipes(user).stream()
                .map((youtubeRecipe) -> RecipeDto.RecipeResponse.from(youtubeRecipe, user))
                .collect(Collectors.toList());

        return success(data);
    }

    @PostMapping("/{youtubeRecipeId}/scrap")
    public BaseResponse<Void> postYoutubeRecipeScrap(final Authentication authentication, @PathVariable Long youtubeRecipeId) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        youtubeRecipeService.createYoutubeScrap(youtubeRecipeId, user);

        return success();
    }

    @DeleteMapping("/{youtubeRecipeId}/scrap")
    public BaseResponse<Void> deleteYoutubeRecipeScrap(final Authentication authentication, @PathVariable Long youtubeRecipeId) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        youtubeRecipeService.deleteYoutubeScrap(youtubeRecipeId, user);

        return success();
    }

}
