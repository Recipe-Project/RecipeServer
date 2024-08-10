package com.recipe.app.src.fridge.api;

import com.recipe.app.src.common.aop.LoginCheck;
import com.recipe.app.src.fridge.application.FridgeService;
import com.recipe.app.src.fridge.application.dto.FridgeRequest;
import com.recipe.app.src.fridge.application.dto.FridgeResponse;
import com.recipe.app.src.fridge.application.dto.FridgesResponse;
import com.recipe.app.src.user.domain.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

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
    @LoginCheck
    public void postFridges(@ApiIgnore User user) {

        fridgeService.create(user);
    }

    @ApiOperation(value = "냉장고 목록 조회 API")
    @GetMapping
    @LoginCheck
    public FridgesResponse getFridges(@ApiIgnore User user) {

        return fridgeService.getFridges(user);
    }

    @ApiOperation(value = "냉장고 상세 조회 API")
    @GetMapping("/{fridgeId}")
    @LoginCheck
    public FridgeResponse getFridge(@ApiIgnore User user, @PathVariable Long fridgeId) {

        return fridgeService.getFridge(user, fridgeId);
    }

    @ApiOperation(value = "냉장고 삭제 API")
    @DeleteMapping("/{fridgeId}")
    @LoginCheck
    public void deleteFridge(@ApiIgnore User user, @PathVariable Long fridgeId) {

        fridgeService.delete(user, fridgeId);
    }

    @ApiOperation(value = "냉장고 수정 API")
    @PatchMapping("/{fridgeId}")
    @LoginCheck
    public void patchFridge(@ApiIgnore User user,
                            @PathVariable Long fridgeId,
                            @ApiParam(value = "냉장고 수정 입력 정보", required = true)
                            @RequestBody FridgeRequest request) {

        fridgeService.update(user, fridgeId, request);
    }
}
