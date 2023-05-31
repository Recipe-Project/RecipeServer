package com.recipe.app.src.recipeKeyword;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.src.recipeKeyword.models.GetRecipesBestKeywordRes;
import com.recipe.app.common.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.recipe.app.common.response.BaseResponse.success;
import static com.recipe.app.common.response.BaseResponseStatus.SUCCESS;


@RestController
@RequestMapping("/recipes")
public class RecipeKeywordController {
    private final RecipeKeywordService recipeKeywordService;
    private final RecipeKeywordProvider recipeKeywordProvider;
    private final JwtService jwtService;

    @Autowired
    public RecipeKeywordController(RecipeKeywordService recipeKeywordService, RecipeKeywordProvider recipeKeywordProvider, JwtService jwtService) {
        this.recipeKeywordService = recipeKeywordService;
        this.recipeKeywordProvider = recipeKeywordProvider;
        this.jwtService = jwtService;
    }


    /**
     * 레시피 검색어 저장 API
     * [POST] /recipes?keyword=
     * @return BaseResponse<Void>
     */
    @PostMapping("")
    public BaseResponse<Void> postRecipesKeyword(@RequestParam(value="keyword") String keyword) {
        int jwtUserIdx = jwtService.getUserId();

        recipeKeywordService.createRecipeKeyword(jwtUserIdx, keyword);

        return success();
    }

    /**
     * 인기 검색어 조회 API
     * [GET] /recipes/best-keyword
     * @return BaseResponse<List<GetRecipesBestKeywordRes>>
     */
    @GetMapping("/best-keyword")
    public BaseResponse<List<GetRecipesBestKeywordRes>> getRecipesBestKeyword() {
        int jwtUserIdx = jwtService.getUserId();

        List<GetRecipesBestKeywordRes> getRecipesBestKeywordRes = recipeKeywordProvider.retrieveRecipesBestKeyword();

        return success(getRecipesBestKeywordRes);
    }


}