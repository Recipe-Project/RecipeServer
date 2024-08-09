package com.recipe.app.src.fridge.api;

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
    public void postFridges(@ApiIgnore final Authentication authentication) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        fridgeService.create(user);
    }

    @ApiOperation(value = "냉장고 목록 조회 API")
    @GetMapping
    public FridgesResponse getFridges(@ApiIgnore final Authentication authentication) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        return fridgeService.getFridges(user);
    }

    @ApiOperation(value = "냉장고 상세 조회 API")
    @GetMapping("/{fridgeId}")
    public FridgeResponse getFridge(@ApiIgnore final Authentication authentication, @PathVariable Long fridgeId) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        return fridgeService.getFridge(user, fridgeId);
    }

    @ApiOperation(value = "냉장고 삭제 API")
    @DeleteMapping("/{fridgeId}")
    public void deleteFridge(@ApiIgnore final Authentication authentication, @PathVariable Long fridgeId) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        fridgeService.delete(user, fridgeId);
    }

    @ApiOperation(value = "냉장고 수정 API")
    @PatchMapping("/{fridgeId}")
    public void patchFridge(@ApiIgnore final Authentication authentication, @PathVariable Long fridgeId,
                                          @ApiParam(value = "냉장고 수정 입력 정보", required = true)
                                          @RequestBody FridgeRequest request) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        fridgeService.update(user, fridgeId, request);
    }
}
