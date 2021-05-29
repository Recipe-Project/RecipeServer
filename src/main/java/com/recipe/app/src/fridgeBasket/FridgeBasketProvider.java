package com.recipe.app.src.fridgeBasket;

import com.recipe.app.config.BaseException;
import com.recipe.app.src.fridgeBasket.models.FridgeBasket;
import com.recipe.app.src.fridgeBasket.models.GetFridgesBasketRes;
import com.recipe.app.src.fridgeBasket.models.IngredientList;
import com.recipe.app.src.ingredient.IngredientProvider;
import com.recipe.app.src.ingredient.models.Ingredient;
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
    private final IngredientProvider ingredientProvider;
    private final FridgeBasketRepository fridgeBasketRepository;
    private final JwtService jwtService;

    @Autowired
    public FridgeBasketProvider(UserProvider userProvider, IngredientProvider ingredientProvider, FridgeBasketRepository fridgeBasketRepository, JwtService jwtService) {
        this.userProvider = userProvider;
        this.ingredientProvider = ingredientProvider;
        this.fridgeBasketRepository = fridgeBasketRepository;
        this.jwtService = jwtService;
    }

    /**
     * 유저 냉장고 바구니에 이미 가지고 있는 재료인지 확인
     * @param userIdx,ingredientIdx
     * @return existIngredient
     * @throws BaseException
     */
    public Boolean existIngredient(int userIdx,int ingredientIdx) throws BaseException {
        User user = userProvider.retrieveUserByUserIdx(userIdx);
        Ingredient ingredient = ingredientProvider.retrieveIngredientByIngredientIdx(ingredientIdx);
        String ingredientName = ingredient.getName();

        Boolean existIngredient;
        try {
            existIngredient = fridgeBasketRepository.existsByUserAndIngredientNameAndStatus(user,ingredientName,"ACTIVE");
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_RETREIVE_FRIDGE_BASKET_BY_NAME);
        }

        return existIngredient;
    }

    /**
     * FridgeBasket에서 재료명으로 재료 조회하기
     * @param name,userIdx
     * @return FridgeBasket
     * @throws BaseException
     */
    public FridgeBasket retreiveFridgeBasketByName(String name,int userIdx) throws BaseException {
        User user = userProvider.retrieveUserByUserIdx(userIdx);
        FridgeBasket fridgeBasket;
        try {
            fridgeBasket = fridgeBasketRepository.findByUserAndIngredientNameAndStatus(user,name,"ACTIVE");
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
            Integer ingredientCategoryIdx = fridgeBasket.getIngredientCategory().getIngredientCategoryIdx();
            Integer ingredientCnt = fridgeBasket.getCount();

            return new IngredientList(ingredientIdx,ingredientName,ingredientIcon,ingredientCategoryIdx, ingredientCnt);

        }).collect(Collectors.toList());
    }

}
