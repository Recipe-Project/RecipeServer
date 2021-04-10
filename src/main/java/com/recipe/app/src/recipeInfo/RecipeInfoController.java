package com.recipe.app.src.recipeInfo;



import com.recipe.app.config.BaseException;
import com.recipe.app.config.BaseResponse;
import com.recipe.app.src.recipeInfo.models.GetRecipeBlogsRes;
import com.recipe.app.src.recipeInfo.models.GetRecipeInfoRes;
import com.recipe.app.src.recipeInfo.models.GetRecipeInfosRes;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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

        try {
            Integer jwtUserIdx = jwtService.getUserId();
            List<GetRecipeInfosRes> GetRecipeInfoList = recipeInfoProvider.retrieveRecipeInfos(jwtUserIdx, keyword);

            return new BaseResponse<>(GetRecipeInfoList);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 레시피 검색 상세 조회 API
     * [GET] /recipes/:recipeIdx
     * @return BaseResponse<GetMyRecipeRes>
     */

    @GetMapping("/{recipeIdx}")
    public BaseResponse<GetRecipeInfoRes> getRecipeInfo(@PathVariable Integer recipeIdx) {

        try {
            Integer jwtUserIdx = jwtService.getUserId();
            GetRecipeInfoRes getRecipeInfo = recipeInfoProvider.retrieveRecipeInfo(jwtUserIdx, recipeIdx);

            return new BaseResponse<>(getRecipeInfo);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 블로그 검색 API
     * [GET] /recipes/blog?keyword=
     * @return BaseResponse<List<GetRecipeBlogsRes>>
     */
    @GetMapping("/blog")
    public BaseResponse<GetRecipeBlogsRes> getRecipeBlogs(@RequestParam(value="keyword") String keyword, @RequestParam(value="display") Integer display, @RequestParam(value="start") Integer start) {

        try {
            int jwtUserIdx = jwtService.getUserId();
            GetRecipeBlogsRes getRecipeBlog = recipeInfoProvider.retrieveRecipeBlogs(jwtUserIdx, keyword, display, start);

            return new BaseResponse<>(getRecipeBlog);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


}