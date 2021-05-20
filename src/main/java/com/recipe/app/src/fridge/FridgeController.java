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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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


            for (int i = 0; i < fridgeBasketList.size(); i++) {
                String ingredientName = fridgeBasketList.get(i).getIngredientName();
                String ingredientIcon = fridgeBasketList.get(i).getIngredientIcon();
                Integer ingredientCategoryIdx = fridgeBasketList.get(i).getIngredientCategoryIdx();
                String expiredAt = fridgeBasketList.get(i).getExpiredAt();
                String storageMethod = fridgeBasketList.get(i).getStorageMethod();
                Integer count = fridgeBasketList.get(i).getCount();

                if (ingredientName == null || ingredientName.length()==0 ) {
                    return new BaseResponse<>(POST_FRIDGES_EMPTY_INGREDIENT_NAME);
                }
                if (ingredientIcon == null || ingredientIcon.length()==0 ) {
                    return new BaseResponse<>(POST_FRIDGES_EMPTY_INGREDIENT_ICON);
                }
                //int만 입력하도록?
                if (ingredientCategoryIdx == null || ingredientCategoryIdx<=0 ) {
                    return new BaseResponse<>(POST_FRIDGES_DIRECT_BASKET_EMPTY_INGREDIENT_CATEGORY_IDX);
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
                Boolean existIngredientName = fridgeProvider.existIngredient(ingredientName,userIdx);
                if (existIngredientName) {
                    return new BaseResponse<>(POST_FRIDGES_EXIST_INGREDIENT_NAME,ingredientName);
                }
                ArrayList storageMethods = new ArrayList();
                storageMethods.add("냉장");
                storageMethods.add("냉동");
                storageMethods.add("실온");

                if (!storageMethods.contains(storageMethod)){
                    return new BaseResponse<>(INVALID_STORAGE_METHOD);
                }

            }

            List<PostFridgesRes> postFridgesBasketRes = fridgeService.createFridges(parameters,userIdx);

            return new BaseResponse<>(postFridgesBasketRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
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

        String ingrdientName = parameters.getIngredientName();

        if (ingrdientName == null || ingrdientName.equals("")) {
            return new BaseResponse<>(EMPTY_INGREDIENT);
        }
        Integer userIdx = jwtService.getUserId();
        Boolean existIngredient = fridgeProvider.existIngredient(ingrdientName,userIdx);
        if (!existIngredient){
            return new BaseResponse<>(NOT_FOUND_INGREDIENT);
        }



        try {

            fridgeService.deleteFridgeIngredient(userIdx,parameters);
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


            for (int i = 0; i < patchFridgeList.size(); i++) {
                String ingredientName = patchFridgeList.get(i).getIngredientName();
                String expiredAt = patchFridgeList.get(i).getExpiredAt();
                String storageMethod = patchFridgeList.get(i).getStorageMethod();
                Integer count = patchFridgeList.get(i).getCount();

                if (ingredientName == null || ingredientName.length()==0 ) {
                    return new BaseResponse<>(POST_FRIDGES_EMPTY_INGREDIENT_NAME);
                }
                if (storageMethod == null || storageMethod.length()==0 ) {
                    return new BaseResponse<>(EMPTY_STORAGE_METHOD);
                }
                if (count == null || count<=0  ) {
                    return new BaseResponse<>(EMPTY_INGREDIENT_COUNT);
                }
                if (expiredAt !=null && ingredientName.length()!=0 && !expiredAt.matches("^\\d{4}\\.(0[1-9]|1[012])\\.(0[1-9]|[12][0-9]|3[01])$")) {
                    return new BaseResponse<>(INVALID_DATE);
                }
                Boolean existIngredientName = fridgeProvider.existIngredient(ingredientName, userIdx);
                if (!existIngredientName) {
                    return new BaseResponse<>(NOT_FOUND_INGREDIENT);
                }
                ArrayList storageMethods = new ArrayList();
                storageMethods.add("냉장");
                storageMethods.add("냉동");
                storageMethods.add("실온");
                if (!storageMethods.contains(storageMethod)){
                    return new BaseResponse<>(INVALID_STORAGE_METHOD);
                }

            }

            fridgeService.updateFridgeIngredient(parameters,userIdx);

            return new BaseResponse<>(SUCCESS);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 냉장고 파먹기 조회 API
     * [GET] /fridges/recipe
     * @return BaseResponse<GetFridgesRecipeRes>
     */
    @ResponseBody
    @GetMapping("/fridges/recipe")
    public BaseResponse<List<GetFridgesRecipeRes>> getFridgesRecipe()  {

        try {
            Integer userIdx = jwtService.getUserId();


            List<GetFridgesRecipeRes> getFridgesRecipeRes = fridgeProvider.retreiveFridgesRecipe(userIdx);

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


//    /**
//     * 푸시알림 API - test
//     * [POST] /fcm-test
//     * @return BaseResponse<Void>
//     */
//    @Scheduled(cron = "0 0/1 * * * *") //1분마다
////    @Scheduled(fixedDelay = 10000) //10초마다
//    @PostMapping("/fcm-test")
//    public  BaseResponse<Void> posFcmTest() throws BaseException, IOException {
////        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
////        String targetToken = request.getHeader("FCM-TOKEN");
//        String targetToken = "";
//        String title = "title-test";
//        String body = "body-test";
//
//        firebaseCloudMessageService.sendMessageTo(targetToken,title,body);
//
//        return new BaseResponse<>(SUCCESS);
//
//    }

    // 테스트
    @Scheduled(cron = "0 30 22 * * *") // 10시 20분
    public void cronTest() {
        System.out.println("cron: 0 30 22 * * * " + new Date());
    }

    /**
     * 푸시알림 API
     * [POST] /notification
     * @return BaseResponse<Void>
     */
//    @Scheduled(cron = "0 0/1 * * * *") //1분마다
//    @Scheduled(cron = "0 0 13 * * *") //cron = 0 0 12 * * * 매일 12시
//    @Scheduled(cron = "*/20 * * * * *") //20초마다
    @Scheduled(cron = "0 30 22 * * *") // 10시 30분
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
