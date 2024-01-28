package com.recipe.app.src.fridgeBasket.api;

import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.src.common.application.BadWordService;
import com.recipe.app.src.fridgeBasket.application.FridgeBasketService;
import com.recipe.app.src.fridgeBasket.application.dto.FridgeBasketDto;
import com.recipe.app.src.fridgeBasket.domain.FridgeBasket;
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

@Api(tags = {"냉장고 바구니 Controller"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/fridges/basket")
public class FridgeBasketController {

    private final FridgeBasketService fridgeBasketService;
    private final BadWordService badWordService;

    @ApiOperation(value = "재료 선택하여 냉장고 바구니 채우기 API")
    @ResponseBody
    @PostMapping("")
    public BaseResponse<FridgeBasketDto.FridgeBasketsResponse> postFridgeBasketByIngredientId(@ApiIgnore final Authentication authentication,
                                                                                              @ApiParam(value = "재료 선택 목록", required = true)
                                                                                              @RequestBody FridgeBasketDto.FridgeBasketIngredientIdsRequest request) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        List<FridgeBasket> fridgeBaskets = fridgeBasketService.createFridgeBasketByIngredientId(user, request);
        FridgeBasketDto.FridgeBasketsResponse data = FridgeBasketDto.FridgeBasketsResponse.from(fridgeBaskets);

        return success(data);
    }

    @ApiOperation(value = "재료 직접 입력하여 냉장고 바구니 채우기 API")
    @ResponseBody
    @PostMapping("/direct")
    public BaseResponse<FridgeBasketDto.FridgeBasketsResponse> postFridgeBasketWithIngredientSave(@ApiIgnore final Authentication authentication,
                                                                                                  @ApiParam(value = "재료 직접 입력 정보", required = true)
                                                                                                  @RequestBody FridgeBasketDto.FridgeBasketIngredientRequest request) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        badWordService.checkBadWords(request.getIngredientName());
        List<FridgeBasket> fridgeBaskets = fridgeBasketService.createFridgeBasketWithIngredientSave(user, request);
        FridgeBasketDto.FridgeBasketsResponse data = FridgeBasketDto.FridgeBasketsResponse.from(fridgeBaskets);

        return success(data);
    }

    @ApiOperation(value = "냉장고 바구니 조회 API")
    @ResponseBody
    @GetMapping("")
    public BaseResponse<FridgeBasketDto.FridgeBasketsResponse> getFridgeBasket(@ApiIgnore final Authentication authentication) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        FridgeBasketDto.FridgeBasketsResponse data = FridgeBasketDto.FridgeBasketsResponse.from(fridgeBasketService.getFridgeBasketsByUser(user));

        return success(data);
    }

    @ApiOperation(value = "냉장고 바구니 삭제 API")
    @ResponseBody
    @DeleteMapping("/{fridgeBasketId}")
    public BaseResponse<Void> deleteFridgeBasket(@ApiIgnore final Authentication authentication, @PathVariable Long fridgeBasketId) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        fridgeBasketService.deleteFridgeBasket(user, fridgeBasketId);

        return success();
    }

    @ApiOperation(value = "냉장고 바구니 수정 API")
    @ResponseBody
    @PatchMapping("/{fridgeBasketId}")
    public BaseResponse<FridgeBasketDto.FridgeBasketsResponse> patchFridgeBasket(@ApiIgnore final Authentication authentication, @PathVariable Long fridgeBasketId,
                                                                                 @ApiParam(value = "냉장고 바구니 수정 정보", required = true)
                                                                                 @RequestBody FridgeBasketDto.FridgeBasketRequest request) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        FridgeBasketDto.FridgeBasketsResponse data = FridgeBasketDto.FridgeBasketsResponse.from(fridgeBasketService.updateFridgeBasket(user, fridgeBasketId, request));

        return success(data);
    }

    @ApiOperation(value = "냉장고 바구니 갯수 조회 API")
    @ResponseBody
    @GetMapping("/count")
    public BaseResponse<FridgeBasketDto.FridgeBasketCountResponse> getFridgeBasketCount(@ApiIgnore final Authentication authentication) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        long fridgeBasketCount = fridgeBasketService.countFridgeBasketByUser(user);
        FridgeBasketDto.FridgeBasketCountResponse data = FridgeBasketDto.FridgeBasketCountResponse.from(fridgeBasketCount);

        return success(data);
    }
}
