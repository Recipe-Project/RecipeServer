package com.recipe.app.src.ingredient.api;

import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.src.fridgeBasket.application.FridgeBasketService;
import com.recipe.app.src.ingredient.application.IngredientService;
import com.recipe.app.src.ingredient.application.dto.IngredientDto;
import com.recipe.app.src.ingredient.domain.Ingredient;
import com.recipe.app.src.user.domain.SecurityUser;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.user.exception.UserTokenNotExistException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

import static com.recipe.app.common.response.BaseResponse.success;

@Api(value = "재료 Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/ingredients")
public class IngredientController {

    private final FridgeBasketService fridgeBasketService;
    private final IngredientService ingredientService;

    @ApiOperation(value = "재료 조회 API")
    @GetMapping("")
    public BaseResponse<IngredientDto.IngredientsResponse> getIngredients(final Authentication authentication, @RequestParam(value = "keyword") @Nullable String keyword) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        IngredientDto.IngredientsResponse data = new IngredientDto.IngredientsResponse(
                user.hasFridgeBaskets(),
                ingredientService.getIngredientsGroupingByIngredientCategory(keyword, user));

        return success(data);
    }

}