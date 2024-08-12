package com.recipe.app.src.fridgeBasket.api;

import com.recipe.app.src.common.aop.LoginCheck;
import com.recipe.app.src.fridgeBasket.application.FridgeBasketService;
import com.recipe.app.src.fridgeBasket.application.dto.FridgeBasketCountResponse;
import com.recipe.app.src.fridgeBasket.application.dto.FridgeBasketRequest;
import com.recipe.app.src.fridgeBasket.application.dto.FridgeBasketsRequest;
import com.recipe.app.src.fridgeBasket.application.dto.FridgeBasketsResponse;
import com.recipe.app.src.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "냉장고 바구니 Controller")
@RestController
@RequestMapping("/fridges/basket")
public class FridgeBasketController {

    private final FridgeBasketService fridgeBasketService;

    public FridgeBasketController(FridgeBasketService fridgeBasketService) {
        this.fridgeBasketService = fridgeBasketService;
    }

    @Operation(summary = "재료 선택하여 냉장고 바구니 채우기 API")
    @PostMapping
    @LoginCheck
    public void postFridgeBasketByIngredientId(@Parameter(hidden = true) User user,
                                               @Parameter(name = "재료 선택 목록", required = true)
                                               @RequestBody FridgeBasketsRequest request) {

        fridgeBasketService.create(user, request);
    }

    @Operation(summary = "냉장고 바구니 조회 API")
    @GetMapping
    @LoginCheck
    public FridgeBasketsResponse getFridgeBaskets(@Parameter(hidden = true) User user) {

        return fridgeBasketService.findAllByUser(user);
    }

    @Operation(summary = "냉장고 바구니 삭제 API")
    @DeleteMapping("/{fridgeBasketId}")
    @LoginCheck
    public void deleteFridgeBasket(@Parameter(hidden = true) User user, @PathVariable Long fridgeBasketId) {

        fridgeBasketService.delete(user, fridgeBasketId);
    }

    @Operation(summary = "냉장고 바구니 수정 API")
    @PatchMapping("/{fridgeBasketId}")
    @LoginCheck
    public void patchFridgeBasket(@Parameter(hidden = true) User user, @PathVariable Long fridgeBasketId,
                                  @Parameter(name = "냉장고 바구니 수정 정보", required = true)
                                  @RequestBody FridgeBasketRequest request) {

        fridgeBasketService.update(user, fridgeBasketId, request);
    }

    @Operation(summary = "냉장고 바구니 갯수 조회 API")
    @GetMapping("/count")
    @LoginCheck
    public FridgeBasketCountResponse getFridgeBasketCount(@Parameter(hidden = true) User user) {

        return fridgeBasketService.countFridgeBasketByUser(user);
    }
}
