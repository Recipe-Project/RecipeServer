package com.recipe.app.src.fridge;

import com.recipe.app.config.BaseException;
import com.recipe.app.src.fridge.models.*;
import com.recipe.app.src.fridgeBasket.FridgeBasketRepository;
import com.recipe.app.src.fridgeBasket.models.FridgeBasket;
import com.recipe.app.src.ingredientCategory.IngredientCategoryProvider;
import com.recipe.app.src.ingredientCategory.models.IngredientCategory;
import com.recipe.app.src.user.UserProvider;
import com.recipe.app.src.user.UserRepository;
import com.recipe.app.src.user.models.User;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.recipe.app.config.BaseResponseStatus.*;

@Service
public class FridgeService {
    private final UserProvider userProvider;
    private final FridgeProvider fridgeProvider;
    private final FridgeRepository fridgeRepository;
    private final FridgeBasketRepository fridgeBasketRepository;
    private final UserRepository userRepository;

    private static final String FIREBASE_API_URL="https://fcm.googleapis.com/fcm/send";

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
     * @param postFridgesReq,userIdx
     * @return List<PostFridgesRes>
     * @throws BaseException
     */
    @Transactional
    public List<PostFridgesRes> createFridges(PostFridgesReq postFridgesReq, int userIdx) throws BaseException {
        User user = userProvider.retrieveUserByUserIdx(userIdx);
        List<FridgeBasketList> fridgeBasketList = postFridgesReq.getFridgeBasketList();
        List<String> ingredientNameList = fridgeBasketList.stream().map(FridgeBasketList::getIngredientName).collect(Collectors.toList());
        List<FridgeBasket> fbList;
        try{
            fbList = fridgeBasketRepository.findAllByUserAndStatusAndIngredientNameIn(user, "ACTIVE", ingredientNameList);
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_GET_FRIDGE_BASKET);
        }
        if(fbList.size() != fridgeBasketList.size())
            throw new BaseException(FAILED_TO_GET_FRIDGE_BASKET);
        List<Fridge> existIngredients = fridgeProvider.getExistIngredients(ingredientNameList, user);
        if (existIngredients.size() > 0) {
            throw new BaseException(POST_FRIDGES_EXIST_INGREDIENT_NAME, existIngredients.get(0).getIngredientName());
        }

        // 냉장고 저장
        List<Fridge> fridges = new ArrayList<>();
        try {
            for (FridgeBasket fridgeBasket: fbList) {
                String ingredientName = fridgeBasket.getIngredientName();
                String ingredientIcon = fridgeBasket.getIngredientIcon();
                IngredientCategory ingredientCategory = fridgeBasket.getIngredientCategory();
                Date expiredAt = fridgeBasket.getExpiredAt();
                String storageMethod = fridgeBasket.getStorageMethod();
                Integer count = fridgeBasket.getCount();
                fridges.add(new Fridge(user, ingredientName, ingredientIcon, ingredientCategory, storageMethod, expiredAt, count));
            }
            fridgeRepository.saveAll(fridges);
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_POST_FRIDGES);
        }

        // 냉장고 바구니 삭제
        try{
            fridgeBasketRepository.deleteAll(fbList);
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_DELETE_FRIDGE_BASKET);
        }

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
     * @param userIdx,parameters
     * @throws BaseException
     */
    @Transactional
    public void deleteFridgeIngredient(Integer userIdx, DeleteFridgesIngredientReq parameters) throws BaseException {
        User user = userProvider.retrieveUserByUserIdx(userIdx);

        List<String> ingredientNames = parameters.getIngredientName();

        for(String ingredientName : ingredientNames) {
            Fridge fridge;
            try {
                fridge = fridgeRepository.findByUserAndIngredientNameAndStatus(user, ingredientName, "ACTIVE");
            } catch (Exception ignored) {
                throw new BaseException(FAILED_TO_GET_FRIDGE);
            }

            try {
                fridge.setStatus("INACTIVE");
                fridgeRepository.save(fridge);
            } catch (Exception exception) {
                throw new BaseException(FAILED_TO_DELETE_FRIDGE);
            }

        }

    }

    /**
     * 냉장고 재료 수정 API
     * @param patchFridgesIngredientReq,userIdx
     * @return void
     * @throws BaseException
     */
    @Transactional
    public void updateFridgeIngredient(PatchFridgesIngredientReq patchFridgesIngredientReq, Integer userIdx) throws BaseException {
        User user = userProvider.retrieveUserByUserIdx(userIdx);

        List<PatchFridgeList> patchFridgeList = patchFridgesIngredientReq.getPatchFridgeList();

        try {
            for (int i = 0; i < patchFridgeList.size(); i++) {

                String ingredientName = patchFridgeList.get(i).getIngredientName();


                String expiredAtTmp = patchFridgeList.get(i).getExpiredAt();

                Date expiredAt;
                if (expiredAtTmp == null || expiredAtTmp.equals("")){
                    expiredAt=null;
                }
                else{
                    DateFormat sdFormat = new SimpleDateFormat("yyyy.MM.dd");
                    expiredAt = sdFormat.parse(expiredAtTmp);
                }


                String storageMethod = patchFridgeList.get(i).getStorageMethod();
                Integer count = patchFridgeList.get(i).getCount();

                Fridge fridge;
                try {
                    fridge = fridgeRepository.findByUserAndIngredientNameAndStatus(user,ingredientName,"ACTIVE");
                } catch (Exception ignored) {
                    throw new BaseException(FAILED_TO_GET_FRIDGE);
                }


                try {
                    fridge.setExpiredAt(expiredAt);
                    fridge.setStorageMethod(storageMethod);
                    fridge.setCount(count);
                    fridgeRepository.save(fridge);

                } catch (Exception ignored) {
                    throw new BaseException(FAILED_TO_SAVE_FRIDGE);
                }

            }

        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_PATCH_FRIDGES_INGREDIENT);
        }

    }



    /**
     * fcm 토큰 수정 API
     * @param patchFcmTokenReq,userIdx
     * @return void
     * @throws BaseException
     */
    @Transactional
    public void updateFcmToken(PatchFcmTokenReq patchFcmTokenReq, Integer userIdx) throws BaseException {
        String fcmToken = patchFcmTokenReq.getFcmToken();

        User userinfo ;
        try{
            userinfo = userRepository.findByUserIdxAndStatus(userIdx,"ACTIVE");
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_GET_USER);
        }

        try{
            userinfo.setDeviceToken(fcmToken);
            userRepository.save(userinfo);
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_PATCH_FCM_TOKEN);
        }

    }


}
