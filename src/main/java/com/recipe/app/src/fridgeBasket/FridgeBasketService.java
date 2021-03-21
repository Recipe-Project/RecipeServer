package com.recipe.app.src.fridgeBasket;

import com.recipe.app.config.BaseException;
import com.recipe.app.src.fridgeBasket.models.FridgeBasket;
import com.recipe.app.src.fridgeBasket.models.PostFridgesBasketReq;
import com.recipe.app.src.fridgeBasket.models.PostFridgesBasketRes;
import com.recipe.app.src.ingredientCategory.IngredientCategoryProvider;
import com.recipe.app.src.ingredientCategory.models.IngredientCategory;
import com.recipe.app.src.user.UserProvider;
import com.recipe.app.src.user.models.User;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.recipe.app.config.BaseResponseStatus.FAILED_TO_POST_FRIDGES_BASKET;

@Service
public class FridgeBasketService {
    private final UserProvider userProvider;
    private final FridgeBasketRepository fridgeBasketRepository;
    private final IngredientCategoryProvider ingredientCategoryProvider;
    private final JwtService jwtService;

    @Autowired
    public FridgeBasketService(UserProvider userProvider, FridgeBasketRepository fridgeBasketRepository, IngredientCategoryProvider ingredientCategoryProvider, JwtService jwtService){

        this.userProvider = userProvider;
        this.fridgeBasketRepository = fridgeBasketRepository;
        this.ingredientCategoryProvider = ingredientCategoryProvider;
        this.jwtService = jwtService;
    }
    /**
     * 재료 직접 입력으로 냉장고 바구니 담기 API
     * @param postFridgesBasketReq,userIdx
     * @return PostFridgesBasketRes
     * @throws BaseException
     */
    public PostFridgesBasketRes createFridgesBasket(PostFridgesBasketReq postFridgesBasketReq, int userIdx) throws BaseException {
        User user = userProvider.retrieveUserByUserIdx(userIdx);
        String ingredientName = postFridgesBasketReq.getIngredientName();
        String ingredientIcon = postFridgesBasketReq.getIngredientIcon();
        Integer ingredientCategoryIdx = postFridgesBasketReq.getIngredientCategoryIdx();

        IngredientCategory ingredientCategory = ingredientCategoryProvider.retrieveIngredientCategoryByIngredientCategoryIdx(ingredientCategoryIdx);
        try {
            FridgeBasket fridgeBasket = new FridgeBasket(user,null,ingredientName,ingredientIcon,ingredientCategory);
            fridgeBasket = fridgeBasketRepository.save(fridgeBasket);

        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_POST_FRIDGES_BASKET);
        }


        return new PostFridgesBasketRes(ingredientName,ingredientIcon,ingredientCategoryIdx);
    }


}
