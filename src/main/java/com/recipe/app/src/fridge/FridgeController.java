package com.recipe.app.src.fridge;


import com.recipe.app.config.BaseException;
import com.recipe.app.config.BaseResponse;
import com.recipe.app.src.fridge.models.*;
import com.recipe.app.src.user.UserProvider;
import com.recipe.app.utils.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

import static com.recipe.app.config.BaseResponseStatus.*;

@Slf4j
@RestController
//@RequestMapping("/fridges")
public class FridgeController {
//    Logger logger = LoggerFactory.getLogger(this.getClass());
    private final UserProvider userProvider;
    private final FridgeProvider fridgeProvider;
    private final FridgeService fridgeService;
    private final FridgeRepository fridgeRepository;
    private final FirebaseCloudMessageService firebaseCloudMessageService;
    private final JwtService jwtService;

    @Autowired
    public FridgeController(UserProvider userProvider, FridgeProvider fridgeProvider, FridgeService fridgeService, FridgeRepository fridgeRepository, FirebaseCloudMessageService firebaseCloudMessageService, JwtService jwtService) {
        this.userProvider = userProvider;
        this.fridgeProvider = fridgeProvider;
        this.fridgeService = fridgeService;
        this.fridgeRepository = fridgeRepository;
        this.firebaseCloudMessageService = firebaseCloudMessageService;
        this.jwtService = jwtService;
    }

    /**
     * 냉장고 채우기  API
     * [POST] /fridges
     * @RequestBody parameters
     * @return BaseResponse<List<PostFridgesRes>>
     */
    @ResponseBody
    @PostMapping("/fridges")
    public BaseResponse<List<PostFridgesRes>> postFridges(@RequestBody PostFridgesReq parameters) {
        try {
            List<FridgeBasketList> fridgeBasketList = parameters.getFridgeBasketList();
            Integer userIdx = jwtService.getUserId();

            if (fridgeBasketList==null || fridgeBasketList.isEmpty()) {
                return new BaseResponse<>(POST_FRIDGES_EMPTY_FRIDGE_BASKET_LIST);
            }

            for (FridgeBasketList fridgeBasket : fridgeBasketList) {
                String ingredientName = fridgeBasket.getIngredientName();
                String ingredientIcon = fridgeBasket.getIngredientIcon();
                Integer ingredientCategoryIdx = fridgeBasket.getIngredientCategoryIdx();
                String expiredAt = fridgeBasket.getExpiredAt();
                String storageMethod = fridgeBasket.getStorageMethod();
                Integer count = fridgeBasket.getCount();

                if (ingredientName == null || ingredientName.length()==0) {
                    return new BaseResponse<>(POST_FRIDGES_EMPTY_INGREDIENT_NAME);
                }
                if (ingredientIcon == null || ingredientIcon.length()==0) {
                    return new BaseResponse<>(POST_FRIDGES_EMPTY_INGREDIENT_ICON);
                }
                if (ingredientCategoryIdx == null || ingredientCategoryIdx<=0) {
                    return new BaseResponse<>(POST_FRIDGES_DIRECT_BASKET_EMPTY_INGREDIENT_CATEGORY_IDX);
                }
                if (storageMethod == null || storageMethod.length()==0) {
                    return new BaseResponse<>(EMPTY_STORAGE_METHOD);
                }
                if (count == null || count<=0) {
                    return new BaseResponse<>(EMPTY_INGREDIENT_COUNT);
                }
                if (expiredAt !=null && expiredAt.length()!=0 && !expiredAt.matches("^\\d{4}\\.(0[1-9]|1[012])\\.(0[1-9]|[12][0-9]|3[01])$")) {
                    return new BaseResponse<>(INVALID_DATE);
                }
                if (!storageMethod.equals("냉장") && !storageMethod.equals("냉동") && !storageMethod.equals("실온")){
                    return new BaseResponse<>(INVALID_STORAGE_METHOD);
                }
            }

            List<PostFridgesRes> postFridgesBasketRes = fridgeService.createFridges(parameters, userIdx);

            return new BaseResponse<>(postFridgesBasketRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus(), exception.getIngredientName());
        }
    }

    /**
     * 냉장고 조회 API
     * [GET] /fridges
     * @return BaseResponse<GetFridgesBasketRes>
     */
    @GetMapping("/fridges")
    public BaseResponse<GetFridgesRes> getFridges() {
        try {
            Integer userIdx = jwtService.getUserId();
            GetFridgesRes getFridgesRes = fridgeProvider.retreiveFridges(userIdx);

            return new BaseResponse<>(getFridgesRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 냉장고 재료 삭제 API
     * [DELETE] /fridges/ingredient
     * @return BaseResponse<Void>
     * @PathVariable myRecipeIdx
     */
    @DeleteMapping("/fridges/ingredient")
    public BaseResponse<Void> deleteFridgesIngredient(@RequestBody DeleteFridgesIngredientReq parameters) throws BaseException {
        try {
            Integer userIdx = jwtService.getUserId();
            List<String> ingrdientNames = parameters.getIngredientName();
            if (ingrdientNames == null || ingrdientNames.isEmpty()) {
                return new BaseResponse<>(EMPTY_INGREDIENT);
            }

            fridgeService.deleteFridgeIngredient(userIdx, parameters);
            return new BaseResponse<>(SUCCESS);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

    }

    /**
     * 냉장고 재료 수정 API
     * [PATCH] /fridges/ingredient
     * @return BaseResponse<Void>
     * @RequestBody PatchFridgesIngredientReq parameters
     */
    @ResponseBody
    @PatchMapping("/fridges/ingredient")
    public BaseResponse<Void> patchFridgesIngredient(@RequestBody PatchFridgesIngredientReq parameters) throws BaseException {
        try {
            Integer userIdx = jwtService.getUserId();
            List<PatchFridgeList> patchFridgeList = parameters.getPatchFridgeList();
            if (patchFridgeList==null || patchFridgeList.isEmpty()) {
                return new BaseResponse<>(EMPTY_PATCH_FRIDGE_LIST);
            }

            for (PatchFridgeList patchFridge : patchFridgeList) {
                String ingredientName = patchFridge.getIngredientName();
                String expiredAt = patchFridge.getExpiredAt();
                String storageMethod = patchFridge.getStorageMethod();
                Integer count = patchFridge.getCount();

                if (ingredientName == null || ingredientName.length()==0 ) {
                    return new BaseResponse<>(POST_FRIDGES_EMPTY_INGREDIENT_NAME);
                }
                if (storageMethod == null || storageMethod.length()==0 ) {
                    return new BaseResponse<>(EMPTY_STORAGE_METHOD);
                }
                if (count == null || count<=0  ) {
                    return new BaseResponse<>(EMPTY_INGREDIENT_COUNT);
                }
                if (expiredAt !=null && expiredAt.length()!=0 && !expiredAt.matches("^\\d{4}\\.(0[1-9]|1[012])\\.(0[1-9]|[12][0-9]|3[01])$")) {
                    return new BaseResponse<>(INVALID_DATE);
                }
                if (!storageMethod.equals("냉장") && !storageMethod.equals("냉동") && !storageMethod.equals("실온")){
                    return new BaseResponse<>(INVALID_STORAGE_METHOD);
                }
            }

            fridgeService.updateFridgeIngredient(parameters,userIdx);

            return new BaseResponse<>(SUCCESS);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_PATCH_FRIDGES_INGREDIENT);
        }
    }


    /**
     * 냉장고 파먹기 조회 API
     * [GET] /fridges/recipe
     * @return BaseResponse<GetFridgesRecipeRes>
     */
    @ResponseBody
    @GetMapping("/fridges/recipe")
    public BaseResponse<GetFridgesRecipeRes> getFridgesRecipe(@RequestParam(value = "start") Integer start, @RequestParam(value = "display") Integer display)  {
        try {
            Integer userIdx = jwtService.getUserId();

            GetFridgesRecipeRes getFridgesRecipeRes = fridgeProvider.retreiveFridgesRecipe(userIdx, start, display);

            return new BaseResponse<>(getFridgesRecipeRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * fcm 토큰 수정 API
     * [PATCH] /fcm-token
     * @RequestBody parameters
     * @return BaseResponse<Void>
     */
    @PatchMapping("/fcm-token")
    public BaseResponse<Void> patchFcmToken(@RequestBody PatchFcmTokenReq parameters)  {

        try {
            Integer userIdx = jwtService.getUserId();
            String fcmToken = parameters.getFcmToken();

            if (fcmToken == null || fcmToken.equals("")){
                return new BaseResponse<>(EMPTY_FCM_TOKEN);
            }

            fridgeService.updateFcmToken(parameters,userIdx);

            return new BaseResponse<>(SUCCESS);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 푸시알림 API - test
     * [POST] /fcm-test
     * @return BaseResponse<Void>
     */
    @PostMapping("/fcm-test")
    public  BaseResponse<Void> posFcmTest() throws BaseException, IOException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String targetToken = request.getHeader("FCM-TOKEN");
        String title = "title-test";
        String body = "body-test";

        firebaseCloudMessageService.sendMessageTo(targetToken,title,body);

        return new BaseResponse<>(SUCCESS);

    }


    /**
     * 푸시알림 API
     * [POST] /notification
     * @return BaseResponse<Void>
     */
    // @Scheduled(cron = "0 30 2 * * *") // 2시 30분 test
    @Scheduled(cron = "0 0 12 * * *") //cron = 0 0 12 * * * 매일 12시
    @PostMapping("/notification")
    public  BaseResponse<Void> postNotification() throws BaseException ,IOException{
         System.out.println("*******************fcm start!****************");
        try {
            // 유통기한 리스트 조회한다.
            List<ShelfLifeUser> shelfLifeUsers =  fridgeProvider.retreiveShelfLifeUserList();

            // 유통기한 리스트 있을때만 알림 보내기
            if (shelfLifeUsers!=null) {
                // 유통기한 리스트로 알림 보낸다.
                for (ShelfLifeUser shelfLifeUser : shelfLifeUsers) {
                    String deviceToken = shelfLifeUser.getDeviceToken();
                    String title = "유통기한 알림";
                    String body = shelfLifeUser.getIngredientName() + "의 유통기한이 3일 남았습니다.";
                    System.out.println("deviceToken: "+deviceToken + "/title: " + title + "/body: " + body);
                    firebaseCloudMessageService.sendMessageTo(deviceToken, title, body);
                }
            }
            return new BaseResponse<>(SUCCESS);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

    }







}
