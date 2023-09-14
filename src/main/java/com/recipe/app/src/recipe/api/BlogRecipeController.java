package com.recipe.app.src.recipe.api;

import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.src.recipe.application.BlogRecipeService;
import com.recipe.app.src.recipe.application.SearchKeywordService;
import com.recipe.app.src.recipe.application.dto.RecipeDto;
import com.recipe.app.src.recipe.domain.BlogRecipe;
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

import java.util.stream.Collectors;

import static com.recipe.app.common.response.BaseResponse.success;

@Api(tags = {"블로그 레시피 Controller"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/recipes/blog")
public class BlogRecipeController {

    private final BlogRecipeService blogRecipeService;
    private final SearchKeywordService recipeKeywordService;

    @ApiOperation(value = "블로그 레시피 목록 조회 API")
    @GetMapping("")
    public BaseResponse<RecipeDto.RecipesResponse> getBlogRecipes(@ApiIgnore final Authentication authentication,
                                                                  @ApiParam(name = "keyword", type = "String", example = "감자", value = "검색어")
                                                                  @RequestParam(value = "keyword") String keyword,
                                                                  @ApiParam(name = "page", type = "int", example = "0", value = "페이지")
                                                                  @RequestParam(value = "page") int page,
                                                                  @ApiParam(name = "size", type = "int", example = "20", value = "사이즈")
                                                                  @RequestParam(value = "size") int size,
                                                                  @ApiParam(name = "sort", type = "String", example = "조회수순(blogViews) / 좋아요순(blogScraps) / 최신순(blogRecipeId) = 기본값", value = "정렬")
                                                                  @RequestParam(value = "sort") String sort) {

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

    @ApiOperation(value = "블로그 레시피 상세 조회 API", notes = "블로그 레시피 상세 조회 시 조회수 올리기 위한 API")
    @GetMapping("/{blogRecipeId}")
    public BaseResponse<Void> getBlogRecipe(@ApiIgnore final Authentication authentication, @PathVariable Long blogRecipeId) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        blogRecipeService.createBlogView(blogRecipeId, user);

        return success();
    }

    @ApiOperation(value = "블로그 레시피 스크랩 목록 조회 API")
    @GetMapping("/scrap")
    public BaseResponse<RecipeDto.RecipesResponse> getScrapBlogRecipes(@ApiIgnore final Authentication authentication,
                                                                       @ApiParam(name = "page", type = "int", example = "0", value = "페이지")
                                                                       @RequestParam(value = "page") int page,
                                                                       @ApiParam(name = "size", type = "int", example = "20", value = "사이즈")
                                                                       @RequestParam(value = "size") int size) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        Page<BlogRecipe> blogRecipes = blogRecipeService.getScrapBlogRecipes(user, page, size);
        RecipeDto.RecipesResponse data = new RecipeDto.RecipesResponse(blogRecipes.getTotalElements(), blogRecipes.stream()
                .map(blogRecipe -> RecipeDto.RecipeResponse.from(blogRecipe, user))
                .collect(Collectors.toList()));

        return success(data);
    }

    @ApiOperation(value = "블로그 레시피 스크랩 생성 API")
    @PostMapping("/{blogRecipeId}/scrap")
    public BaseResponse<Void> postBlogRecipeScrap(@ApiIgnore final Authentication authentication, @PathVariable Long blogRecipeId) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        blogRecipeService.createBlogRecipeScrap(blogRecipeId, user);

        return success();
    }

    @ApiOperation(value = "블로그 레시피 스크랩 삭제 API")
    @DeleteMapping("/{blogRecipeId}/scrap")
    public BaseResponse<Void> deleteBlogRecipeScrap(@ApiIgnore final Authentication authentication, @PathVariable Long blogRecipeId) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        blogRecipeService.deleteBlogRecipeScrap(blogRecipeId, user);

        return success();
    }
}
