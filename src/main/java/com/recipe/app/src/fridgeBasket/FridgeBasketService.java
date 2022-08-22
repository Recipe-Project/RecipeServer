package com.recipe.app.src.fridgeBasket;

import com.recipe.app.config.BaseException;
import com.recipe.app.src.fridgeBasket.models.*;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        try {
            User user = userProvider.retrieveUserByUserIdx(userIdx);
            List<Integer> ingredientIdxList = postFridgesBasketReq.getIngredientList();
            List<Ingredient> ingredientList = ingredientRepository.findAllByIngredientIdxIn(ingredientIdxList);
            Map<String, FridgeBasket> existIngredientMap = fridgeBasketRepository.findAllByUserAndStatusAndIngredientIn(user, "ACTIVE", ingredientList)
                    .stream().collect(Collectors.toMap(FridgeBasket::getIngredientName, v -> v));;

            List<FridgeBasket> fridgeBaskets = new ArrayList<>();
            for (Ingredient ingredient : ingredientList) {
                String ingredientName = ingredient.getName();
                String ingredientIcon = ingredient.getIcon();
                IngredientCategory ingredientCategory = ingredient.getIngredientCategory();
                if (existIngredientMap.containsKey(ingredientName)) {
                    FridgeBasket fridgeBasket = existIngredientMap.get(ingredientName);
                    fridgeBasket.setCount(fridgeBasket.getCount() + 1);
                    fridgeBaskets.add(fridgeBasket);
                } else {
                    FridgeBasket fridgeBasket = new FridgeBasket(user, ingredient, ingredientName, ingredientIcon, ingredientCategory);
                    fridgeBaskets.add(fridgeBasket);
                }
            }
            fridgeBasketRepository.saveAll(fridgeBaskets);
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_POST_FRIDGES_BASKET);
        }
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


    /**
     * 냉장고 바구니 수정 사항 저장 API
     * @param patchFridgesBasketReq,userIdx
     * @return void
     * @throws BaseException
     */
    public void updateFridgesBasket(PatchFridgesBasketReq patchFridgesBasketReq, int userIdx) throws BaseException {
        User user = userProvider.retrieveUserByUserIdx(userIdx);
        List<FridgeBasketList> fridgeBasketList = patchFridgesBasketReq.getFridgeBasketList();

        for (FridgeBasketList fridgeBasket : fridgeBasketList) {
            String ingredientName = fridgeBasket.getIngredientName();
            Integer ingredientCnt = fridgeBasket.getIngredientCnt();
            String storageMethod = fridgeBasket.getStorageMethod();
            String expiredAtTmp = fridgeBasket.getExpiredAt();
            Date expiredAt;
            if (expiredAtTmp == null || expiredAtTmp.equals("")){
                expiredAt=null;
            }
            else{
                try {
                    DateFormat sdFormat = new SimpleDateFormat("yyyy.MM.dd");
                    expiredAt = sdFormat.parse(expiredAtTmp);
                }catch(Exception e){
                    throw new BaseException(DATE_PARSE_ERROR);
                }
            }

            FridgeBasket existIngredient=null;
            try {
                existIngredient = fridgeBasketRepository.findByUserAndIngredientNameAndStatus(user, ingredientName, "ACTIVE");
            } catch (Exception ignored) {
                throw new BaseException(FAILED_TO_RETREIVE_FRIDGE_BASKET_BY_NAME);
            }

            if(existIngredient!=null){
                existIngredient.setCount(ingredientCnt);
                existIngredient.setStorageMethod(storageMethod);
                existIngredient.setExpiredAt(expiredAt);
                try {
                    fridgeBasketRepository.save(existIngredient);
                }catch(Exception e){
                    throw new BaseException(DATABASE_ERROR);
                }
            }
        }


    }
}
