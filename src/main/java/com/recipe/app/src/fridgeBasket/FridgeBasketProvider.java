package com.recipe.app.src.fridgeBasket;

import com.recipe.app.config.BaseException;
import com.recipe.app.src.fridgeBasket.models.FridgeBasket;
import com.recipe.app.src.fridgeBasket.models.GetFridgesBasketRes;
import com.recipe.app.src.ingredient.models.IngredientList;
import com.recipe.app.src.user.UserProvider;
import com.recipe.app.src.user.models.User;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.recipe.app.config.BaseResponseStatus.*;

@Service
public class FridgeBasketProvider {
    private final UserProvider userProvider;
    private final FridgeBasketRepository fridgeBasketRepository;
    private final JwtService jwtService;

    @Autowired
    public FridgeBasketProvider(UserProvider userProvider, FridgeBasketRepository fridgeBasketRepository,JwtService jwtService) {
        this.userProvider = userProvider;
        this.fridgeBasketRepository = fridgeBasketRepository;
        this.jwtService = jwtService;
    }

    /**
     * FridgeBasket에서 재료명으로 재료 조회하기
     * @param name
     * @return FridgeBasket
     * @throws BaseException
     */
    public FridgeBasket retreiveFridgeBasketByName(String name) throws BaseException {
        FridgeBasket fridgeBasket;
        try {
            fridgeBasket = fridgeBasketRepository.findByIngredientNameAndStatus(name,"ACTIVE");
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_RETREIVE_FRIDGE_BASKET_BY_NAME);
        }

        return fridgeBasket;
    }

    /**
     *냉장고 바구니 조회 API
     * @param userIdx
     * @return GetFridgesBasketRes
     * @throws BaseException
     */
    public GetFridgesBasketRes retreiveFridgeBasket(int userIdx) throws BaseException {
        User user = userProvider.retrieveUserByUserIdx(userIdx);

        Long ingredientCount;
        try {
            ingredientCount = fridgeBasketRepository.countByUserAndStatus(user,"ACTIVE");
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_COUNT_FRIDGE_BASKET_BY_USER);
        }

        List ingredientList = retrieveIngredientList(userIdx);;


        return new GetFridgesBasketRes(ingredientCount,ingredientList);
    }


    /**
     * 유저 인덱스로 냉장고 바구니 조회
     * @param userIdx
     * @return List<IngredientList>
     * @throws BaseException
     */
    public List<IngredientList> retrieveIngredientList(int userIdx) throws BaseException {
        User user = userProvider.retrieveUserByUserIdx(userIdx);

        List<FridgeBasket> fridgeBasketList;
        try {
            fridgeBasketList = fridgeBasketRepository.findByUserAndStatus(user, "ACTIVE");
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_RETREIVE_INGREDIENT_LIST_BY_USER);
        }

        return fridgeBasketList.stream().map(fridgeBasket -> {
            Integer ingredientIdx=null;
            if(fridgeBasket.getIngredient()!=null){
                ingredientIdx = fridgeBasket.getIngredient().getIngredientIdx();
            }

            String ingredientName = fridgeBasket.getIngredientName();
            String ingredientIcon = fridgeBasket.getIngredientIcon();

            return new IngredientList(ingredientIdx,ingredientName,ingredientIcon);

        }).collect(Collectors.toList());
    }

}
