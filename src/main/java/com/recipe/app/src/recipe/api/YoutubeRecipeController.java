package com.recipe.app.src.recipe.api;

import com.recipe.app.src.common.aop.LoginCheck;
import com.recipe.app.src.recipe.application.dto.RecipesResponse;
import com.recipe.app.src.recipe.application.youtube.YoutubeRecipeService;
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

import java.io.IOException;

@Tag(name = "유튜브 레시피 Controller")
@RestController
@RequestMapping("/recipes/youtube")
public class YoutubeRecipeController {

    private final YoutubeRecipeService youtubeRecipeService;

    public YoutubeRecipeController(YoutubeRecipeService youtubeRecipeService) {
        this.youtubeRecipeService = youtubeRecipeService;
    }

    @Operation(summary = "유튜브 레시피 목록 조회 API")
    @GetMapping
    @LoginCheck
    public RecipesResponse getYoutubeRecipes(@Parameter(hidden = true) User user,
                                             @Parameter(example = "감자", name = "검색어")
                                             @RequestParam(value = "keyword") String keyword,
                                             @Parameter(example = "0", name = "마지막 조회 유튜브 레시피 아이디")
                                             @RequestParam(value = "startAfter") long startAfter,
                                             @Parameter(example = "20", name = "사이즈")
                                             @RequestParam(value = "size") int size,
                                             @Parameter(example = "조회수순(youtubeViews) / 좋아요순(youtubeScraps) / 최신순(newest) = 기본값", name = "정렬")
                                             @RequestParam(value = "sort") String sort) throws IOException {

        return youtubeRecipeService.findYoutubeRecipesByKeyword(user, keyword, startAfter, size, sort);
    }

    @Operation(summary = "유튜브 레시피 상세 조회 API")
    @PostMapping("/{youtubeRecipeId}/views")
    @LoginCheck
    public void postYoutubeRecipeView(@Parameter(hidden = true) User user, @PathVariable long youtubeRecipeId) {

        youtubeRecipeService.createYoutubeView(user, youtubeRecipeId);
    }

    @Operation(summary = "유튜브 레시피 스크랩 목록 조회 API")
    @GetMapping("/scraps")
    @LoginCheck
    public RecipesResponse getScrapYoutubeRecipes(@Parameter(hidden = true) User user,
                                                  @Parameter(example = "0", name = "마지막 조회 유튜브 레시피 아이디")
                                                  @RequestParam(value = "startAfter") long startAfter,
                                                  @Parameter(example = "20", name = "사이즈")
                                                  @RequestParam(value = "size") int size) {

        return youtubeRecipeService.findScrapYoutubeRecipes(user, startAfter, size);
    }

    @Operation(summary = "유튜브 레시피 스크랩 생성 API")
    @PostMapping("/{youtubeRecipeId}/scraps")
    @LoginCheck
    public void postYoutubeRecipeScrap(@Parameter(hidden = true) User user, @PathVariable long youtubeRecipeId) {

        youtubeRecipeService.createYoutubeScrap(user, youtubeRecipeId);
    }

    @Operation(summary = "유튜브 레시피 스크랩 삭제 API")
    @DeleteMapping("/{youtubeRecipeId}/scraps")
    @LoginCheck
    public void deleteYoutubeRecipeScrap(@Parameter(hidden = true) User user, @PathVariable long youtubeRecipeId) {

        youtubeRecipeService.deleteYoutubeScrap(user, youtubeRecipeId);
    }

}
