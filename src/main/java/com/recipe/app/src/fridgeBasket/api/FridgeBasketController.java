package com.recipe.app.src.fridgeBasket.api;

import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.src.fridgeBasket.application.FridgeBasketService;
import com.recipe.app.src.fridgeBasket.application.dto.FridgeBasketCountResponse;
import com.recipe.app.src.fridgeBasket.application.dto.FridgeBasketIngredientIdsRequest;
import com.recipe.app.src.fridgeBasket.application.dto.FridgeBasketIngredientRequest;
import com.recipe.app.src.fridgeBasket.application.dto.FridgeBasketRequest;
import com.recipe.app.src.fridgeBasket.application.dto.FridgeBasketsResponse;
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

@Api(tags = {"냉장고 바구니 Controller"})
@RestController
@RequestMapping("/fridges/basket")
public class FridgeBasketController {

    private final FridgeBasketService fridgeBasketService;

    public FridgeBasketController(FridgeBasketService fridgeBasketService) {
        this.fridgeBasketService = fridgeBasketService;
    }

    @ApiOperation(value = "재료 선택하여 냉장고 바구니 채우기 API")
    @PostMapping
    public BaseResponse<Void> postFridgeBasketByIngredientId(@ApiIgnore final Authentication authentication,
                                                             @ApiParam(value = "재료 선택 목록", required = true)
                                                             @RequestBody FridgeBasketIngredientIdsRequest request) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        fridgeBasketService.createFridgeBasketsByIngredientId(user, request);

        return success();
    }

    @ApiOperation(value = "재료 직접 입력하여 냉장고 바구니 채우기 API")
    @PostMapping("/direct")
    public BaseResponse<Void> postFridgeBasketWithIngredientSave(@ApiIgnore final Authentication authentication,
                                                                 @ApiParam(value = "재료 직접 입력 정보", required = true)
                                                                 @RequestBody FridgeBasketIngredientRequest request) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        fridgeBasketService.createFridgeBasketWithIngredientSave(user, request);

        return success();
    }

    @ApiOperation(value = "냉장고 바구니 조회 API")
    @GetMapping
    public BaseResponse<FridgeBasketsResponse> getFridgeBaskets(@ApiIgnore final Authentication authentication) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        return success(fridgeBasketService.getFridgeBasketsByUser(user));
    }

    @ApiOperation(value = "냉장고 바구니 삭제 API")
    @DeleteMapping("/{fridgeBasketId}")
    public BaseResponse<Void> deleteFridgeBasket(@ApiIgnore final Authentication authentication, @PathVariable Long fridgeBasketId) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        fridgeBasketService.deleteFridgeBasket(user, fridgeBasketId);

        return success();
    }

    @ApiOperation(value = "냉장고 바구니 수정 API")
    @PatchMapping("/{fridgeBasketId}")
    public BaseResponse<Void> patchFridgeBasket(@ApiIgnore final Authentication authentication, @PathVariable Long fridgeBasketId,
                                                                                 @ApiParam(value = "냉장고 바구니 수정 정보", required = true)
                                                                                 @RequestBody FridgeBasketRequest request) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        fridgeBasketService.updateFridgeBasket(user, fridgeBasketId, request);

        return success();
    }

    @ApiOperation(value = "냉장고 바구니 갯수 조회 API")
    @GetMapping("/count")
    public BaseResponse<FridgeBasketCountResponse> getFridgeBasketCount(@ApiIgnore final Authentication authentication) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        return success(fridgeBasketService.countFridgeBasketByUser(user));
    }
}
