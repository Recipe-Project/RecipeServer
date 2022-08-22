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

import java.util.List;

import static com.recipe.app.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/fridges")
public class FridgeBasketController {
    private final FridgeBasketProvider fridgeBasketProvider;
    private final FridgeBasketService fridgeBasketService;
    private final UserProvider userProvider;
    private final FridgeBasketRepository fridgeBasketRepository;
    private final IngredientProvider ingredientProvider;
    private final JwtService jwtService;

    @Autowired
    public FridgeBasketController(FridgeBasketProvider fridgeBasketProvider, FridgeBasketService fridgeBasketService, UserProvider userProvider, FridgeBasketRepository fridgeBasketRepository, IngredientProvider ingredientProvider, JwtService jwtService) {

        this.fridgeBasketProvider = fridgeBasketProvider;
        this.fridgeBasketService = fridgeBasketService;
        this.userProvider = userProvider;
        this.fridgeBasketRepository = fridgeBasketRepository;
        this.ingredientProvider = ingredientProvider;
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
            // name 이 이미 바구니에 있다면
            FridgeBasket fridgeBasket = fridgeBasketProvider.retreiveFridgeBasketByName(parameters.getIngredientName(),userIdx);
            if (fridgeBasket != null) {
                return new BaseResponse<>(POST_FRIDGES_BASKET_EXIST_INGREDIENT_NAME,parameters.getIngredientName());
            }
            // name 이 재료리스트에 있다면
            Ingredient ingredient = ingredientProvider.retreiveIngredientByName(parameters.getIngredientName());
            if (ingredient != null) {
                return new BaseResponse<>(POST_FRIDGES_DIRECT_BASKET_DUPLICATED_INGREDIENT_NAME_IN_INGREDIENTS,parameters.getIngredientName());
            }

            PostFridgesDirectBasketRes postFridgesBasketRes = fridgeBasketService.createFridgesDirectBasket(parameters,userIdx);

            return new BaseResponse<>(postFridgesBasketRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
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

        if (ingredient == null || ingredient.equals("")){

            return new BaseResponse<>(EMPTY_INGREDIENT);
        }

        try {
            Integer userIdx = jwtService.getUserId();

            fridgeBasketService.deleteFridgeBasket(userIdx,ingredient);


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
