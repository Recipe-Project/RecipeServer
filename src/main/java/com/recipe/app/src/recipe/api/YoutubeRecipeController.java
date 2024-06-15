package com.recipe.app.src.recipe.api;

import com.recipe.app.src.recipe.application.dto.RecipesResponse;
import com.recipe.app.src.recipe.application.youtube.YoutubeRecipeService;
import com.recipe.app.src.user.domain.SecurityUser;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.user.exception.UserTokenNotExistException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
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
    public RecipesResponse getYoutubeRecipes(@ApiIgnore final Authentication authentication,
                                                           @ApiParam(name = "keyword", type = "String", example = "감자", value = "검색어")
                                                           @RequestParam(value = "keyword") String keyword,
                                                           @ApiParam(name = "startAfter", type = "long", example = "0", value = "마지막 조회 유튜브 레시피 아이디")
                                                           @RequestParam(value = "startAfter", required = false) Long startAfter,
                                                           @ApiParam(name = "size", type = "int", example = "20", value = "사이즈")
                                                           @RequestParam(value = "size") int size,
                                                           @ApiParam(name = "sort", type = "String", example = "조회수순(youtubeViews) / 좋아요순(youtubeScraps) / 최신순(newest) = 기본값", value = "정렬")
                                                           @RequestParam(value = "sort") String sort) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        return youtubeRecipeService.getYoutubeRecipes(user, keyword, startAfter, size, sort);
    }

    @ApiOperation(value = "유튜브 레시피 상세 조회 API")
    @PostMapping("/{youtubeRecipeId}/views")
    public void postYoutubeRecipeView(@ApiIgnore final Authentication authentication, @PathVariable Long youtubeRecipeId) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        youtubeRecipeService.createYoutubeView(user, youtubeRecipeId);
    }

    @ApiOperation(value = "유튜브 레시피 스크랩 목록 조회 API")
    @GetMapping("/scraps")
    public RecipesResponse getScrapYoutubeRecipes(@ApiIgnore final Authentication authentication,
                                                                @ApiParam(name = "startAfter", type = "int", example = "0", value = "마지막 조회 유튜브 레시피 아이디")
                                                                @RequestParam(value = "startAfter") Long startAfter,
                                                                @ApiParam(name = "size", type = "int", example = "20", value = "사이즈")
                                                                @RequestParam(value = "size") int size) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        return youtubeRecipeService.getScrapYoutubeRecipes(user, startAfter, size);
    }

    @ApiOperation(value = "유튜브 레시피 스크랩 생성 API")
    @PostMapping("/{youtubeRecipeId}/scraps")
    public void postYoutubeRecipeScrap(@ApiIgnore final Authentication authentication, @PathVariable Long youtubeRecipeId) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        youtubeRecipeService.createYoutubeScrap(user, youtubeRecipeId);
    }

    @ApiOperation(value = "유튜브 레시피 스크랩 삭제 API")
    @DeleteMapping("/{youtubeRecipeId}/scraps")
    public void deleteYoutubeRecipeScrap(@ApiIgnore final Authentication authentication, @PathVariable Long youtubeRecipeId) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        youtubeRecipeService.deleteYoutubeScrap(user, youtubeRecipeId);
    }

}
