package com.recipe.app.src.fridge;


import com.recipe.app.config.BaseException;
import com.recipe.app.config.BaseResponse;
import com.recipe.app.src.fridge.models.*;
import com.recipe.app.src.fridgeBasket.models.GetFridgesBasketRes;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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

}
