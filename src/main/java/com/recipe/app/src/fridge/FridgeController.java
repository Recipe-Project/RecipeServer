package com.recipe.app.src.fridge;


import com.recipe.app.config.BaseException;
import com.recipe.app.config.BaseResponse;
import com.recipe.app.src.fridge.models.*;
import com.recipe.app.utils.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.recipe.app.config.BaseResponseStatus.*;

@Slf4j
@RestController
@RequestMapping("/fridges")
public class FridgeController {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    private final FridgeProvider fridgeProvider;
    private final FridgeService fridgeService;
    private final FridgeRepository fridgeRepository;
    private final JwtService jwtService;
    private final AndroidPushNotificationsService androidPushNotificationsService;

    @Autowired
    public FridgeController(FridgeProvider fridgeProvider, FridgeService fridgeService, FridgeRepository fridgeRepository, JwtService jwtService, AndroidPushNotificationsService androidPushNotificationsService) {
        this.fridgeProvider = fridgeProvider;
        this.fridgeService = fridgeService;
        this.fridgeRepository = fridgeRepository;
        this.jwtService = jwtService;
        this.androidPushNotificationsService = androidPushNotificationsService;
    }

    /**
     * 냉장고 채우기  API
     * [POST] /fridges
     * @RequestBody parameters
     * @return BaseResponse<List<PostFridgesRes>>
     */
    @ResponseBody
    @PostMapping("")
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
                    return new BaseResponse<>(POST_FRIDGES_EXIST_INGREDIENT_NAME); // 재료명보여줄수있나
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
    @GetMapping("")
    public BaseResponse<List<GetFridgesRes>> getFridges() {

        try {
            Integer userIdx = jwtService.getUserId();
            List<GetFridgesRes> getFridgesRes = fridgeProvider.retreiveFridges(userIdx);


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
    @DeleteMapping("/ingredient")
    public BaseResponse<Void> deleteFridgesIngredient(@RequestBody DeleteFridgesIngredientReq parameters) throws BaseException {

        // 리스트가 널일때
        if (parameters.getIngredientList().isEmpty()) {
            return new BaseResponse<>(EMPTY_INGREDIENT_LIST);
        }

        // 입력받은 리스트에서 이상한 값일때
        for (int i=0;i<parameters.getIngredientList().size();i++){
            Boolean existIngredient = fridgeProvider.existIngredient(parameters.getIngredientList().get(i));
            if (!existIngredient){
            return new BaseResponse<>(NOT_FOUND_INGREDIENT);
            }
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
    @PatchMapping("/ingredient")
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
    @GetMapping("/recipe")
    public BaseResponse<List<GetFridgesRecipeRes>> getFridgesRecipe()  {

        try {
            Integer userIdx = jwtService.getUserId();


            List<GetFridgesRecipeRes> getFridgesRecipeRes = fridgeProvider.retreiveFridgesRecipe(userIdx);

            return new BaseResponse<>(getFridgesRecipeRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

//    @Scheduled(fixedDelay = 10000) //10초마다
//    @Scheduled(cron = "0 0 12 * * *") //cron = 0 0 12 * * * 매일 12시 0 15 10 * * * 매일 10시 15분
    public  @ResponseBody ResponseEntity<String>  notification() throws BaseException, JSONException,InterruptedException {
        log.info("This job is executed per a second.");

        ArrayList userList = new ArrayList();
        userList = fridgeProvider.retreiveShelfLifeUserList();

        String notifications = AndroidPushPeriodicNotifications.PeriodicNotificationJson(userList);

        HttpEntity<String> request = new HttpEntity<>(notifications);

        CompletableFuture<String> pushNotification = androidPushNotificationsService.send(request);
        CompletableFuture.allOf(pushNotification).join();

        try{
            String firebaseResponse = pushNotification.get();
            return new ResponseEntity<>(firebaseResponse, HttpStatus.OK);
        }
        catch (InterruptedException e){
            logger.debug("got interrupted!");
            throw new InterruptedException();
        }
        catch (ExecutionException e){
            logger.debug("execution error!");
        }

        return new ResponseEntity<>("Push Notification ERROR!", HttpStatus.BAD_REQUEST);

    }

}
