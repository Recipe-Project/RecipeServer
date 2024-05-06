package com.recipe.app.src.ingredient.api;

import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.src.ingredient.application.IngredientFacadeService;
import com.recipe.app.src.ingredient.application.IngredientService;
import com.recipe.app.src.ingredient.application.dto.IngredientRequest;
import com.recipe.app.src.ingredient.application.dto.IngredientsResponse;
import com.recipe.app.src.user.domain.SecurityUser;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.user.exception.UserTokenNotExistException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import static com.recipe.app.common.response.BaseResponse.success;

@Api(tags = {"재료 Controller"})
@RestController
@RequestMapping("/ingredients")
public class IngredientController {

    private final IngredientFacadeService ingredientFacadeService;
    private final IngredientService ingredientService;

    public IngredientController(IngredientFacadeService ingredientFacadeService, IngredientService ingredientService) {
        this.ingredientFacadeService = ingredientFacadeService;
        this.ingredientService = ingredientService;
    }

    @ApiOperation(value = "재료 목록 조회 API")
    @GetMapping("")
    public BaseResponse<IngredientsResponse> getIngredients(@ApiIgnore final Authentication authentication,
                                                            @ApiParam(name = "keyword", type = "String", example = "감자", value = "검색어")
                                                            @RequestParam(value = "keyword", required = false) @Nullable String keyword) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        return success(ingredientFacadeService.findIngredientsByKeyword(user, keyword));
    }

    @ApiOperation(value = "재료 등록 API")
    @PostMapping("")
    public BaseResponse<Void> postIngredient(@ApiIgnore final Authentication authentication,
                                             @ApiParam(value = "재료 추가 정보", required = true)
                                             @RequestBody IngredientRequest request) {

        if (authentication == null)
            throw new UserTokenNotExistException();

        User user = ((SecurityUser) authentication.getPrincipal()).getUser();

        ingredientService.createIngredient(user.getUserId(), request);

        return success();
    }
}