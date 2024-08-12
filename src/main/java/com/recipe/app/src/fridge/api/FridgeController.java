package com.recipe.app.src.fridge.api;

import com.recipe.app.src.common.aop.LoginCheck;
import com.recipe.app.src.fridge.application.FridgeService;
import com.recipe.app.src.fridge.application.dto.FridgeRequest;
import com.recipe.app.src.fridge.application.dto.FridgeResponse;
import com.recipe.app.src.fridge.application.dto.FridgesResponse;
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

@Tag(name = "냉장고 Controller")
@RestController
@RequestMapping("/fridges")
public class FridgeController {

    private final FridgeService fridgeService;

    public FridgeController(FridgeService fridgeService) {
        this.fridgeService = fridgeService;
    }

    @Operation(summary = "냉장고 채우기 API")
    @PostMapping
    @LoginCheck
    public void postFridges(@Parameter(hidden = true) User user) {

        fridgeService.create(user);
    }

    @Operation(summary = "냉장고 목록 조회 API")
    @GetMapping
    @LoginCheck
    public FridgesResponse getFridges(@Parameter(hidden = true) User user) {

        return fridgeService.getFridges(user);
    }

    @Operation(summary = "냉장고 상세 조회 API")
    @GetMapping("/{fridgeId}")
    @LoginCheck
    public FridgeResponse getFridge(@Parameter(hidden = true) User user, @PathVariable Long fridgeId) {

        return fridgeService.getFridge(user, fridgeId);
    }

    @Operation(summary = "냉장고 삭제 API")
    @DeleteMapping("/{fridgeId}")
    @LoginCheck
    public void deleteFridge(@Parameter(hidden = true) User user, @PathVariable Long fridgeId) {

        fridgeService.delete(user, fridgeId);
    }

    @Operation(summary = "냉장고 수정 API")
    @PatchMapping("/{fridgeId}")
    @LoginCheck
    public void patchFridge(@Parameter(hidden = true) User user,
                            @PathVariable Long fridgeId,
                            @Parameter(name = "냉장고 수정 입력 정보", required = true)
                            @RequestBody FridgeRequest request) {

        fridgeService.update(user, fridgeId, request);
    }
}
