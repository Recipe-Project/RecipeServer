package com.recipe.app.src.recipeKeyword;

import com.recipe.app.config.BaseException;
import com.recipe.app.config.BaseResponse;
import com.recipe.app.src.recipeKeyword.models.GetRecipesBestKeywordRes;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.recipe.app.config.BaseResponseStatus.SUCCESS;


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

        try {
            int jwtUserIdx = jwtService.getUserId();


            recipeKeywordService.createRecipeKeyword(jwtUserIdx, keyword);

            return new BaseResponse<>(SUCCESS);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 인기 검색어 조회 API
     * [GET] /recipes/best-keyword
     * @return BaseResponse<List<GetRecipesBestKeywordRes>>
     */
    @GetMapping("/best-keyword")
    public BaseResponse<List<GetRecipesBestKeywordRes>> getRecipesBestKeyword() {

        try {
            int jwtUserIdx = jwtService.getUserId();

            List<GetRecipesBestKeywordRes> getRecipesBestKeywordRes = recipeKeywordProvider.retrieveRecipesBestKeyword();

            return new BaseResponse<>(getRecipesBestKeywordRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


}