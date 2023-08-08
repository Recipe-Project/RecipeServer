package com.recipe.app.src.fridgeBasket.api;

import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.src.fridgeBasket.application.FridgeBasketService;
import com.recipe.app.src.fridgeBasket.application.dto.FridgeBasketDto;
import com.recipe.app.src.fridgeBasket.domain.FridgeBasket;
import com.recipe.app.src.user.domain.SecurityUser;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.user.exception.UserTokenNotExistException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.recipe.app.common.response.BaseResponse.success;

@RestController
@RequiredArgsConstructor
@RequestMapping("/fridges/basket")
public class FridgeBasketController {

    private final FridgeBasketService fridgeBasketService;

    @ResponseBody
    @PostMapping("")
    public BaseResponse<FridgeBasketDto.FridgeBasketsResponse> postFridgeBasketByIngredientId(final Authentication authentication, @RequestBody FridgeBasketDto.FridgeBasketIngredientIdsRequest request) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        List<FridgeBasket> fridgeBaskets = fridgeBasketService.createFridgeBasketByIngredientId(user, request);
        FridgeBasketDto.FridgeBasketsResponse data = FridgeBasketDto.FridgeBasketsResponse.from(fridgeBaskets);

        return success(data);
    }

    @ResponseBody
    @PostMapping("/direct")
    public BaseResponse<FridgeBasketDto.FridgeBasketsResponse> postFridgeBasketWithIngredientSave(final Authentication authentication, @RequestBody FridgeBasketDto.FridgeBasketIngredientRequest request) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        List<FridgeBasket> fridgeBaskets = fridgeBasketService.createFridgeBasketWithIngredientSave(user, request);
        FridgeBasketDto.FridgeBasketsResponse data = FridgeBasketDto.FridgeBasketsResponse.from(fridgeBaskets);

        return success(data);
    }

    @GetMapping("")
    public BaseResponse<FridgeBasketDto.FridgeBasketsResponse> getFridgeBasket(final Authentication authentication) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        FridgeBasketDto.FridgeBasketsResponse data = FridgeBasketDto.FridgeBasketsResponse.from(fridgeBasketService.getFridgeBasketsByUser(user));

        return success(data);
    }

    @DeleteMapping("")
    public BaseResponse<Void> deleteFridgeBasket(final Authentication authentication, @RequestBody FridgeBasketDto.FridgeBasketIdsRequest request) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        fridgeBasketService.deleteFridgeBaskets(user, request);

        return success();
    }

    @ResponseBody
    @PatchMapping("")
    public BaseResponse<FridgeBasketDto.FridgeBasketsResponse> patchFridgeBasket(final Authentication authentication, @RequestBody FridgeBasketDto.FridgeBasketsRequest request) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        FridgeBasketDto.FridgeBasketsResponse data = FridgeBasketDto.FridgeBasketsResponse.from(fridgeBasketService.updateFridgeBaskets(user, request));

        return success(data);
    }

    @GetMapping("/count")
    public BaseResponse<FridgeBasketDto.FridgeBasketCountResponse> getFridgeBasketCount(final Authentication authentication) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();
        FridgeBasketDto.FridgeBasketCountResponse data = new FridgeBasketDto.FridgeBasketCountResponse(fridgeBasketService.countFridgeBasketByUser(user));

        return success(data);
    }
}
