package com.recipe.app.src.recipe.api;

import com.recipe.app.src.common.aop.LoginCheck;
import com.recipe.app.src.recipe.application.dto.RecipesResponse;
import com.recipe.app.src.recipe.application.youtube.YoutubeRecipeService;
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

import java.io.IOException;

@Api(tags = {"유튜브 레시피 Controller"})
@RestController
@RequestMapping("/recipes/youtube")
public class YoutubeRecipeController {

    private final YoutubeRecipeService youtubeRecipeService;

    public YoutubeRecipeController(YoutubeRecipeService youtubeRecipeService) {
        this.youtubeRecipeService = youtubeRecipeService;
    }

    @ApiOperation(value = "유튜브 레시피 목록 조회 API")
    @GetMapping
    @LoginCheck
    public RecipesResponse getYoutubeRecipes(@ApiIgnore User user,
                                             @ApiParam(name = "keyword", type = "String", example = "감자", value = "검색어")
                                             @RequestParam(value = "keyword") String keyword,
                                             @ApiParam(name = "startAfter", type = "long", example = "0", value = "마지막 조회 유튜브 레시피 아이디")
                                             @RequestParam(value = "startAfter") long startAfter,
                                             @ApiParam(name = "size", type = "int", example = "20", value = "사이즈")
                                             @RequestParam(value = "size") int size,
                                             @ApiParam(name = "sort", type = "String", example = "조회수순(youtubeViews) / 좋아요순(youtubeScraps) / 최신순(newest) = 기본값", value = "정렬")
                                             @RequestParam(value = "sort") String sort) throws IOException {

        return youtubeRecipeService.findYoutubeRecipesByKeyword(user, keyword, startAfter, size, sort);
    }

    @ApiOperation(value = "유튜브 레시피 상세 조회 API")
    @PostMapping("/{youtubeRecipeId}/views")
    @LoginCheck
    public void postYoutubeRecipeView(@ApiIgnore User user, @PathVariable long youtubeRecipeId) {

        youtubeRecipeService.createYoutubeView(user, youtubeRecipeId);
    }

    @ApiOperation(value = "유튜브 레시피 스크랩 목록 조회 API")
    @GetMapping("/scraps")
    @LoginCheck
    public RecipesResponse getScrapYoutubeRecipes(@ApiIgnore User user,
                                                  @ApiParam(name = "startAfter", type = "int", example = "0", value = "마지막 조회 유튜브 레시피 아이디")
                                                  @RequestParam(value = "startAfter") long startAfter,
                                                  @ApiParam(name = "size", type = "int", example = "20", value = "사이즈")
                                                  @RequestParam(value = "size") int size) {

        return youtubeRecipeService.findScrapYoutubeRecipes(user, startAfter, size);
    }

    @ApiOperation(value = "유튜브 레시피 스크랩 생성 API")
    @PostMapping("/{youtubeRecipeId}/scraps")
    @LoginCheck
    public void postYoutubeRecipeScrap(@ApiIgnore User user, @PathVariable long youtubeRecipeId) {

        youtubeRecipeService.createYoutubeScrap(user, youtubeRecipeId);
    }

    @ApiOperation(value = "유튜브 레시피 스크랩 삭제 API")
    @DeleteMapping("/{youtubeRecipeId}/scraps")
    @LoginCheck
    public void deleteYoutubeRecipeScrap(@ApiIgnore User user, @PathVariable long youtubeRecipeId) {

        youtubeRecipeService.deleteYoutubeScrap(user, youtubeRecipeId);
    }

}
