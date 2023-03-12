package com.recipe.app.src.recipeInfo;



import com.recipe.app.common.exception.BaseException;
import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.src.recipeInfo.models.GetRecipeBlogsRes;
import com.recipe.app.src.recipeInfo.models.GetRecipeInfoRes;
import com.recipe.app.src.recipeInfo.models.GetRecipeInfosRes;
import com.recipe.app.common.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.recipe.app.common.response.BaseResponse.success;
import static com.recipe.app.common.response.BaseResponseStatus.RECIPES_EMPTY_RECIPE_IDX;


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
     * @return BaseResponse<List<GetRecipeInfosRes>>
     */

    @GetMapping("")
    public BaseResponse<List<GetRecipeInfosRes>> getRecipeInfos(@RequestParam(value="keyword") String keyword) {
        Integer jwtUserIdx = jwtService.getUserId();
        List<GetRecipeInfosRes> GetRecipeInfoList = recipeInfoProvider.retrieveRecipeInfos(jwtUserIdx, keyword);

        return success(GetRecipeInfoList);
    }


    /**
     * 레시피 검색 상세 조회 API
     * [GET] /recipes/:recipeIdx
     * @return BaseResponse<GetMyRecipeRes>
     */

    @GetMapping("/{recipeIdx}")
    public BaseResponse<GetRecipeInfoRes> getRecipeInfo(@PathVariable Integer recipeIdx) {
        if(recipeIdx==null || recipeIdx<=0){
            throw new BaseException(RECIPES_EMPTY_RECIPE_IDX);
        }

        Integer jwtUserIdx = jwtService.getUserId();
        GetRecipeInfoRes getRecipeInfo = recipeInfoProvider.retrieveRecipeInfo(jwtUserIdx, recipeIdx);

        return success(getRecipeInfo);
    }


    /**
     * 블로그 검색 API
     * [GET] /recipes/blog?keyword=
     * @return BaseResponse<List<GetRecipeBlogsRes>>
     */
    @GetMapping("/blog")
    public BaseResponse<GetRecipeBlogsRes> getRecipeBlogs(@RequestParam(value="keyword") String keyword, @RequestParam(value="display") Integer display, @RequestParam(value="start") Integer start) {
        int jwtUserIdx = jwtService.getUserId();
        GetRecipeBlogsRes getRecipeBlog = recipeInfoProvider.retrieveRecipeBlogs(jwtUserIdx, keyword, display, start);

        return success(getRecipeBlog);
    }


}