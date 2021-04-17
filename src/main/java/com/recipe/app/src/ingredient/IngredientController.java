package com.recipe.app.src.ingredient;

import com.recipe.app.config.BaseException;
import com.recipe.app.config.BaseResponse;
import com.recipe.app.src.ingredient.models.GetIngredientsRes;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


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
     * @return BaseResponse<GetIngredientsRes>
     * @PageableDefault pageable
     */
    @GetMapping("")
    public BaseResponse<GetIngredientsRes> getIngredients(@RequestParam(value = "keyword") @Nullable String keyword) {

        try {
            Integer userIdx = jwtService.getUserId();

            GetIngredientsRes getIngredientsRes;
            if (keyword != null && keyword.length() != 0){
                getIngredientsRes = ingredientProvider.retrieveKeywordIngredientsList(keyword,userIdx);
            }
            else{
                getIngredientsRes = ingredientProvider.retrieveIngredientsList(userIdx);
            }


            return new BaseResponse<>(getIngredientsRes);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

}