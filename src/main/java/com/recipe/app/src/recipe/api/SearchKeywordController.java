package com.recipe.app.src.recipe.api;

import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.src.recipe.application.SearchKeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.recipe.app.common.response.BaseResponse.success;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recipes")
public class SearchKeywordController {

    private final SearchKeywordService recipeKeywordService;

    @GetMapping("/best-keywords")
    public BaseResponse<List<String>> getRecipeSearchTop10BestKeywords() {

        List<String> data = recipeKeywordService.retrieveRecipesBestKeyword();

        return success(data);
    }


}