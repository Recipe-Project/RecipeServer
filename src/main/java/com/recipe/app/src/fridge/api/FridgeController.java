package com.recipe.app.src.fridge.api;

import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.src.fridge.application.FridgeService;
import com.recipe.app.src.fridge.application.dto.FridgeRequest;
import com.recipe.app.src.fridge.application.dto.FridgeResponse;
import com.recipe.app.src.fridge.application.dto.FridgesResponse;
import com.recipe.app.src.user.domain.SecurityUser;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.user.exception.UserTokenNotExistException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import static com.recipe.app.common.response.BaseResponse.success;

@Api(tags = {"냉장고 Controller"})
@RestController
@RequestMapping("/fridges")
public class FridgeController {

    private final FridgeService fridgeService;

    public FridgeController(FridgeService fridgeService) {
        this.fridgeService = fridgeService;
    }

    @ApiOperation(value = "냉장고 채우기 API")
    @PostMapping
    public BaseResponse<Void> postFridges(@ApiIgnore final Authentication authentication) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        fridgeService.createFridges(user);

        return success();
    }

    @ApiOperation(value = "냉장고 목록 조회 API")
    @GetMapping
    public BaseResponse<FridgesResponse> getFridges(@ApiIgnore final Authentication authentication) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        return success(fridgeService.getFridges(user));
    }

    @ApiOperation(value = "냉장고 상세 조회 API")
    @GetMapping("/{fridgeId}")
    public BaseResponse<FridgeResponse> getFridge(@ApiIgnore final Authentication authentication, @PathVariable Long fridgeId) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        return success(fridgeService.getFridge(user, fridgeId));
    }

    @ApiOperation(value = "냉장고 삭제 API")
    @DeleteMapping("/{fridgeId}")
    public BaseResponse<Void> deleteFridge(@ApiIgnore final Authentication authentication, @PathVariable Long fridgeId) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        fridgeService.deleteFridge(user, fridgeId);

        return success();
    }

    @ApiOperation(value = "냉장고 수정 API")
    @PatchMapping("/{fridgeId}")
    public BaseResponse<Void> patchFridge(@ApiIgnore final Authentication authentication, @PathVariable Long fridgeId,
                                          @ApiParam(value = "냉장고 수정 입력 정보", required = true)
                                          @RequestBody FridgeRequest request) {

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
