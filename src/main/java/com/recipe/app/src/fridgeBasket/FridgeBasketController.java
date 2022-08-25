package com.recipe.app.src.fridgeBasket;

import com.recipe.app.config.BaseException;
import com.recipe.app.config.BaseResponse;
import com.recipe.app.src.fridgeBasket.models.*;
import com.recipe.app.src.ingredient.IngredientProvider;
import com.recipe.app.src.ingredient.models.Ingredient;
import com.recipe.app.src.user.UserProvider;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

import static com.recipe.app.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/fridges")
public class FridgeBasketController {
    private final FridgeBasketProvider fridgeBasketProvider;
    private final FridgeBasketService fridgeBasketService;
    private final JwtService jwtService;

    @Autowired
    public FridgeBasketController(FridgeBasketProvider fridgeBasketProvider, FridgeBasketService fridgeBasketService, UserProvider userProvider, FridgeBasketRepository fridgeBasketRepository, IngredientProvider ingredientProvider, JwtService jwtService) {

        this.fridgeBasketProvider = fridgeBasketProvider;
        this.fridgeBasketService = fridgeBasketService;
        this.jwtService = jwtService;
    }


    /**
     * 재료 선택으로 냉장고 바구니 담기 API
     * [POST] /fridges/basket
     * @RequestBody parameters
     * @return BaseResponse<PostFridgesBasketRes>
     */
    @ResponseBody
    @PostMapping("/basket")
    public BaseResponse<Void> postFridgesBasket(@RequestBody PostFridgesBasketReq parameters) {
        try {
            Integer userIdx = jwtService.getUserId();
            List<Integer> ingredientList = parameters.getIngredientList();
            if (ingredientList == null || ingredientList.isEmpty()) {
                return new BaseResponse<>(POST_FRIDGES_BASKET_EMPTY_INGREDIENT_LIST);
            }

            fridgeBasketService.createFridgesBasket(parameters,userIdx);

            return new BaseResponse<>(SUCCESS);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        } catch (Exception exception) {
            return new BaseResponse<>(FAILED_TO_POST_FRIDGES_BASKET);
        }
    }

    /**
     * 재료 직접 입력으로 냉장고 바구니 담기 API
     * [POST] /fridges/direct-basket
     * @RequestBody parameters
     * @return BaseResponse<PostFridgesDirectBasketRes>
     */
    @ResponseBody
    @PostMapping("/direct-basket")
    public BaseResponse<PostFridgesDirectBasketRes> postFridgesDirectBasket(@RequestBody PostFridgesDirectBasketReq parameters) {
        try {
            Integer userIdx = jwtService.getUserId();

            if (parameters.getIngredientName() == null || parameters.getIngredientName().length()==0) {
                return new BaseResponse<>(POST_FRIDGES_DIRECT_BASKET_EMPTY_INGREDIENT_NAME);
            }

            if (parameters.getIngredientIcon() == null || parameters.getIngredientIcon().length()==0) {
                return new BaseResponse<>(POST_FRIDGES_DIRECT_BASKET_EMPTY_INGREDIENT_ICON);
            }
            if (parameters.getIngredientCategoryIdx() == null || parameters.getIngredientCategoryIdx() <= 0) {
                return new BaseResponse<>(POST_FRIDGES_DIRECT_BASKET_EMPTY_INGREDIENT_CATEGORY_IDX);
            }

            PostFridgesDirectBasketRes postFridgesBasketRes = fridgeBasketService.createFridgesDirectBasket(parameters,userIdx);

            return new BaseResponse<>(postFridgesBasketRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus(), parameters.getIngredientName());
        } catch (Exception exception) {
            return new BaseResponse<>(FAILED_TO_POST_FRIDGES_DIRECT_BASKET);
        }
    }


    /**
     * 냉장고 바구니 조회 API
     * [GET] /fridges/basket
     * @return BaseResponse<GetFridgesBasketRes>
     */
    @GetMapping("/basket")
    public BaseResponse<GetFridgesBasketRes> getFridgesBasket() {
        try {
            Integer userIdx = jwtService.getUserId();
            GetFridgesBasketRes getFridgesBasketRes = fridgeBasketProvider.retreiveFridgeBasket(userIdx);
            return new BaseResponse<>(getFridgesBasketRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        } catch (Exception exception) {
            return new BaseResponse<>(FAILED_TO_COUNT_FRIDGE_BASKET_BY_USER);
        }
    }

    /**
     * 냉장고 바구니 재료 삭제 API
     * [DELETE] /fridges/basket?ingredient=
     * @RequestParam ingredient
     * @return BaseResponse<Void>
     */
    @DeleteMapping("/basket")
    public BaseResponse<Void> deleteFridgesBasket(@RequestParam(value="ingredient") String ingredient) {
        try {
            Integer userIdx = jwtService.getUserId();
            if (ingredient == null || ingredient.equals("")){
                return new BaseResponse<>(EMPTY_INGREDIENT);
            }

            fridgeBasketService.deleteFridgeBasket(userIdx, ingredient);

            return new BaseResponse<>(SUCCESS);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 냉장고 바구니 수정 사항 저장 API
     * [PATCH] /fridges/basket
     * @RequestBody parameters
     * @return BaseResponse<Void>
     */
    @ResponseBody
    @PatchMapping("/basket")
    public BaseResponse<Void> patchFridgesBasket(@RequestBody PatchFridgesBasketReq parameters) {
        try {
            Integer userIdx = jwtService.getUserId();
            List<FridgeBasketList> fridgeBasketList = parameters.getFridgeBasketList();
            if (fridgeBasketList == null || fridgeBasketList.isEmpty()) {
                return new BaseResponse<>(PATCH_FRIDGES_BASKET_EMPTY_FRIDGES_BASKET_LIST);
            }

            fridgeBasketService.updateFridgesBasket(parameters,userIdx);

            return new BaseResponse<>(SUCCESS);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        } catch (ParseException exception) {
            return new BaseResponse<>(DATE_PARSE_ERROR);
        } catch (Exception exception) {
            return new BaseResponse<>(FAILED_TO_PATCH_FRIDGE_BASKET_INGREDIENTS);
        }
    }

    /**
     * 냉장고 바구니 개수 조회 API
     * [GET] /fridges/basket/count
     * @return BaseResponse<GetFridgesBasketCountRes>
     */
    @GetMapping("/basket/count")
    public BaseResponse<GetFridgesBasketCountRes> getFridgesBasketCount() {

        try {
            Integer userIdx = jwtService.getUserId();
            GetFridgesBasketCountRes getFridgesBasketCountRes = fridgeBasketProvider.retreiveFridgeBasketCount(userIdx);

            return new BaseResponse<>(getFridgesBasketCountRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
