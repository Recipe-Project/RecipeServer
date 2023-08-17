package com.recipe.app.src.recipe.api;

import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.src.recipe.application.BlogRecipeService;
import com.recipe.app.src.recipe.application.SearchKeywordService;
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
@RequestMapping("/recipes/blog")
public class BlogRecipeController {

    private final BlogRecipeService blogRecipeService;
    private final SearchKeywordService recipeKeywordService;

    @GetMapping("")
    public BaseResponse<List<RecipeDto.RecipeResponse>> getBlogRecipes(final Authentication authentication, @RequestParam(value = "keyword") String keyword) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        List<RecipeDto.RecipeResponse> data = blogRecipeService.getBlogRecipes(keyword).stream()
                .map((recipe) -> RecipeDto.RecipeResponse.from(recipe, user))
                .collect(Collectors.toList());
        recipeKeywordService.createSearchKeyword(keyword, user);

        return success(data);
    }

    @GetMapping("/{blogRecipeId}")
    public BaseResponse<Void> getBlogRecipe(final Authentication authentication, @PathVariable Long blogRecipeId) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        blogRecipeService.createBlogView(blogRecipeId, user);

        return success();
    }

    @GetMapping("/scrap")
    public BaseResponse<List<RecipeDto.RecipeResponse>> getScrapBlogRecipes(final Authentication authentication) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        List<RecipeDto.RecipeResponse> data = blogRecipeService.getScrapBlogRecipes(user).stream()
                .map(blogRecipe -> RecipeDto.RecipeResponse.from(blogRecipe, user))
                .collect(Collectors.toList());

        return success(data);
    }

    @PostMapping("/{blogRecipeId}/scrap")
    public BaseResponse<Void> postBlogRecipeScrap(final Authentication authentication, @PathVariable Long blogRecipeId) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        blogRecipeService.createBlogRecipeScrap(blogRecipeId, user);

        return success();
    }

    @DeleteMapping("/{blogRecipeId}/scrap")
    public BaseResponse<Void> deleteBlogRecipeScrap(final Authentication authentication, @PathVariable Long blogRecipeId) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        blogRecipeService.deleteBlogRecipeScrap(blogRecipeId, user);

        return success();
    }
}
