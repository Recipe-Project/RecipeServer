package com.recipe.app.src.fridge.api;

import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.src.fridge.application.FridgeService;
import com.recipe.app.src.fridge.application.dto.FridgeDto;
import com.recipe.app.src.fridge.domain.Fridge;
import com.recipe.app.src.fridgeBasket.application.FridgeBasketService;
import com.recipe.app.src.user.domain.SecurityUser;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.user.exception.UserTokenNotExistException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

import static com.recipe.app.common.response.BaseResponse.success;

@Api(tags = {"냉장고 Controller"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/fridges")
public class FridgeController {

    private final FridgeService fridgeService;
    private final FridgeBasketService fridgeBasketService;

    @ApiOperation(value = "냉장고 채우기 API")
    @ResponseBody
    @PostMapping("")
    public BaseResponse<FridgeDto.FridgesResponse> postFridges(@ApiIgnore final Authentication authentication) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        long fridgeBasketsCnt = fridgeBasketService.countFridgeBasketByUser(user);
        List<Fridge> fridges = fridgeService.createFridges(user);
        FridgeDto.FridgesResponse data = FridgeDto.FridgesResponse.from(fridgeBasketsCnt, fridges);

        return success(data);
    }

    @ApiOperation(value = "냉장고 목록 조회 API")
    @ResponseBody
    @GetMapping("")
    public BaseResponse<FridgeDto.FridgesResponse> getFridges(@ApiIgnore final Authentication authentication) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        long fridgeBasketsCnt = fridgeBasketService.countFridgeBasketByUser(user);
        List<Fridge> fridges = fridgeService.getFridges(user);
        FridgeDto.FridgesResponse data = FridgeDto.FridgesResponse.from(fridgeBasketsCnt, fridges);

        return success(data);
    }

    @ApiOperation(value = "냉장고 상세 조회 API")
    @ResponseBody
    @GetMapping("/{fridgeId}")
    public BaseResponse<FridgeDto.FridgeResponse> getFridge(@ApiIgnore final Authentication authentication, @PathVariable Long fridgeId) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        Fridge fridge = fridgeService.getFridge(user, fridgeId);
        FridgeDto.FridgeResponse data = FridgeDto.FridgeResponse.from(fridge);

        return success(data);
    }

    @ApiOperation(value = "냉장고 삭제 API")
    @ResponseBody
    @DeleteMapping("/{fridgeId}")
    public BaseResponse<Void> deleteFridge(@ApiIgnore final Authentication authentication, @PathVariable Long fridgeId) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        fridgeService.deleteFridge(user, fridgeId);

        return success();
    }

    @ApiOperation(value = "냉장고 수정 API")
    @ResponseBody
    @PatchMapping("/{fridgeId}")
    public BaseResponse<Void> patchFridge(@ApiIgnore final Authentication authentication, @PathVariable Long fridgeId,
                                                               @ApiParam(value = "냉장고 수정 입력 정보", required = true)
                                                               @RequestBody FridgeDto.FridgeRequest request) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        fridgeService.updateFridge(user, fridgeId, request);

        return success();
    }

    /*
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
     */


}
