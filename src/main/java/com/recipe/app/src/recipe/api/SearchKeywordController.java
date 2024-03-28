package com.recipe.app.src.recipe.api;

import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.src.recipe.application.keyword.SearchKeywordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.recipe.app.common.response.BaseResponse.success;

@Api(tags = {"검색어 Controller"})
@RestController
@RequestMapping("/recipes")
public class SearchKeywordController {

    private final SearchKeywordService searchKeywordService;

    public SearchKeywordController(SearchKeywordService searchKeywordService) {
        this.searchKeywordService = searchKeywordService;
    }

    @ApiOperation(value = "검색어 추천 목록 조회 API")
    @GetMapping("/best-keywords")
    public BaseResponse<List<String>> getRecipeBestKeywords() {

        return success(searchKeywordService.retrieveRecipesBestKeyword());
    }


}