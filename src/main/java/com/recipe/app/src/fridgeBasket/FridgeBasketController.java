package com.recipe.app.src.fridgeBasket;

import com.recipe.app.config.BaseException;
import com.recipe.app.config.BaseResponse;
import com.recipe.app.src.fridgeBasket.models.FridgeBasket;
import com.recipe.app.src.fridgeBasket.models.PostFridgesBasketReq;
import com.recipe.app.src.fridgeBasket.models.PostFridgesBasketRes;
import com.recipe.app.src.ingredient.IngredientProvider;
import com.recipe.app.src.ingredient.models.Ingredient;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.recipe.app.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/fridges")
public class FridgeBasketController {
    private final FridgeBasketProvider fridgeBasketProvider;
    private final FridgeBasketService fridgeBasketService;
    private final IngredientProvider ingredientProvider;
    private final JwtService jwtService;

    @Autowired
    public FridgeBasketController(FridgeBasketProvider fridgeBasketProvider, FridgeBasketService fridgeBasketService, IngredientProvider ingredientProvider, JwtService jwtService) {

        this.fridgeBasketProvider = fridgeBasketProvider;
        this.fridgeBasketService = fridgeBasketService;
        this.ingredientProvider = ingredientProvider;
        this.jwtService = jwtService;
    }

    /**
     * 재료 직접 입력으로 냉장고 바구니 담기 API
     * [POST] /fridges/basket
     * @RequestParam
     * @return BaseResponse<PostFridgesBasketRes>
     * @PageableDefault pageable
     */
    @ResponseBody
    @PostMapping("/direct-basket")
    public BaseResponse<PostFridgesBasketRes> postFridgesBasket(@RequestBody PostFridgesBasketReq parameters) {

        try {
            Integer userIdx = jwtService.getUserId();

            if (parameters.getIngredientName() == null) {
                return new BaseResponse<>(POST_FRIDGES_BASKET_EMPTY_INGREDIENT_NAME);
            }

            if (parameters.getIngredientIcon() == null) {
                return new BaseResponse<>(POST_FRIDGES_BASKET_EMPTY_INGREDIENT_ICON);
            }
            if (parameters.getIngredientCategoryIdx() == null) {
                return new BaseResponse<>(POST_FRIDGES_BASKET_EMPTY_INGREDIENT_CATEGORY_IDX);
            }

            // name 이 이미 바구니에 있다면
            FridgeBasket fridgeBasket = fridgeBasketProvider.retreiveFridgeBasketByName(parameters.getIngredientName());
            if (fridgeBasket != null) {
                return new BaseResponse<>(POST_FRIDGES_BASKET_DUPLICATED_INGREDIENT_NAME_IN_BASKET);
            }
            // name 이 재료리스트에 있다면
            Ingredient ingredient = ingredientProvider.retreiveIngredientByName(parameters.getIngredientName());
            if (ingredient != null) {
                return new BaseResponse<>(POST_FRIDGES_BASKET_DUPLICATED_INGREDIENT_NAME_IN_INGREDIENTS);
            }

            PostFridgesBasketRes postFridgesBasketRes = fridgeBasketService.createFridgesBasket(parameters,userIdx);

            return new BaseResponse<>(postFridgesBasketRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
