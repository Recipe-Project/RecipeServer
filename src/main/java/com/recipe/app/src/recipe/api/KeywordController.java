package com.recipe.app.src.recipe.api;

import com.recipe.app.src.recipe.application.keyword.KeywordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "검색어 Controller")
@RestController
@RequestMapping("/recipes")
public class KeywordController {

    private final KeywordService keywordService;

    public KeywordController(KeywordService keywordService) {
        this.keywordService = keywordService;
    }

    @Operation(summary = "검색어 추천 목록 조회 API")
    @GetMapping("/best-keywords")
    public List<String> getRecipeBestKeywords() {

        return keywordService.retrieveRecipesBestKeyword();
    }
}
