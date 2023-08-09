package com.recipe.app.src.fridge.api;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.common.utils.FirebaseCloudMessageService;
import com.recipe.app.src.fridge.application.FridgeService;
import com.recipe.app.src.fridge.application.dto.FridgeDto;
import com.recipe.app.src.fridge.domain.Fridge;
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
@RestController("/fridges")
@RequiredArgsConstructor
public class FridgeController {

    private final FridgeService fridgeService;
    private final FridgeBasketService fridgeBasketService;
    private final FirebaseCloudMessageService firebaseCloudMessageService;

    @ResponseBody
    @PostMapping("")
    public BaseResponse<FridgeDto.FridgesResponse> postFridges(final Authentication authentication) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        long fridgeBasketsCnt = fridgeBasketService.countFridgeBasketByUser(user);
        List<Fridge> fridges = fridgeService.createFridges(user);
        FridgeDto.FridgesResponse data = FridgeDto.FridgesResponse.from(fridgeBasketsCnt, fridges);

        return success(data);
    }

    @GetMapping("")
    public BaseResponse<FridgeDto.FridgesResponse> getFridges(final Authentication authentication) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        long fridgeBasketsCnt = fridgeBasketService.countFridgeBasketByUser(user);
        List<Fridge> fridges = fridgeService.getFridges(user);
        FridgeDto.FridgesResponse data = FridgeDto.FridgesResponse.from(fridgeBasketsCnt, fridges);

        return success(data);
    }

    @DeleteMapping("/{fridgeId}")
    public BaseResponse<FridgeDto.FridgesResponse> deleteFridge(final Authentication authentication, @RequestParam Long fridgeId) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        long fridgeBasketsCnt = fridgeBasketService.countFridgeBasketByUser(user);
        List<Fridge> fridges = fridgeService.deleteFridge(user, fridgeId);
        FridgeDto.FridgesResponse data = FridgeDto.FridgesResponse.from(fridgeBasketsCnt, fridges);

        return success(data);
    }

    @ResponseBody
    @PatchMapping("/{fridgeId}")
    public BaseResponse<FridgeDto.FridgesResponse> patchFridge(final Authentication authentication, @RequestParam Long fridgeId, @RequestBody FridgeDto.FridgeRequest request) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        long fridgeBasketsCnt = fridgeBasketService.countFridgeBasketByUser(user);
        List<Fridge> fridges = fridgeService.updateFridge(user, fridgeId, request);
        FridgeDto.FridgesResponse data = FridgeDto.FridgesResponse.from(fridgeBasketsCnt, fridges);

        return success(data);
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
