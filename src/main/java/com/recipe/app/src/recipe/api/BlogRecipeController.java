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
import springfox.documentation.annotations.ApiIgnore;

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
    public BaseResponse<RecipeDto.RecipesResponse> getBlogRecipes(@ApiIgnore final Authentication authentication,

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        Page<BlogRecipe> blogRecipes = blogRecipeService.getBlogRecipes(keyword, page, size, sort);
        if (blogRecipes.getTotalElements() < 10) {
            blogRecipes = blogRecipeService.searchNaverBlogRecipes(keyword, page, size, sort);
        }

        RecipeDto.RecipesResponse data = new RecipeDto.RecipesResponse(blogRecipes.getTotalElements(), blogRecipes.stream()
                .map(blogRecipe -> RecipeDto.RecipeResponse.from(blogRecipe, user))
                .collect(Collectors.toList()));
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
    public BaseResponse<RecipeDto.RecipesResponse> getScrapBlogRecipes(@ApiIgnore final Authentication authentication,

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        Page<BlogRecipe> blogRecipes = blogRecipeService.getScrapBlogRecipes(user, page, size);
        RecipeDto.RecipesResponse data = new RecipeDto.RecipesResponse(blogRecipes.getTotalElements(), blogRecipes.stream()
                .map(blogRecipe -> RecipeDto.RecipeResponse.from(blogRecipe, user))
                .collect(Collectors.toList()));

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
