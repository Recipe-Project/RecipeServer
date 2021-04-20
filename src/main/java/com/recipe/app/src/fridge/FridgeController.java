package com.recipe.app.src.fridge;


import com.recipe.app.config.BaseException;
import com.recipe.app.config.BaseResponse;
import com.recipe.app.src.fridge.models.*;
import com.recipe.app.src.user.UserProvider;
import com.recipe.app.src.user.models.User;
import com.recipe.app.utils.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.recipe.app.config.BaseResponseStatus.*;

@Slf4j
@RestController
//@RequestMapping("/fridges")
public class FridgeController {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    private final UserProvider userProvider;
    private final FridgeProvider fridgeProvider;
    private final FridgeService fridgeService;
    private final FridgeRepository fridgeRepository;
//    private final FirebaseCloudMessageService firebaseCloudMessageService;
    private final JwtService jwtService;

    @Autowired
    public FridgeController(UserProvider userProvider, FridgeProvider fridgeProvider, FridgeService fridgeService, FridgeRepository fridgeRepository, JwtService jwtService) {
        this.userProvider = userProvider;
        this.fridgeProvider = fridgeProvider;
        this.fridgeService = fridgeService;
        this.fridgeRepository = fridgeRepository;
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

            if (fridgeBasketList.isEmpty()) {
                return new BaseResponse<>(POST_FRIDGES_EMPTY_FRIDGE_BASKET_LIST);
            }

            for (int i = 0; i < fridgeBasketList.size(); i++) {
                String ingredientName = fridgeBasketList.get(i).getIngredientName();

                Boolean existIngredientName = fridgeRepository.existsByIngredientNameAndStatus(ingredientName, "ACTIVE");

                if (existIngredientName) {
                    return new BaseResponse<>(POST_FRIDGES_EXIST_INGREDIENT_NAME,ingredientName);
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

        Boolean existIngredient = fridgeProvider.existIngredient(ingrdientName);
        if (!existIngredient){
            return new BaseResponse<>(NOT_FOUND_INGREDIENT);
        }



        try {
            Integer userIdx = jwtService.getUserId();
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

            if (patchFridgeList.isEmpty()) {
                return new BaseResponse<>(EMPTY_PATCH_FRIDGE_LIST);
            }


            for (int i = 0; i < patchFridgeList.size(); i++) {
                String ingredientName = patchFridgeList.get(i).getIngredientName();

                Boolean existIngredientName = fridgeRepository.existsByIngredientNameAndStatus(ingredientName, "ACTIVE");

                if (!existIngredientName) {
                    return new BaseResponse<>(NOT_FOUND_INGREDIENT);
                }

                ArrayList storageMethods = new ArrayList();
                storageMethods.add("냉장");
                storageMethods.add("냉동");
                storageMethods.add("실온");

                String storageMethod = patchFridgeList.get(i).getStorageMethod();
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
     * 냉장고 파먹기 API
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
     * 푸시알림 API
     * [POST] /fridges/notification
     * @return BaseResponse<Void>
     */
    @Scheduled(cron = "0 0 12 * * *") //cron = 0 0 12 * * * 매일 12시 0 15 10 * * * 매일 10시 15분 //@Scheduled(fixedDelay = 10000) //10초마다
    @PostMapping("/fridges/notification")
    public  BaseResponse<Void> postFridgesNotification() throws BaseException, JSONException,InterruptedException {
        log.info("This job is executed per a second.");

        List<ShelfLifeUser> shelfLifeUsers =  fridgeProvider.retreiveShelfLifeUserList();

        // 유저인덱스말고 로그인할때마다 디바이스토큰을 디비에 저장하고 그 토큰을 불러오기


        if(shelfLifeUsers.isEmpty()){
            return new BaseResponse<>(EMPTY_USER_LIST);
        }


//        for (String deviceToken : userMapList.keySet() ){
//            String notifications = AndroidPushPeriodicNotifications.PeriodicNotificationJson(userMapList);
//
//            HttpEntity<String> request = new HttpEntity<>(notifications);
//
//            CompletableFuture<String> pushNotification = fridgeService.sendNotification(request);
//            CompletableFuture.allOf(pushNotification).join();
//
//            try{
//                String firebaseResponse = pushNotification.get();
//            }
//            catch (InterruptedException e){
//                logger.debug("got interrupted!");
//                throw new InterruptedException();
//
//            }
//            catch (ExecutionException e){
//                logger.debug("execution error!");
//            }
//            return new BaseResponse<>(SUCCESS);
//        }


        // 디바이스 토큰 추가하려면 로그인 회원가입시에 디바이스토큰까지 저장하도록 로직 변경해야함 안드에게 얘기도 해야함
        try{
//            for (String deviceToken : userMapList.keySet() ){
//                String ingredientName = userMapList.get(deviceToken);
//
//                String notifications = AndroidPushPeriodicNotifications.PeriodicNotificationJson(deviceToken,ingredientName);
//
//                HttpEntity<String> request = new HttpEntity<>(notifications);
//
//                CompletableFuture<String> pushNotification = fridgeService.sendNotification(request);
//                CompletableFuture.allOf(pushNotification).join();
//
//                String firebaseResponse = pushNotification.get();
//            }

            //테스트
            for (ShelfLifeUser shelfLifeUser : shelfLifeUsers ){
                Integer userIdx = shelfLifeUser.getUserIdx();  //디바이스토큰으로
                String ingredientName = shelfLifeUser.getIngredientName();
                User user = userProvider.retrieveUserByUserIdx(userIdx);
                String deviceToken = user.getDeviceToken();

//                String notifications = AndroidPushPeriodicNotifications.PeriodicNotificationJson(userIdx,ingredientName); //디바이스토큰으로
                String notifications = AndroidPushPeriodicNotifications.PeriodicNotificationJson(deviceToken,ingredientName); //디바이스토큰으로

                HttpEntity<String> request = new HttpEntity<>(notifications);

                CompletableFuture<String> pushNotification = fridgeService.sendNotification(request);
                CompletableFuture.allOf(pushNotification).join();

                String firebaseResponse = pushNotification.get();
            }

        }
        catch (InterruptedException e){
            logger.debug("got interrupted!");
            throw new InterruptedException();


        }
        catch (ExecutionException e){
            logger.debug("execution error!");
        }
        return new BaseResponse<>(SUCCESS);


    }
//    //    @Scheduled(cron = "0 0 12 * * *") //cron = 0 0 12 * * * 매일 12시 0 15 10 * * * 매일 10시 15분
//    @Scheduled(fixedDelay = 10000) //10초마다
//    @PostMapping("/notification")
//    public  @ResponseBody ResponseEntity<String>  postFridgesNotification() throws BaseException, JSONException,InterruptedException {
//        log.info("This job is executed per a second.");
//        List<Integer> userList = new ArrayList<>();
//
//        userList = fridgeProvider.retreiveShelfLifeUserList();
//
//        if(userList.isEmpty()){
//
//        }
//        // 유저리스트가 비었다면 ? 유저리스트가 비었습니다.
//
//        String notifications = AndroidPushPeriodicNotifications.PeriodicNotificationJson(userList);
//
//        HttpEntity<String> request = new HttpEntity<>(notifications);
//
//        CompletableFuture<String> pushNotification = fridgeService.sendNotification(request);
//        CompletableFuture.allOf(pushNotification).join();
//
//        try{
//            String firebaseResponse = pushNotification.get();
//            return new ResponseEntity<>(firebaseResponse, HttpStatus.OK);
//        }
//        catch (InterruptedException e){
//            logger.debug("got interrupted!");
//            throw new InterruptedException();
//        }
//        catch (ExecutionException e){
//            logger.debug("execution error!");
//        }
//
//        return new ResponseEntity<>("Push Notification ERROR!", HttpStatus.BAD_REQUEST);
//
//    }


    /**
     * fcm 토큰 수정 API
     * [PATCH] /fcm/token
     * @RequestBody parameters
     * @return BaseResponse<Void>
     */
    @PatchMapping("/fcm/token")
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
//     * 푸시알림 API
//     * [POST] /fcm
//     * @return BaseResponse<Void>
//     */
////    @Scheduled(cron = "0 0 12 * * *") //cron = 0 0 12 * * * 매일 12시 0 15 10 * * * 매일 10시 15분 //@Scheduled(fixedDelay = 10000) //10초마다
//    @PostMapping("/fcm")
//    public  BaseResponse<Void> postFcm() throws BaseException,IOException {
//
//        String targetToken = "dsiQzbjDS9SQYyQFyiwGkM:APA91bHMq0mUdROfC-bmWXJ9-wq09MvvwFyZPO0UooU8jJibdzYoDpFONaXt8yNPBs36fRToy4vSlZHkbI4mCFss06o6uu8gC0U5EZqzxKe-_lB2S78BgdnUsmbmheTef3SMgueJUX5G";
//        String title = "title-test";
//        String body = "body-test";
//
//        firebaseCloudMessageService.sendMessageTo(targetToken,title,body);
//
//        return new BaseResponse<>(SUCCESS);
//
//    }

}
