package com.recipe.app.src.fridge.api;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.common.utils.FirebaseCloudMessageService;
import com.recipe.app.src.fridge.application.FridgeService;
import com.recipe.app.src.fridge.application.dto.FridgeDto;
import com.recipe.app.src.fridge.models.PatchFcmTokenReq;
import com.recipe.app.src.fridge.models.ShelfLifeUser;
import com.recipe.app.src.fridgeBasket.application.FridgeBasketService;
import com.recipe.app.src.user.domain.SecurityUser;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.user.exception.UserTokenNotExistException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.recipe.app.common.response.BaseResponse.success;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FridgeController {

    private final FridgeService fridgeService;
    private final FridgeBasketService fridgeBasketService;
    private final FirebaseCloudMessageService firebaseCloudMessageService;

    @ResponseBody
    @PostMapping("/fridges")
    public BaseResponse<List<FridgeDto.FridgeResponse>> postFridges(final Authentication authentication, @RequestBody FridgeDto.FridgesRequest request) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        List<FridgeDto.FridgeResponse> data = fridgeService.createFridges(request, user).stream()
                .map(FridgeDto.FridgeResponse::new)
                .collect(Collectors.toList());

        return success(data);
    }

    @GetMapping("/fridges")
    public BaseResponse<FridgeDto.FridgesResponse> getFridges(final Authentication authentication) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        FridgeDto.FridgesResponse data = new FridgeDto.FridgesResponse(fridgeBasketService.countFridgeBasketsByUser(user), fridgeService.retrieveFridges(user));

        return success(data);
    }

    @DeleteMapping("/fridges/ingredient")
    public BaseResponse<Void> deleteFridgeIngredients(final Authentication authentication, @RequestBody FridgeDto.FridgeIngredientsRequest request) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        fridgeService.deleteFridgeIngredients(user, request);

        return success();
    }

    @ResponseBody
    @PatchMapping("/fridges/ingredient")
    public BaseResponse<Void> patchFridgeIngredients(final Authentication authentication, @RequestBody FridgeDto.PatchFridgesRequest request) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        fridgeService.updateFridgeIngredients(user, request);

        return success();
    }

    @ResponseBody
    @GetMapping("/fridges/recipe")
    public BaseResponse<FridgeDto.FridgeRecipesResponse> getFridgesRecipe(final Authentication authentication, @RequestParam(value = "start") Integer start, @RequestParam(value = "display") Integer display) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        FridgeDto.FridgeRecipesResponse data = new FridgeDto.FridgeRecipesResponse(fridgeService.countFridgeRecipes(user), fridgeService.retrieveFridgeRecipes(user, start, display));

        return success(data);
    }

    @PatchMapping("/fcm-token")
    public BaseResponse<Void> patchFcmToken(final Authentication authentication, @RequestBody PatchFcmTokenReq parameters) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        fridgeService.updateFcmToken(parameters, user);

        return success();
    }

    @PostMapping("/fcm-test")
    public BaseResponse<Void> posFcmTest() throws BaseException, IOException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String targetToken = request.getHeader("FCM-TOKEN");
        String title = "title-test";
        String body = "body-test";

        firebaseCloudMessageService.sendMessageTo(targetToken, title, body);

        return success();
    }

    @Scheduled(cron = "0 0 12 * * *") //cron = 0 0 12 * * * 매일 12시
    @PostMapping("/notification")
    public BaseResponse<Void> postNotification() throws BaseException, IOException {
        System.out.println("*******************fcm start!****************");
        // 유통기한 리스트 조회한다.
        List<ShelfLifeUser> shelfLifeUsers = fridgeService.retreiveShelfLifeUserList();

        // 유통기한 리스트 있을때만 알림 보내기
        if (shelfLifeUsers != null) {
            // 유통기한 리스트로 알림 보낸다.
            for (ShelfLifeUser shelfLifeUser : shelfLifeUsers) {
                String deviceToken = shelfLifeUser.getDeviceToken();
                String title = "유통기한 알림";
                String body = shelfLifeUser.getIngredientName() + "의 유통기한이 3일 남았습니다.";
                System.out.println("deviceToken: " + deviceToken + "/title: " + title + "/body: " + body);
                firebaseCloudMessageService.sendMessageTo(deviceToken, title, body);
            }
        }
        return success();
    }


}
