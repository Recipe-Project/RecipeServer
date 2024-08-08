package com.recipe.app.src.fridgeBasket.api;

import com.recipe.app.src.fridgeBasket.application.FridgeBasketService;
import com.recipe.app.src.fridgeBasket.application.dto.FridgeBasketCountResponse;
import com.recipe.app.src.fridgeBasket.application.dto.FridgeBasketIngredientIdsRequest;
import com.recipe.app.src.fridgeBasket.application.dto.FridgeBasketRequest;
import com.recipe.app.src.fridgeBasket.application.dto.FridgeBasketsResponse;
import com.recipe.app.src.user.domain.SecurityUser;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.user.exception.UserTokenNotExistException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

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
    public void postFridgeBasketByIngredientId(@ApiIgnore final Authentication authentication,
                                                             @ApiParam(value = "재료 선택 목록", required = true)
                                                             @RequestBody FridgeBasketIngredientIdsRequest request) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        fridgeBasketService.createFridgeBasketsByIngredientId(user, request);
    }

    @ApiOperation(value = "냉장고 바구니 조회 API")
    @GetMapping
    public FridgeBasketsResponse getFridgeBaskets(@ApiIgnore final Authentication authentication) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        return fridgeBasketService.getFridgeBasketsByUser(user);
    }

    @ApiOperation(value = "냉장고 바구니 삭제 API")
    @DeleteMapping("/{fridgeBasketId}")
    public void deleteFridgeBasket(@ApiIgnore final Authentication authentication, @PathVariable Long fridgeBasketId) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        fridgeBasketService.deleteFridgeBasket(user, fridgeBasketId);
    }

    @ApiOperation(value = "냉장고 바구니 수정 API")
    @PatchMapping("/{fridgeBasketId}")
    public void patchFridgeBasket(@ApiIgnore final Authentication authentication, @PathVariable Long fridgeBasketId,
                                                                                 @ApiParam(value = "냉장고 바구니 수정 정보", required = true)
                                                                                 @RequestBody FridgeBasketRequest request) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        fridgeBasketService.updateFridgeBasket(user, fridgeBasketId, request);
    }

    @ApiOperation(value = "냉장고 바구니 갯수 조회 API")
    @GetMapping("/count")
    public FridgeBasketCountResponse getFridgeBasketCount(@ApiIgnore final Authentication authentication) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        return fridgeBasketService.countFridgeBasketByUser(user);
    }
}
