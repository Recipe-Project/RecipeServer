package com.recipe.app.src.recipe.api;

import com.recipe.app.src.common.aop.LoginCheck;
import com.recipe.app.src.recipe.application.blog.BlogRecipeService;
import com.recipe.app.src.recipe.application.dto.RecipesResponse;
import com.recipe.app.src.user.domain.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

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
    @LoginCheck
    public RecipesResponse getBlogRecipes(@ApiIgnore User user,
                                          @ApiParam(name = "keyword", type = "String", example = "감자", value = "검색어")
                                          @RequestParam(value = "keyword") String keyword,
                                          @ApiParam(name = "startAfter", type = "long", example = "0", value = "마지막 조회 블로그 레시피 아이디")
                                          @RequestParam(value = "startAfter") long startAfter,
                                          @ApiParam(name = "size", type = "int", example = "20", value = "사이즈")
                                          @RequestParam(value = "size") int size,
                                          @ApiParam(name = "sort", type = "String", example = "조회수순(blogViews) / 좋아요순(blogScraps) / 최신순(newest) = 기본값", value = "정렬")
                                          @RequestParam(value = "sort") String sort) {

        return blogRecipeService.findBlogRecipesByKeyword(user, keyword, startAfter, size, sort);
    }

    @ApiOperation(value = "블로그 레시피 상세 조회 API", notes = "블로그 레시피 상세 조회 시 조회수 올리기 위한 API")
    @PostMapping("/{blogRecipeId}/views")
    @LoginCheck
    public void postBlogRecipeView(@ApiIgnore User user, @PathVariable long blogRecipeId) {

        blogRecipeService.createBlogView(user, blogRecipeId);
    }

    @ApiOperation(value = "블로그 레시피 스크랩 목록 조회 API")
    @GetMapping("/scraps")
    @LoginCheck
    public RecipesResponse getScrapBlogRecipes(@ApiIgnore User user,
                                               @ApiParam(name = "startAfter", type = "long", example = "0", value = "마지막 조회 블로그 레시피 아이디")
                                               @RequestParam(value = "startAfter") long startAfter,
                                               @ApiParam(name = "size", type = "int", example = "20", value = "사이즈")
                                               @RequestParam(value = "size") int size) {

        return blogRecipeService.findScrapBlogRecipes(user, startAfter, size);
    }

    @ApiOperation(value = "블로그 레시피 스크랩 생성 API")
    @PostMapping("/{blogRecipeId}/scraps")
    @LoginCheck
    public void postBlogRecipeScrap(@ApiIgnore User user, @PathVariable long blogRecipeId) {

        blogRecipeService.createBlogScrap(user, blogRecipeId);
    }

    @ApiOperation(value = "블로그 레시피 스크랩 삭제 API")
    @DeleteMapping("/{blogRecipeId}/scraps")
    @LoginCheck
    public void deleteBlogRecipeScrap(@ApiIgnore User user, @PathVariable long blogRecipeId) {

        blogRecipeService.deleteBlogScrap(user, blogRecipeId);
    }
}
