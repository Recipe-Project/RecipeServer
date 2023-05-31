package com.recipe.app.src.fridge;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.src.fridge.models.*;
import com.recipe.app.src.fridgeBasket.FridgeBasketRepository;
import com.recipe.app.src.fridgeBasket.models.FridgeBasket;
import com.recipe.app.src.ingredientCategory.models.IngredientCategory;
import com.recipe.app.src.user.UserProvider;
import com.recipe.app.src.user.UserRepository;
import com.recipe.app.src.user.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.recipe.app.common.response.BaseResponseStatus.*;

@Service
public class FridgeService {
    private final UserProvider userProvider;
    private final FridgeRepository fridgeRepository;
    private final FridgeProvider fridgeProvider;
    private final FridgeBasketRepository fridgeBasketRepository;
    private final UserRepository userRepository;

    private static final String FIREBASE_API_URL = "https://fcm.googleapis.com/fcm/send";

    @Autowired
    public FridgeService(UserProvider userProvider, FridgeProvider fridgeProvider, FridgeRepository fridgeRepository, FridgeBasketRepository fridgeBasketRepository, UserRepository userRepository) {
        this.userProvider = userProvider;
        this.fridgeProvider = fridgeProvider;
        this.fridgeRepository = fridgeRepository;
        this.fridgeBasketRepository = fridgeBasketRepository;
        this.userRepository = userRepository;
    }

    /**
     * 냉장고 채우기  API
     *
     * @param postFridgesReq,userIdx
     * @return List<PostFridgesRes>
     * @throws BaseException
     */
    @Transactional
    public List<PostFridgesRes> createFridges(PostFridgesReq postFridgesReq, int userIdx) throws BaseException, ParseException {
        User user = userProvider.retrieveUserByUserIdx(userIdx);
        List<FridgeBasketList> fridgeBasketList = postFridgesReq.getFridgeBasketList();
        Map<String, FridgeBasketList> fridgeBasketListMap = fridgeBasketList.stream().collect(Collectors.toMap(FridgeBasketList::getIngredientName, v -> v));
        List<String> ingredientNameList = fridgeBasketList.stream().map(FridgeBasketList::getIngredientName).collect(Collectors.toList());

        List<FridgeBasket> fbList = fridgeBasketRepository.findAllByUserAndStatusAndIngredientNameIn(user, "ACTIVE", ingredientNameList);

        if(fbList.size() != fridgeBasketList.size())
            throw new BaseException(FAILED_TO_GET_FRIDGE_BASKET);

        List<Fridge> existIngredients = fridgeProvider.getExistIngredients(ingredientNameList, user);
        if (existIngredients.size() > 0)
            throw new BaseException(POST_FRIDGES_EXIST_INGREDIENT_NAME, existIngredients.get(0).getIngredientName());

        // 냉장고 저장
        List<Fridge> fridges = new ArrayList<>();
        for (FridgeBasket fridgeBasket: fbList) {
            String ingredientName = fridgeBasket.getIngredientName();
            IngredientCategory ingredientCategory = fridgeBasket.getIngredientCategory();
            String ingredientIcon = fridgeBasket.getIngredientIcon();
            FridgeBasketList fbMapValue = fridgeBasketListMap.get(ingredientName);
            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy.MM.dd");
            Date expiredAt = (fbMapValue.getExpiredAt() == null || "".equals(fbMapValue.getExpiredAt())) ? null : sdFormat.parse(fbMapValue.getExpiredAt());
            String storageMethod = fbMapValue.getStorageMethod();
            Integer count = fbMapValue.getCount();
            fridges.add(new Fridge(user, ingredientName, ingredientIcon, ingredientCategory, storageMethod, expiredAt, count));
        }
        fridgeRepository.saveAll(fridges);

        fridgeBasketRepository.deleteAll(fbList);

        List<PostFridgesRes> postFridgesResList = new ArrayList<>();
        for (FridgeBasketList fridgeBasket : fridgeBasketList) {
            String ingredientName = fridgeBasket.getIngredientName();
            String ingredientIcon = fridgeBasket.getIngredientIcon();
            Integer ingredientCategoryIdx = fridgeBasket.getIngredientCategoryIdx();
            String expiredAt = fridgeBasket.getExpiredAt();
            String storageMethod = fridgeBasket.getStorageMethod();
            Integer count = fridgeBasket.getCount();
            PostFridgesRes postFridgesRes = new PostFridgesRes(ingredientName, ingredientIcon, ingredientCategoryIdx, expiredAt, storageMethod, count);
            postFridgesResList.add(postFridgesRes);
        }
        return postFridgesResList;
    }

    /**
     * 냉장고 재료 삭제 API
     *
     * @param userIdx,parameters
     * @throws BaseException
     */
    @Transactional
    public void deleteFridgeIngredient(Integer userIdx, DeleteFridgesIngredientReq parameters) throws BaseException {
        User user = userProvider.retrieveUserByUserIdx(userIdx);
        List<String> ingredientNames = parameters.getIngredientName();
        List<Fridge> existIngredients = fridgeProvider.getExistIngredients(ingredientNames, user);
        if (existIngredients.size() != ingredientNames.size())
            throw new BaseException(NOT_FOUND_INGREDIENT);
        fridgeRepository.deleteAll(existIngredients);
    }

    /**
     * 냉장고 재료 수정 API
     *
     * @param patchFridgesIngredientReq,userIdx
     * @return void
     * @throws BaseException
     */
    @Transactional
    public void updateFridgeIngredient(PatchFridgesIngredientReq patchFridgesIngredientReq, Integer userIdx) throws BaseException, ParseException {
        User user = userProvider.retrieveUserByUserIdx(userIdx);
        List<PatchFridgeList> patchFridgeList = patchFridgesIngredientReq.getPatchFridgeList();
        List<String> ingredientNames = patchFridgeList.stream().map(PatchFridgeList::getIngredientName).collect(Collectors.toList());
        List<Fridge> existIngredients = fridgeProvider.getExistIngredients(ingredientNames, user);
        if (existIngredients.size() != ingredientNames.size())
            throw new BaseException(NOT_FOUND_INGREDIENT);
        Map<String, Fridge> existIngredientMap = existIngredients.stream().collect(Collectors.toMap(Fridge::getIngredientName, v -> v));

        for (PatchFridgeList patchFridge : patchFridgeList) {
            String ingredientName = patchFridge.getIngredientName();
            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy.MM.dd");
            Date expiredAt = (patchFridge.getExpiredAt() == null || "".equals(patchFridge.getExpiredAt())) ? null : sdFormat.parse(patchFridge.getExpiredAt());
            String storageMethod = patchFridge.getStorageMethod();
            Integer count = patchFridge.getCount();

            if (!existIngredientMap.containsKey(ingredientName))
                throw new BaseException(FAILED_TO_RETREIVE_FRIDGE_BY_NAME);

            Fridge existIngredient = existIngredientMap.get(ingredientName);
            existIngredient.setCount(count);
            existIngredient.setStorageMethod(storageMethod);
            existIngredient.setExpiredAt(expiredAt);
        }
        fridgeRepository.saveAll(existIngredients);
    }


    /**
     * fcm 토큰 수정 API
     *
     * @param patchFcmTokenReq,userIdx
     * @return void
     * @throws BaseException
     */
    @Transactional
    public void updateFcmToken(PatchFcmTokenReq patchFcmTokenReq, Integer userIdx) throws BaseException {
        String fcmToken = patchFcmTokenReq.getFcmToken();

        User userinfo = userRepository.findByUserIdxAndStatus(userIdx, "ACTIVE");
        userinfo.setDeviceToken(fcmToken);
        userRepository.save(userinfo);
    }
}
