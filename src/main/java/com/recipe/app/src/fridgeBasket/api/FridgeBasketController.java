package com.recipe.app.src.fridgeBasket.api;

import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.src.fridgeBasket.application.FridgeBasketService;
import com.recipe.app.src.fridgeBasket.application.dto.FridgeBasketDto;
import com.recipe.app.src.user.domain.SecurityUser;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.user.exception.UserTokenNotExistException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static com.recipe.app.common.response.BaseResponse.success;

@RestController
@RequiredArgsConstructor
@RequestMapping("/fridges")
public class FridgeBasketController {

    private final FridgeBasketService fridgeBasketService;

    @ResponseBody
    @PostMapping("/basket")
    public BaseResponse<Void> postFridgesBasket(final Authentication authentication, @RequestBody FridgeBasketDto.FridgeBasketIdsRequest request) {
        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        fridgeBasketService.createFridgesBasket(request, user);

        return success();
    }

    @ResponseBody
    @PostMapping("/direct-basket")
    public BaseResponse<FridgeBasketDto.DirectFridgeBasketsResponse> postFridgesDirectBasket(final Authentication authentication, @RequestBody FridgeBasketDto.DirectFridgeBasketsRequest request) {
        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        FridgeBasketDto.DirectFridgeBasketsResponse data = new FridgeBasketDto.DirectFridgeBasketsResponse(fridgeBasketService.createDirectFridgeBasket(request, user));

        return success(data);
    }

    @GetMapping("/basket")
    public BaseResponse<FridgeBasketDto.FridgeBasketsResponse> getFridgesBasket(final Authentication authentication) {
        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        FridgeBasketDto.FridgeBasketsResponse data = new FridgeBasketDto.FridgeBasketsResponse(fridgeBasketService.countFridgeBasketsByUser(user), fridgeBasketService.retrieveFridgeBasketsByUser(user));

        return success(data);
    }

    @DeleteMapping("/basket")
    public BaseResponse<Void> deleteFridgeBasket(final Authentication authentication, @RequestParam(value = "ingredient") String ingredient) {
        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        fridgeBasketService.deleteFridgeBasket(user, ingredient);

        return success();
    }

    @ResponseBody
    @PatchMapping("/basket")
    public BaseResponse<Void> patchFridgeBaskets(final Authentication authentication, @RequestBody FridgeBasketDto.FridgeBasketsRequest request) {
        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        fridgeBasketService.updateFridgeBaskets(request, user);

        return success();
    }

    @GetMapping("/basket/count")
    public BaseResponse<FridgeBasketDto.FridgeBasketsCountResponse> getFridgesBasketCount(final Authentication authentication) {
        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        FridgeBasketDto.FridgeBasketsCountResponse data = new FridgeBasketDto.FridgeBasketsCountResponse(fridgeBasketService.countFridgeBasketsByUser(user));

        return success(data);
    }
}
