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
import org.springframework.http.HttpEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.recipe.app.config.BaseResponseStatus.*;
import static com.recipe.app.config.secret.Secret.FIREBASE_SERVER_KEY;

@Service
public class FridgeService {
    private final UserProvider userProvider;
    private final FridgeProvider fridgeProvider;
    private final FridgeRepository fridgeRepository;
    private final FridgeBasketRepository fridgeBasketRepository;
    private final IngredientCategoryProvider ingredientCategoryProvider;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    private static final String FIREBASE_API_URL="https://fcm.googleapis.com/fcm/send";

    @Autowired
    public FridgeService(UserProvider userProvider, FridgeProvider fridgeProvider, FridgeRepository fridgeRepository, FridgeBasketRepository fridgeBasketRepository, IngredientCategoryProvider ingredientCategoryProvider, UserRepository userRepository, JwtService jwtService) {
        this.userProvider = userProvider;
        this.fridgeProvider = fridgeProvider;
        this.fridgeRepository = fridgeRepository;
        this.fridgeBasketRepository = fridgeBasketRepository;
        this.ingredientCategoryProvider = ingredientCategoryProvider;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
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


        try {
            for (int i = 0; i < fridgeBasketList.size(); i++) {


                String ingredientName = fridgeBasketList.get(i).getIngredientName();


                String ingredientIcon = fridgeBasketList.get(i).getIngredientIcon();

                Integer ingredientCategoryIdx = fridgeBasketList.get(i).getIngredientCategoryIdx();

                IngredientCategory ingredientCategory = ingredientCategoryProvider.retrieveIngredientCategoryByIngredientCategoryIdx(ingredientCategoryIdx);

                String expiredAtTmp = fridgeBasketList.get(i).getExpiredAt();

                Date expiredAt;
                if (expiredAtTmp == null || expiredAtTmp.equals("")){
                    expiredAt=null;
                }
                else{
                    DateFormat sdFormat = new SimpleDateFormat("yyyy.MM.dd");
                    expiredAt = sdFormat.parse(expiredAtTmp);
                }


                String storageMethod = fridgeBasketList.get(i).getStorageMethod();
                Integer count = fridgeBasketList.get(i).getCount();

                Fridge fridge = new Fridge(user,ingredientName,ingredientIcon,ingredientCategory,storageMethod,expiredAt,count);
                fridge = fridgeRepository.save(fridge);

            }

            List<FridgeBasket> fbList = fridgeBasketRepository.findByUserAndStatus(user, "ACTIVE");


            // 재료 삭제
            for (int i=0;i<fbList.size();i++){
                fbList.get(i).setStatus("INACTIVE");
            }
            fridgeBasketRepository.saveAll(fbList);

        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_POST_FRIDGES);
        }

        List<PostFridgesRes> postFridgesResList = new ArrayList<>();

        for (int i = 0; i < fridgeBasketList.size(); i++) {
            String ingredientName = fridgeBasketList.get(i).getIngredientName();
            String ingredientIcon = fridgeBasketList.get(i).getIngredientIcon();
            Integer ingredientCategoryIdx = fridgeBasketList.get(i).getIngredientCategoryIdx();
            String expiredAt = fridgeBasketList.get(i).getExpiredAt();
            String storageMethod = fridgeBasketList.get(i).getStorageMethod();
            Integer count = fridgeBasketList.get(i).getCount();


            PostFridgesRes postFridgesRes = new PostFridgesRes(ingredientName,ingredientIcon,ingredientCategoryIdx,expiredAt,storageMethod,count);
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

        String ingredientName = parameters.getIngredientName();
        Fridge fridge;
        try {
            fridge = fridgeRepository.findByUserAndIngredientNameAndStatus(user,ingredientName,"ACTIVE");
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
                DateFormat sdFormat = new SimpleDateFormat("yyyy.MM.dd");
                Date expiredAt = sdFormat.parse(expiredAtTmp);

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

    @Async
    public CompletableFuture<String> sendNotification(HttpEntity<String> entity) {

        RestTemplate restTemplate = new RestTemplate();

        ArrayList<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();

        interceptors.add(new HeaderRequestInterceptor("Authorization",  "key=" + FIREBASE_SERVER_KEY));
        interceptors.add(new HeaderRequestInterceptor("Content-Type", "application/json; UTF-8 "));
        restTemplate.setInterceptors(interceptors);

        String firebaseResponse = restTemplate.postForObject(FIREBASE_API_URL, entity, String.class);

        return CompletableFuture.completedFuture(firebaseResponse);
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
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_PATCH_FCM_TOKEN);
        }

    }


}
