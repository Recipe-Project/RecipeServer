package com.recipe.app.src.ingredient;

import com.recipe.app.config.BaseException;
import com.recipe.app.config.BaseResponse;
import com.recipe.app.src.ingredient.models.*;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@RestController
@RequestMapping("/ingredients")
public class IngredientController {
    private final IngredientProvider ingredientProvider;
    private final IngredientService ingredientService;
    private final JwtService jwtService;

    @Autowired
    public IngredientController(IngredientProvider ingredientProvider, IngredientService ingredientService, JwtService jwtService) {
        this.ingredientProvider = ingredientProvider;
        this.ingredientService = ingredientService;
        this.jwtService = jwtService;
    }

    /**
     * 재료 조회 API
     * [GET] /ingredients
     * @RequestParam keyword
     * @return BaseResponse<List<GetIngredientsRes>>
     * @PageableDefault pageable
     */
    @GetMapping("")
    public BaseResponse<List<GetIngredientsRes>> getIngredients(@RequestParam(value = "keyword") @Nullable String keyword) {

        try {
            Integer userIdx = jwtService.getUserId();

            List<GetIngredientsRes> getIngredientsRes;
            if (keyword != null && keyword.length() != 0){
                getIngredientsRes = ingredientProvider.retrieveKeywordIngredientsList(keyword);
            }
            else{
                getIngredientsRes = ingredientProvider.retrieveIngredientsList();
            }


            return new BaseResponse<>(getIngredientsRes);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

//    /**
//     * 재료 직접입력 API
//     * [POST] /ingredients
//     * @RequestParam keyword
//     * @return BaseResponse<List<PostIngredientsRes>>
//     * @PageableDefault pageable
//     */
//    @PostMapping("")
//    public BaseResponse<PostIngredientsRes> postIngredients(@RequestBody PostIngredientsReq parameters) {
//
//        try {
//            Integer userIdx = jwtService.getUserId();
//            if (parameters.getIngredientList() == null) {
//                return new BaseResponse<>(EMPTY_INGREDIENT_LIST);
//            }
//
//            PostIngredientsRes postIngredientsRes = IngredientService.createMyFridge(parameters,userIdx);
//
//            return new BaseResponse<>(postIngredientsRes);
//        } catch (BaseException exception) {
//            return new BaseResponse<>(exception.getStatus());
//        }
//    }
}