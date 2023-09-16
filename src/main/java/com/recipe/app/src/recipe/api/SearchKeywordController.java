package com.recipe.app.src.recipe.api;

import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.src.recipe.application.SearchKeywordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.recipe.app.common.response.BaseResponse.success;

@Api(tags = {"검색어 Controller"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/recipes")
public class SearchKeywordController {

    private final SearchKeywordService recipeKeywordService;

    @ApiOperation(value = "인기 검색어 Top10 목록 조회 API")
    @GetMapping("/best-keywords")
    public BaseResponse<List<String>> getRecipeSearchTop10BestKeywords() {

        List<String> data = recipeKeywordService.retrieveRecipesDayBestKeyword();

        return success(data);
    }


}