package com.recipe.app.src.recipe.api;

import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.src.recipe.application.dto.RecipesResponse;
import com.recipe.app.src.recipe.application.youtube.YoutubeRecipeService;
import com.recipe.app.src.recipe.application.youtube.YoutubeScrapService;
import com.recipe.app.src.recipe.application.youtube.YoutubeViewService;
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

import static com.recipe.app.common.response.BaseResponse.success;

@Api(tags = {"유튜브 레시피 Controller"})
@RestController
@RequestMapping("/recipes/youtube")
public class YoutubeRecipeController {

    private final YoutubeRecipeService youtubeRecipeService;
    private final YoutubeScrapService youtubeScrapService;
    private final YoutubeViewService youtubeViewService;

    public YoutubeRecipeController(YoutubeRecipeService youtubeRecipeService, YoutubeScrapService youtubeScrapService, YoutubeViewService youtubeViewService) {
        this.youtubeRecipeService = youtubeRecipeService;
        this.youtubeScrapService = youtubeScrapService;
        this.youtubeViewService = youtubeViewService;
    }

    @ApiOperation(value = "유튜브 레시피 목록 조회 API")
    @GetMapping
    public BaseResponse<RecipesResponse> getYoutubeRecipes(@ApiIgnore final Authentication authentication,
                                                           @ApiParam(name = "keyword", type = "String", example = "감자", value = "검색어")
                                                           @RequestParam(value = "keyword") String keyword,
                                                           @ApiParam(name = "page", type = "int", example = "0", value = "페이지")
                                                           @RequestParam(value = "page") int page,
                                                           @ApiParam(name = "size", type = "int", example = "20", value = "사이즈")
                                                           @RequestParam(value = "size") int size,
                                                           @ApiParam(name = "sort", type = "String", example = "조회수순(youtubeViews) / 좋아요순(youtubeScraps) / 최신순(newest) = 기본값", value = "정렬")
                                                           @RequestParam(value = "sort") String sort) throws IOException {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        return success(youtubeRecipeService.getYoutubeRecipes(user, keyword, page, size, sort));
    }

    @ApiOperation(value = "유튜브 레시피 상세 조회 API")
    @PostMapping("/{youtubeRecipeId}/views")
    public BaseResponse<Void> postYoutubeRecipeView(@ApiIgnore final Authentication authentication, @PathVariable Long youtubeRecipeId) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        youtubeViewService.createYoutubeView(user, youtubeRecipeId);

        return success();
    }

    @ApiOperation(value = "유튜브 레시피 스크랩 목록 조회 API")
    @GetMapping("/scraps")
    public BaseResponse<RecipesResponse> getScrapYoutubeRecipes(@ApiIgnore final Authentication authentication,
                                                                @ApiParam(name = "page", type = "int", example = "0", value = "페이지")
                                                                @RequestParam(value = "page") int page,
                                                                @ApiParam(name = "size", type = "int", example = "20", value = "사이즈")
                                                                @RequestParam(value = "size") int size) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        return success(youtubeRecipeService.getScrapYoutubeRecipes(user, page, size));
    }

    @ApiOperation(value = "유튜브 레시피 스크랩 생성 API")
    @PostMapping("/{youtubeRecipeId}/scraps")
    public BaseResponse<Void> postYoutubeRecipeScrap(@ApiIgnore final Authentication authentication, @PathVariable Long youtubeRecipeId) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        youtubeScrapService.createYoutubeScrap(user, youtubeRecipeId);

        return success();
    }

    @ApiOperation(value = "유튜브 레시피 스크랩 삭제 API")
    @DeleteMapping("/{youtubeRecipeId}/scraps")
    public BaseResponse<Void> deleteYoutubeRecipeScrap(@ApiIgnore final Authentication authentication, @PathVariable Long youtubeRecipeId) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        youtubeScrapService.deleteYoutubeScrap(user, youtubeRecipeId);

        return success();
    }

}
