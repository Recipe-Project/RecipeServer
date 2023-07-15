package com.recipe.app.src.keyword.api;

import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.src.keyword.application.RecipeKeywordService;
import com.recipe.app.src.keyword.application.dto.RecipeKeywordDto;
import com.recipe.app.src.keyword.models.GetRecipesBestKeywordRes;
import com.recipe.app.common.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.recipe.app.common.response.BaseResponse.success;


@RestController
@RequiredArgsConstructor
@RequestMapping("/recipes")
public class RecipeKeywordController {

    private final RecipeKeywordService recipeKeywordService;

    @PostMapping("")
    public BaseResponse<Void> postRecipesKeyword(@RequestParam(value="keyword") String keyword) {
        recipeKeywordService.createRecipeKeyword(keyword);

        return success();
    }

    @GetMapping("/best-keyword")
    public BaseResponse<List<RecipeKeywordDto.RecipesBestKeywordResponse>> getRecipesBestKeyword() {
        List<RecipeKeywordDto.RecipesBestKeywordResponse> data = recipeKeywordService.retrieveRecipesBestKeyword().stream()
                .map(RecipeKeywordDto.RecipesBestKeywordResponse::new)
                .collect(Collectors.toList());

        return success(data);
    }


}