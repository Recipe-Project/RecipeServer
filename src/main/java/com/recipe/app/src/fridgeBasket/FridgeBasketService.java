package com.recipe.app.src.fridgeBasket;

import com.recipe.app.config.BaseException;
import com.recipe.app.src.fridgeBasket.models.FridgeBasket;
import com.recipe.app.src.fridgeBasket.models.PostFridgesBasketReq;
import com.recipe.app.src.fridgeBasket.models.PostFridgesDirectBasketReq;
import com.recipe.app.src.fridgeBasket.models.PostFridgesDirectBasketRes;
import com.recipe.app.src.ingredient.IngredientProvider;
import com.recipe.app.src.ingredient.IngredientRepository;
import com.recipe.app.src.ingredient.models.Ingredient;
import com.recipe.app.src.ingredientCategory.IngredientCategoryProvider;
import com.recipe.app.src.ingredientCategory.models.IngredientCategory;
import com.recipe.app.src.user.UserProvider;
import com.recipe.app.src.user.models.User;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.recipe.app.config.BaseResponseStatus.*;

@Service
public class FridgeBasketService {
    private final UserProvider userProvider;
    private final FridgeBasketRepository fridgeBasketRepository;
    private final IngredientCategoryProvider ingredientCategoryProvider;
    private final IngredientProvider ingredientProvider;
    private final IngredientRepository ingredientRepository;
    private final JwtService jwtService;

    @Autowired
    public FridgeBasketService(UserProvider userProvider, FridgeBasketRepository fridgeBasketRepository, IngredientCategoryProvider ingredientCategoryProvider, IngredientProvider ingredientProvider, IngredientRepository ingredientRepository, JwtService jwtService){

        this.userProvider = userProvider;
        this.fridgeBasketRepository = fridgeBasketRepository;
        this.ingredientCategoryProvider = ingredientCategoryProvider;
        this.ingredientProvider = ingredientProvider;
        this.ingredientRepository = ingredientRepository;
        this.jwtService = jwtService;
    }



    /**
     * 재료 선택으로 냉장고 바구니 담기 API
     * @param postFridgesBasketReq,userIdx
     * @return List<PostFridgesBasketRes>
     * @throws BaseException
     */
    public void createFridgesBasket(PostFridgesBasketReq postFridgesBasketReq, int userIdx) throws BaseException {
        User user = userProvider.retrieveUserByUserIdx(userIdx);
        List<Integer> ingredientList = postFridgesBasketReq.getIngredientList();

        try {
            for (int i = 0; i < ingredientList.size(); i++) {
                Integer ingredientIdx = ingredientList.get(i);

                Ingredient ingredient = ingredientProvider.retrieveIngredientByIngredientIdx(ingredientIdx);

                String ingredientName = ingredient.getName();
                String ingredientIcon = ingredient.getIcon();
                Integer ingredientCategoryIdx = ingredient.getIngredientCategory().getIngredientCategoryIdx();
                IngredientCategory ingredientCategory = ingredientCategoryProvider.retrieveIngredientCategoryByIngredientCategoryIdx(ingredientCategoryIdx);
                FridgeBasket fridgeBasket = new FridgeBasket(user,ingredient,ingredientName,ingredientIcon,ingredientCategory);
                fridgeBasketRepository.save(fridgeBasket);

            }

        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_POST_FRIDGES_BASKET);
        }
//        List<PostFridgesBasketRes> postFridgesBasketResList = new ArrayList<>();
//        for (int i = 0; i < ingredientList.size(); i++) {
//            Integer ingredientIdx = ingredientList.get(i);
//            Ingredient ingredient = ingredientProvider.retrieveIngredientByIngredientIdx(ingredientIdx);
//            FridgeBasket fridgeBasket = fridgeBasketRepository.findByIngredientAndStatus(ingredient,"ACTIVE");
//            String ingredientName = fridgeBasket.getIngredientName();
//            String ingredientIcon = fridgeBasket.getIngredientIcon();
//            Integer ingredientCategoryIdx = fridgeBasket.getIngredientCategory().getIngredientCategoryIdx();
//
//            PostFridgesBasketRes postFridgesBasketRes = new PostFridgesBasketRes(ingredientName,ingredientIcon,ingredientCategoryIdx);
//            postFridgesBasketResList.add(postFridgesBasketRes);
//
//        }
//        return postFridgesBasketResList;

    }

    /**
     * 재료 직접 입력으로 냉장고 바구니 담기 API
     * @param postFridgesDirectBasketReq,userIdx
     * @return PostFridgesDirectBasketRes
     * @throws BaseException
     */
    public PostFridgesDirectBasketRes createFridgesDirectBasket(PostFridgesDirectBasketReq postFridgesDirectBasketReq, int userIdx) throws BaseException {
        User user = userProvider.retrieveUserByUserIdx(userIdx);
        String ingredientName = postFridgesDirectBasketReq.getIngredientName();
        String ingredientIcon = postFridgesDirectBasketReq.getIngredientIcon();
        Integer ingredientCategoryIdx = postFridgesDirectBasketReq.getIngredientCategoryIdx();

        IngredientCategory ingredientCategory = ingredientCategoryProvider.retrieveIngredientCategoryByIngredientCategoryIdx(ingredientCategoryIdx);
        try {
            FridgeBasket fridgeBasket = new FridgeBasket(user,null,ingredientName,ingredientIcon,ingredientCategory);
            fridgeBasket = fridgeBasketRepository.save(fridgeBasket);

        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_POST_FRIDGES_DIRECT_BASKET);
        }


        return new PostFridgesDirectBasketRes(ingredientName,ingredientIcon,ingredientCategoryIdx);
    }


    /**
     * 냉장고 바구니 재료 삭제 API
     * @param userIdx,ingredient
     * @throws BaseException
     */
    public void deleteFridgeBasket(Integer userIdx, String ingredient) throws BaseException {
        User user = userProvider.retrieveUserByUserIdx(userIdx);
        FridgeBasket fridgeBasket;
        try {
            fridgeBasket = fridgeBasketRepository.findByUserAndIngredientNameAndStatus(user,ingredient,"ACTIVE");
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_FRIDGE_BASKET);
        }



        try {
            fridgeBasket.setStatus("INACTIVE");
            fridgeBasketRepository.save(fridgeBasket);


        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_DELETE_FRIDGE_BASKET);
        }



    }

}
