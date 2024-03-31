package com.recipe.app.src.recipe.api;

import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.src.recipe.application.blog.BlogRecipeService;
import com.recipe.app.src.recipe.application.blog.BlogScrapService;
import com.recipe.app.src.recipe.application.blog.BlogViewService;
import com.recipe.app.src.recipe.application.dto.RecipesResponse;
import com.recipe.app.src.user.domain.SecurityUser;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.user.exception.UserTokenNotExistException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.json.simple.parser.ParseException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;

import static com.recipe.app.common.response.BaseResponse.success;

@Api(tags = {"블로그 레시피 Controller"})
@RestController
@RequestMapping("/recipes/blog")
public class BlogRecipeController {

    private final BlogRecipeService blogRecipeService;
    private final BlogViewService blogViewService;
    private final BlogScrapService blogScrapService;

    public BlogRecipeController(BlogRecipeService blogRecipeService, BlogViewService blogViewService, BlogScrapService blogScrapService) {
        this.blogRecipeService = blogRecipeService;
        this.blogViewService = blogViewService;
        this.blogScrapService = blogScrapService;
    }

    @ApiOperation(value = "블로그 레시피 목록 조회 API")
    @GetMapping("")
    public BaseResponse<RecipesResponse> getBlogRecipes(@ApiIgnore final Authentication authentication,
                                                        @ApiParam(name = "keyword", type = "String", example = "감자", value = "검색어")
                                                        @RequestParam(value = "keyword") String keyword,
                                                        @ApiParam(name = "startAfter", type = "long", example = "0", value = "마지막 조회 블로그 레시피 아이디")
                                                        @RequestParam(value = "startAfter") Long startAfter,
                                                        @ApiParam(name = "size", type = "int", example = "20", value = "사이즈")
                                                        @RequestParam(value = "size") int size,
                                                        @ApiParam(name = "sort", type = "String", example = "조회수순(blogViews) / 좋아요순(blogScraps) / 최신순(newest) = 기본값", value = "정렬")
                                                        @RequestParam(value = "sort") String sort) throws IOException, ParseException {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        return success(blogRecipeService.getBlogRecipes(user, keyword, startAfter, size, sort));
    }

    @ApiOperation(value = "블로그 레시피 상세 조회 API", notes = "블로그 레시피 상세 조회 시 조회수 올리기 위한 API")
    @PostMapping("/{blogRecipeId}/views")
    public BaseResponse<Void> postBlogRecipeView(@ApiIgnore final Authentication authentication, @PathVariable Long blogRecipeId) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        blogViewService.createBlogView(user, blogRecipeId);

        return success();
    }

    @ApiOperation(value = "블로그 레시피 스크랩 목록 조회 API")
    @GetMapping("/scraps")
    public BaseResponse<RecipesResponse> getScrapBlogRecipes(@ApiIgnore final Authentication authentication,
                                                             @ApiParam(name = "startAfter", type = "long", example = "0", value = "페이지")
                                                             @RequestParam(value = "startAfter") Long startAfter,
                                                             @ApiParam(name = "size", type = "int", example = "20", value = "사이즈")
                                                             @RequestParam(value = "size") int size) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        return success(blogRecipeService.getScrapBlogRecipes(user, startAfter, size));
    }

    @ApiOperation(value = "블로그 레시피 스크랩 생성 API")
    @PostMapping("/{blogRecipeId}/scraps")
    public BaseResponse<Void> postBlogRecipeScrap(@ApiIgnore final Authentication authentication, @PathVariable Long blogRecipeId) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        blogScrapService.createBlogScrap(user, blogRecipeId);

        return success();
    }

    @ApiOperation(value = "블로그 레시피 스크랩 삭제 API")
    @DeleteMapping("/{blogRecipeId}/scraps")
    public BaseResponse<Void> deleteBlogRecipeScrap(@ApiIgnore final Authentication authentication, @PathVariable Long blogRecipeId) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        blogScrapService.deleteBlogScrap(user, blogRecipeId);

        return success();
    }
}
