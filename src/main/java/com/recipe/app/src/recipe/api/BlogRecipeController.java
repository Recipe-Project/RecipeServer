package com.recipe.app.src.recipe.api;

import com.recipe.app.src.recipe.application.blog.BlogRecipeService;
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
import java.io.UnsupportedEncodingException;

@Api(tags = {"블로그 레시피 Controller"})
@RestController
@RequestMapping("/recipes/blog")
public class BlogRecipeController {

    private final BlogRecipeService blogRecipeService;

    public BlogRecipeController(BlogRecipeService blogRecipeService) {
        this.blogRecipeService = blogRecipeService;
    }

    @ApiOperation(value = "블로그 레시피 목록 조회 API")
    @GetMapping("")
    public RecipesResponse getBlogRecipes(@ApiIgnore final Authentication authentication,
                                                        @ApiParam(name = "keyword", type = "String", example = "감자", value = "검색어")
                                                        @RequestParam(value = "keyword") String keyword,
                                                        @ApiParam(name = "startAfter", type = "long", example = "0", value = "마지막 조회 블로그 레시피 아이디")
                                                        @RequestParam(value = "startAfter", required = false) Long startAfter,
                                                        @ApiParam(name = "size", type = "int", example = "20", value = "사이즈")
                                                        @RequestParam(value = "size") int size,
                                                        @ApiParam(name = "sort", type = "String", example = "조회수순(blogViews) / 좋아요순(blogScraps) / 최신순(newest) = 기본값", value = "정렬")
                                                        @RequestParam(value = "sort") String sort) throws UnsupportedEncodingException {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        return blogRecipeService.getBlogRecipes(user, keyword, startAfter, size, sort);
    }

    @ApiOperation(value = "블로그 레시피 상세 조회 API", notes = "블로그 레시피 상세 조회 시 조회수 올리기 위한 API")
    @PostMapping("/{blogRecipeId}/views")
    public void postBlogRecipeView(@ApiIgnore final Authentication authentication, @PathVariable Long blogRecipeId) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        blogRecipeService.createBlogView(user, blogRecipeId);
    }

    @ApiOperation(value = "블로그 레시피 스크랩 목록 조회 API")
    @GetMapping("/scraps")
    public RecipesResponse getScrapBlogRecipes(@ApiIgnore final Authentication authentication,
                                                             @ApiParam(name = "startAfter", type = "long", example = "0", value = "마지막 조회 블로그 레시피 아이디")
                                                             @RequestParam(value = "startAfter", required = false) Long startAfter,
                                                             @ApiParam(name = "size", type = "int", example = "20", value = "사이즈")
                                                             @RequestParam(value = "size") int size) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        return blogRecipeService.getScrapBlogRecipes(user, startAfter, size);
    }

    @ApiOperation(value = "블로그 레시피 스크랩 생성 API")
    @PostMapping("/{blogRecipeId}/scraps")
    public void postBlogRecipeScrap(@ApiIgnore final Authentication authentication, @PathVariable Long blogRecipeId) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        blogRecipeService.createBlogScrap(user, blogRecipeId);
    }

    @ApiOperation(value = "블로그 레시피 스크랩 삭제 API")
    @DeleteMapping("/{blogRecipeId}/scraps")
    public void deleteBlogRecipeScrap(@ApiIgnore final Authentication authentication, @PathVariable Long blogRecipeId) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        blogRecipeService.deleteBlogScrap(user, blogRecipeId);
    }
}
