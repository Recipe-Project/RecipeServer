package com.recipe.app.src.recipeInfo;



import com.recipe.app.config.BaseException;
import com.recipe.app.config.BaseResponse;
import com.recipe.app.src.recipeInfo.models.*;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.recipe.app.config.BaseResponseStatus.*;


@RestController
@RequestMapping("/recipes")
public class RecipeInfoController {
    private final RecipeInfoProvider recipeInfoProvider;
    private final RecipeInfoService recipeInfoService;
    private final JwtService jwtService;

    @Autowired
    public RecipeInfoController(RecipeInfoProvider recipeInfoProvider,RecipeInfoService recipeInfoService, JwtService jwtService) {
        this.recipeInfoProvider = recipeInfoProvider;
        this.recipeInfoService = recipeInfoService;
        this.jwtService = jwtService;
    }

    /**
     * 레시피 검색 API
     * [GET] /recipes?keyword=
     * @return BaseResponse<List<GetMyRecipesRes>>
     * @PageableDefault pageable
     */

    @GetMapping("")
    public BaseResponse<List<GetRecipeInfosRes>> getRecipeInfos(@RequestParam(value="keyword") String keyword, @PageableDefault(size=10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        try {
            Integer jwtUserIdx = jwtService.getUserId();
            List<GetRecipeInfosRes> GetRecipeInfoList = recipeInfoProvider.retrieveRecipeInfos(jwtUserIdx, keyword, pageable);

            return new BaseResponse<>(GetRecipeInfoList);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

}