package com.recipe.app.src.fridge;


import com.recipe.app.config.BaseException;
import com.recipe.app.config.BaseResponse;
import com.recipe.app.src.fridge.models.*;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.recipe.app.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/fridges")
public class FridgeController {
    private final FridgeProvider fridgeProvider;
    private final FridgeService fridgeService;
    private final FridgeRepository fridgeRepository;
    private final JwtService jwtService;

    @Autowired
    public FridgeController(FridgeProvider fridgeProvider, FridgeService fridgeService,FridgeRepository fridgeRepository, JwtService jwtService) {
        this.fridgeProvider = fridgeProvider;
        this.fridgeService = fridgeService;
        this.fridgeRepository = fridgeRepository;
        this.jwtService = jwtService;
    }

    /**
     * 냉장고 채우기  API
     * [POST] /fridges
     * @RequestBody parameters
     * @return BaseResponse<List<PostFridgesRes>>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<List<PostFridgesRes>> postFridges(@RequestBody PostFridgesReq parameters) {
        // 중복되는거 처리해줘
        // 입력받은 재료이름을 for 문해서 if 있다면 에러 이미 있는 재료이다
        try {

            List<FridgeBasketList> fridgeBasketList = parameters.getFridgeBasketList();
            Integer userIdx = jwtService.getUserId();

            if (fridgeBasketList == null) {
                return new BaseResponse<>(POST_FRIDGES_EMPTY_FRIDGE_BASKET_LIST);
            }

            for (int i = 0; i < fridgeBasketList.size(); i++) {
                String ingredientName = fridgeBasketList.get(i).getIngredientName();

                Boolean existIngredientName = fridgeRepository.existsByIngredientNameAndStatus(ingredientName, "ACTIVE");

                if (existIngredientName) {
                    return new BaseResponse<>(POST_FRIDGES_EXIST_INGREDIENT_NAME);
                }
            }



            List<PostFridgesRes> postFridgesBasketRes = fridgeService.createFridges(parameters,userIdx);

            return new BaseResponse<>(postFridgesBasketRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 냉장고 조회 API
     * [GET] /fridges
     * @return BaseResponse<GetFridgesBasketRes>
     */
    @GetMapping("")
    public BaseResponse<List<GetFridgesRes>> getFridges() {

        try {
            Integer userIdx = jwtService.getUserId();
            List<GetFridgesRes> getFridgesRes = fridgeProvider.retreiveFridges(userIdx);


            return new BaseResponse<>(getFridgesRes);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 냉장고 재료 삭제 API
     * [DELETE] /fridges/ingredient
     * @return BaseResponse<Void>
     * @PathVariable myRecipeIdx
     */
    @DeleteMapping("/ingredient")
    public BaseResponse<Void> deleteFridgesIngredient(@RequestBody DeleteFridgesIngredientReq parameters) throws BaseException {

        // 리스트가 널일때
        if (parameters.getIngredientList().isEmpty()) {
            return new BaseResponse<>(EMPTY_INGREDIENT_LIST);
        }

        // 입력받은 리스트에서 이상한 값일때
        for (int i=0;i<parameters.getIngredientList().size();i++){
            Boolean existIngredient = fridgeProvider.existIngredient(parameters.getIngredientList().get(i));
            if (!existIngredient){
            return new BaseResponse<>(NOT_FOUND_INGREDIENT);
            }
        }


        try {
            Integer userIdx = jwtService.getUserId();
            fridgeService.deleteFridgeIngredient(userIdx,parameters);
            return new BaseResponse<>(SUCCESS);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

    }


//    /**
//     * 냉장고 재료 수정 API
//     * [PATCH] /fridges/ingredient
//     * @return BaseResponse<PatchFridgesIngredientRes>
//     * @RequestBody PatchFridgesIngredientReq parameters
//     */
//    @ResponseBody
//    @PatchMapping("/ingredient")
//    public BaseResponse<PatchFridgesIngredientRes> patchFridgesIngredient(@RequestBody PatchFridgesIngredientReq parameters) throws BaseException {
//
//        try {
//            Integer userIdx = jwtService.getUserId();
//
//
//            PatchFridgesIngredientRes patchFridgesIngredientRes = fridgeService.updateFridgeIngredient(parameters,userIdx);
//            return new BaseResponse<>(patchFridgesIngredientRes);
//        } catch (BaseException exception) {
//            return new BaseResponse<>(exception.getStatus());
//        }
//    }

}
