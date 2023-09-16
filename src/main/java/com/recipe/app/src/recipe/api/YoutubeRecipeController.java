package com.recipe.app.src.recipe.api;

import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.src.recipe.application.SearchKeywordService;
import com.recipe.app.src.recipe.application.YoutubeRecipeService;
import com.recipe.app.src.recipe.application.dto.RecipeDto;
import com.recipe.app.src.recipe.domain.YoutubeRecipe;
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

@Api(tags = {"유튜브 레시피 Controller"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/recipes/youtube")
public class YoutubeRecipeController {

    private final YoutubeRecipeService youtubeRecipeService;
    private final SearchKeywordService recipeKeywordService;

    @ApiOperation(value = "유튜브 레시피 목록 조회 API")
    @GetMapping("")
    public BaseResponse<RecipeDto.RecipesResponse> getYoutubeRecipes(@ApiIgnore final Authentication authentication,
                                                                     @ApiParam(name = "keyword", type = "String", example = "감자", value = "검색어")
                                                                     @RequestParam(value = "keyword") String keyword,
                                                                     @ApiParam(name = "page", type = "int", example = "0", value = "페이지")
                                                                     @RequestParam(value = "page") int page,
                                                                     @ApiParam(name = "size", type = "int", example = "20", value = "사이즈")
                                                                     @RequestParam(value = "size") int size,
                                                                     @ApiParam(name = "sort", type = "String", example = "조회수순(youtubeViews) / 좋아요순(youtubeScraps) / 최신순(createdAt) = 기본값", value = "정렬")
                                                                     @RequestParam(value = "sort") String sort) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        Page<YoutubeRecipe> youtubeRecipes = youtubeRecipeService.getYoutubeRecipes(keyword, page, size, sort);
        if (youtubeRecipes.getTotalElements() < 10)
            youtubeRecipes = youtubeRecipeService.searchYoutubes(keyword, page, size, sort);

        RecipeDto.RecipesResponse data = new RecipeDto.RecipesResponse(youtubeRecipes.getTotalElements(), youtubeRecipes.stream()
                .map((recipe) -> RecipeDto.RecipeResponse.from(recipe, user))
                .collect(Collectors.toList()));
        recipeKeywordService.createSearchKeyword(keyword, user);

        return success(data);
    }

    @ApiOperation(value = "유튜브 레시피 상세 조회 API")
    @GetMapping("/{youtubeRecipeId}")
    public BaseResponse<Void> getYoutubeRecipe(@ApiIgnore final Authentication authentication, @PathVariable Long youtubeRecipeId) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        youtubeRecipeService.createYoutubeView(youtubeRecipeId, user);

        return success();
    }

    @ApiOperation(value = "유튜브 레시피 스크랩 목록 조회 API")
    @GetMapping("/scrap")
    public BaseResponse<RecipeDto.RecipesResponse> getScrapYoutubeRecipes(@ApiIgnore final Authentication authentication,
                                                                          @ApiParam(name = "page", type = "int", example = "0", value = "페이지")
                                                                          @RequestParam(value = "page") int page,
                                                                          @ApiParam(name = "size", type = "int", example = "20", value = "사이즈")
                                                                          @RequestParam(value = "size") int size) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        Page<YoutubeRecipe> youtubeRecipes = youtubeRecipeService.getScrapYoutubeRecipes(user, page, size);
        RecipeDto.RecipesResponse data = new RecipeDto.RecipesResponse(youtubeRecipes.getTotalElements(), youtubeRecipes.stream()
                .map((youtubeRecipe) -> RecipeDto.RecipeResponse.from(youtubeRecipe, user))
                .collect(Collectors.toList()));

        return success(data);
    }

    @ApiOperation(value = "유튜브 레시피 스크랩 생성 API")
    @PostMapping("/{youtubeRecipeId}/scrap")
    public BaseResponse<Void> postYoutubeRecipeScrap(@ApiIgnore final Authentication authentication, @PathVariable Long youtubeRecipeId) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        youtubeRecipeService.createYoutubeScrap(youtubeRecipeId, user);

        return success();
    }

    @ApiOperation(value = "유튜브 레시피 스크랩 삭제 API")
    @DeleteMapping("/{youtubeRecipeId}/scrap")
    public BaseResponse<Void> deleteYoutubeRecipeScrap(@ApiIgnore final Authentication authentication, @PathVariable Long youtubeRecipeId) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        youtubeRecipeService.deleteYoutubeScrap(youtubeRecipeId, user);

        return success();
    }

}
