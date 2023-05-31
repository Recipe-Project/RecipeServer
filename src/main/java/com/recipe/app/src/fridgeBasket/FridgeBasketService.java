package com.recipe.app.src.fridgeBasket;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.src.fridgeBasket.models.*;
import com.recipe.app.src.ingredient.IngredientProvider;
import com.recipe.app.src.ingredient.IngredientRepository;
import com.recipe.app.src.ingredient.models.Ingredient;
import com.recipe.app.src.ingredientCategory.IngredientCategoryProvider;
import com.recipe.app.src.ingredientCategory.models.IngredientCategory;
import com.recipe.app.src.user.UserProvider;
import com.recipe.app.src.user.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.recipe.app.common.response.BaseResponseStatus.*;

@Service
public class FridgeBasketService {
    private final UserProvider userProvider;
    private final FridgeBasketRepository fridgeBasketRepository;
    private final IngredientCategoryProvider ingredientCategoryProvider;
    private final IngredientProvider ingredientProvider;
    private final IngredientRepository ingredientRepository;
    private final FridgeBasketProvider fridgeBasketProvider;

    @Autowired
    public FridgeBasketService(UserProvider userProvider, FridgeBasketRepository fridgeBasketRepository, IngredientCategoryProvider ingredientCategoryProvider, IngredientProvider ingredientProvider, IngredientRepository ingredientRepository, FridgeBasketProvider fridgeBasketProvider){
        this.userProvider = userProvider;
        this.fridgeBasketRepository = fridgeBasketRepository;
        this.ingredientCategoryProvider = ingredientCategoryProvider;
        this.ingredientProvider = ingredientProvider;
        this.ingredientRepository = ingredientRepository;
        this.fridgeBasketProvider = fridgeBasketProvider;
    }



    /**
     * 재료 선택으로 냉장고 바구니 담기 API
     * @param postFridgesBasketReq,userIdx
     * @return List<PostFridgesBasketRes>
     * @throws BaseException
     */
    public void createFridgesBasket(PostFridgesBasketReq postFridgesBasketReq, int userIdx) throws BaseException {
        User user = userProvider.retrieveUserByUserIdx(userIdx);
        List<Integer> ingredientIdxList = postFridgesBasketReq.getIngredientList();
        List<Ingredient> ingredientList = ingredientRepository.findAllByIngredientIdxIn(ingredientIdxList);
        Map<String, FridgeBasket> existIngredientMap = fridgeBasketRepository.findAllByUserAndStatusAndIngredientIn(user, "ACTIVE", ingredientList)
                .stream().collect(Collectors.toMap(FridgeBasket::getIngredientName, v -> v));

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

        // name 이 이미 바구니에 있다면
        FridgeBasket existFridgeBasket = fridgeBasketProvider.retreiveFridgeBasketByName(ingredientName, user);
        if (existFridgeBasket != null)
            throw new BaseException(POST_FRIDGES_BASKET_EXIST_INGREDIENT_NAME);
        // name 이 재료리스트에 있다면
        Ingredient existIngredient = ingredientProvider.retreiveIngredientByName(ingredientName);
        if (existIngredient != null)
            throw new BaseException(POST_FRIDGES_DIRECT_BASKET_DUPLICATED_INGREDIENT_NAME_IN_INGREDIENTS);

        IngredientCategory ingredientCategory = ingredientCategoryProvider.retrieveIngredientCategoryByIngredientCategoryIdx(ingredientCategoryIdx);
        FridgeBasket fridgeBasket = new FridgeBasket(user,null, ingredientName, ingredientIcon, ingredientCategory);
        fridgeBasketRepository.save(fridgeBasket);

        return new PostFridgesDirectBasketRes(ingredientName,ingredientIcon,ingredientCategoryIdx);
    }


    /**
     * 냉장고 바구니 재료 삭제 API
     * @param userIdx,ingredient
     * @throws BaseException
     */
    public void deleteFridgeBasket(Integer userIdx, String ingredient) throws BaseException {
        User user = userProvider.retrieveUserByUserIdx(userIdx);
        FridgeBasket fridgeBasket = fridgeBasketRepository.findByUserAndIngredientNameAndStatus(user, ingredient,"ACTIVE");
        fridgeBasketRepository.delete(fridgeBasket);
    }


    /**
     * 냉장고 바구니 수정 사항 저장 API
     * @param patchFridgesBasketReq,userIdx
     * @return void
     * @throws BaseException
     */
    public void updateFridgesBasket(PatchFridgesBasketReq patchFridgesBasketReq, int userIdx) throws BaseException, ParseException {
        User user = userProvider.retrieveUserByUserIdx(userIdx);
        List<FridgeBasketList> fridgeBasketList = patchFridgesBasketReq.getFridgeBasketList();
        List<String> ingredientNameList = fridgeBasketList.stream().map(FridgeBasketList::getIngredientName).collect(Collectors.toList());
        List<FridgeBasket> existIngredients = fridgeBasketRepository.findAllByUserAndStatusAndIngredientNameIn(user, "ACTIVE", ingredientNameList);
        Map<String, FridgeBasket> existIngredientMap = existIngredients.stream().collect(Collectors.toMap(FridgeBasket::getIngredientName, v -> v));

        for (FridgeBasketList fridgeBasket : fridgeBasketList) {
            String ingredientName = fridgeBasket.getIngredientName();
            Integer ingredientCnt = fridgeBasket.getIngredientCnt();
            String storageMethod = fridgeBasket.getStorageMethod();
            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA);
            Date expiredAt = (fridgeBasket.getExpiredAt() == null || "".equals(fridgeBasket.getExpiredAt())) ? null : sdFormat.parse(fridgeBasket.getExpiredAt());

            if (!existIngredientMap.containsKey(ingredientName))
                throw new BaseException(FAILED_TO_RETREIVE_FRIDGE_BASKET_BY_NAME);

            FridgeBasket existIngredient = existIngredientMap.get(ingredientName);
            existIngredient.setCount(ingredientCnt);
            existIngredient.setStorageMethod(storageMethod);
            existIngredient.setExpiredAt(expiredAt);
        }
        fridgeBasketRepository.saveAll(existIngredients);
    }
}
