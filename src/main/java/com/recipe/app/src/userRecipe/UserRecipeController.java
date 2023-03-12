package com.recipe.app.src.userRecipe;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.src.ingredient.IngredientProvider;
import com.recipe.app.src.userRecipe.models.*;
import com.recipe.app.common.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.recipe.app.common.response.BaseResponse.*;
import static com.recipe.app.common.response.BaseResponseStatus.*;


@RestController
@RequestMapping("/my-recipes")
public class UserRecipeController {
    private final UserRecipeProvider userRecipeProvider;
    private final UserRecipeService userRecipeService;
    private final IngredientProvider ingredientProvider;
    private final JwtService jwtService;

    @Autowired
    public UserRecipeController(UserRecipeProvider userRecipeProvider, UserRecipeService userRecipeService, IngredientProvider ingredientProvider, JwtService jwtService) {
        this.userRecipeProvider = userRecipeProvider;
        this.userRecipeService = userRecipeService;
        this.ingredientProvider = ingredientProvider;
        this.jwtService = jwtService;
    }

    /**
     * 나만의 레시피 전체조회 API
     * [GET] /my-recipes
     * @return BaseResponse<List<GetMyRecipesRes>>
     */
    @GetMapping("")
    public BaseResponse<List<GetMyRecipesRes>> getMyRecipes() {
        Integer userIdx = jwtService.getUserId();
        List<GetMyRecipesRes> GetMyRecipesResList = userRecipeProvider.retrieveMyRecipesList(userIdx);

        return success(GetMyRecipesResList);
    }


    /**
     * 나만의 레시피 상세조회 API
     * [GET] /my-recipes/:myRecipeIdx
     * @return BaseResponse<GetMyRecipeRes>
     * @PathVariable myRecipeIdx
     */
    @GetMapping("/{myRecipeIdx}")
    public BaseResponse<GetMyRecipeRes> getMyRecipe(@PathVariable Integer myRecipeIdx) throws BaseException {
        Boolean existMyRecipe = userRecipeProvider.existMyRecipe(myRecipeIdx);
        if (!existMyRecipe){
            throw new BaseException(NO_FOUND_MY_RECIPE);
        }

        Integer userIdx = jwtService.getUserId();
        GetMyRecipeRes getMyRecipeRes = userRecipeProvider.retrieveMyRecipe(userIdx,myRecipeIdx);
        return success(getMyRecipeRes);
    }

    /**
     * 나만의 레시피 생성 API
     * [POST] /my-recipes
     * @return BaseResponse<PostMyRecipeRes>
     * @RequestBody PostMyRecipeReq parameters
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostMyRecipeRes> postMyRecipe(@RequestBody PostMyRecipeReq parameters) {
        Integer userIdx = jwtService.getUserId();

        if (parameters.getTitle() == null || parameters.getTitle().length()==0 ) {
            throw new BaseException(EMPTY_TITLE);
        }
        if (parameters.getContent() == null || parameters.getContent().length()==0 ) {
            throw new BaseException(EMPTY_CONTENT);
        }

        List<MyRecipeIngredient> ingredientList = parameters.getIngredientList();
        if (ingredientList!=null) {
            for(int i=0;i<ingredientList.size();i++){
                if(ingredientList.get(i).getIngredientName()==null || ingredientList.get(i).getIngredientName().length()==0 ){
                    throw new BaseException(EMPTY_INGREDIENT_NAME);
                }
                if(ingredientList.get(i).getIngredientIcon()==null || ingredientList.get(i).getIngredientIcon().length()==0 ){
                    throw new BaseException(EMPTY_INGREDIENT_ICON);
                }

            }
        }

        PostMyRecipeRes postMyRecipeRes = userRecipeService.createMyRecipe(parameters,userIdx);
        return success(postMyRecipeRes);
    }
    /**
     * 나만의 레시피 수정 API
     * [PATCH] /my-recipes/:myRecipeIdx
     * @return BaseResponse<PatchMyRecipeRes>
     * @PathVariable myRecipeIdx
     * @RequestBody PostMyRecipeReq parameters
     */
    @ResponseBody
    @PatchMapping("{myRecipeIdx}")
    public BaseResponse<PatchMyRecipeRes> patchMyRecipe(@PathVariable Integer myRecipeIdx, @RequestBody PatchMyRecipeReq parameters) throws BaseException {
        Integer userIdx = jwtService.getUserId();

        Boolean existMyRecipe = userRecipeProvider.existMyRecipe(myRecipeIdx);
        if (!existMyRecipe){
            throw new BaseException(NO_FOUND_MY_RECIPE);
        }


        if (myRecipeIdx == null || myRecipeIdx <= 0) {
            throw new BaseException(EMPTY_MY_RECIPEIDX);
        }


        if (parameters.getTitle() == null || parameters.getTitle().length()==0 ) {
            throw new BaseException(EMPTY_TITLE);
        }
        if (parameters.getContent() == null || parameters.getContent().length()==0 ) {
            throw new BaseException(EMPTY_CONTENT);
        }


        List<MyRecipeIngredient> ingredientList = parameters.getIngredientList();
        if (ingredientList !=null) {
            for(int i=0;i<ingredientList.size();i++){
                if(ingredientList.get(i).getIngredientName()==null || ingredientList.get(i).getIngredientName().length()==0 ){
                    throw new BaseException(EMPTY_INGREDIENT_NAME);
                }
                if(ingredientList.get(i).getIngredientIcon()==null || ingredientList.get(i).getIngredientIcon().length()==0 ){
                    throw new BaseException(EMPTY_INGREDIENT_ICON);
                }

            }
        }

        PatchMyRecipeRes patchMyRecipeRes = userRecipeService.updateMyRecipe(parameters,userIdx,myRecipeIdx);
        return success(patchMyRecipeRes);
    }

    /**
     * 나만의 레시피 삭제 API
     * [DELETE] /my-recipes/:myRecipeIdx
     * @return BaseResponse<Void>
     * @PathVariable myRecipeIdx
     */
    @DeleteMapping("/{myRecipeIdx}")
    public BaseResponse<Void> deleteMyRecipe(@PathVariable Integer myRecipeIdx) throws BaseException {
        Boolean existMyRecipe = userRecipeProvider.existMyRecipe(myRecipeIdx);
        if (!existMyRecipe){
            throw new BaseException(NO_FOUND_MY_RECIPE);
        }

        if (myRecipeIdx == null || myRecipeIdx <= 0) {
            throw new BaseException(EMPTY_MY_RECIPEIDX);
        }

        Integer userIdx = jwtService.getUserId();
        userRecipeService.deleteUserRecipe(userIdx,myRecipeIdx);
        return success();
    }

}