package com.recipe.app.src.recipe.api;

import com.recipe.app.src.common.aop.LoginCheck;
import com.recipe.app.src.recipe.application.blog.BlogRecipeService;
import com.recipe.app.src.recipe.application.dto.RecipesResponse;
import com.recipe.app.src.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "블로그 레시피 Controller")
@RestController
@RequestMapping("/recipes/blog")
public class BlogRecipeController {

    private final BlogRecipeService blogRecipeService;

    public BlogRecipeController(BlogRecipeService blogRecipeService) {
        this.blogRecipeService = blogRecipeService;
    }

    @Operation(summary = "블로그 레시피 목록 조회 API")
    @GetMapping("")
    @LoginCheck
    public RecipesResponse getBlogRecipes(@Parameter(hidden = true) User user,
                                          @Parameter(name = "검색어", example = "감자")
                                          @RequestParam(value = "keyword") String keyword,
                                          @Parameter(example = "0", name = "마지막 조회 블로그 레시피 아이디")
                                          @RequestParam(value = "startAfter") long startAfter,
                                          @Parameter(example = "20", name = "사이즈")
                                          @RequestParam(value = "size") int size,
                                          @Parameter(example = "조회수순(views) / 좋아요순(scraps) / 최신순(newest) = 기본값", name = "정렬")
                                          @RequestParam(value = "sort") String sort) {

        return blogRecipeService.findBlogRecipesByKeyword(user, keyword, startAfter, size, sort);
    }

    @Operation(summary = "블로그 레시피 상세 조회 API", description = "블로그 레시피 상세 조회 시 조회수 올리기 위한 API")
    @PostMapping("/{blogRecipeId}/views")
    @LoginCheck
    public void postBlogRecipeView(@Parameter(hidden = true) User user, @PathVariable long blogRecipeId) {

        blogRecipeService.createBlogView(user, blogRecipeId);
    }

    @Operation(summary = "블로그 레시피 스크랩 목록 조회 API")
    @GetMapping("/scraps")
    @LoginCheck
    public RecipesResponse getScrapBlogRecipes(@Parameter(hidden = true) User user,
                                               @Parameter(example = "0", name = "마지막 조회 블로그 레시피 아이디")
                                               @RequestParam(value = "startAfter") long startAfter,
                                               @Parameter(example = "20", name = "사이즈")
                                               @RequestParam(value = "size") int size) {

        return blogRecipeService.findScrapBlogRecipes(user, startAfter, size);
    }

    @Operation(summary = "블로그 레시피 스크랩 생성 API")
    @PostMapping("/{blogRecipeId}/scraps")
    @LoginCheck
    public void postBlogRecipeScrap(@Parameter(hidden = true) User user, @PathVariable long blogRecipeId) {

        blogRecipeService.createBlogScrap(user, blogRecipeId);
    }

    @Operation(summary = "블로그 레시피 스크랩 삭제 API")
    @DeleteMapping("/{blogRecipeId}/scraps")
    @LoginCheck
    public void deleteBlogRecipeScrap(@Parameter(hidden = true) User user, @PathVariable long blogRecipeId) {

        blogRecipeService.deleteBlogScrap(user, blogRecipeId);
    }
}
