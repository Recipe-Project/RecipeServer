package com.recipe.app.src.ingredient.api;

import com.recipe.app.src.common.aop.LoginCheck;
import com.recipe.app.src.ingredient.application.IngredientFacadeService;
import com.recipe.app.src.ingredient.application.IngredientService;
import com.recipe.app.src.ingredient.application.dto.IngredientCreateResponse;
import com.recipe.app.src.ingredient.application.dto.IngredientRequest;
import com.recipe.app.src.ingredient.application.dto.IngredientsResponse;
import com.recipe.app.src.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "재료 Controller")
@RestController
@RequestMapping("/ingredients")
public class IngredientController {

    private final IngredientFacadeService ingredientFacadeService;
    private final IngredientService ingredientService;

    public IngredientController(IngredientFacadeService ingredientFacadeService, IngredientService ingredientService) {
        this.ingredientFacadeService = ingredientFacadeService;
        this.ingredientService = ingredientService;
    }

    @Operation(summary = "재료 목록 조회 API")
    @GetMapping("")
    @LoginCheck
    public IngredientsResponse getIngredients(@Parameter(hidden = true) User user,
                                              @Parameter(name = "검색어", example = "감자")
                                              @RequestParam(value = "keyword", required = false) @Nullable String keyword) {

        return ingredientFacadeService.findIngredientsByKeyword(user, keyword);
    }

    @Operation(summary = "나만의 재료 목록 조회 API")
    @GetMapping("/my")
    @LoginCheck
    public IngredientsResponse getMyIngredients(@Parameter(hidden = true) User user) {

        return ingredientFacadeService.findIngredientsByUser(user);
    }

    @Operation(summary = "나만의 재료 등록 API")
    @PostMapping("")
    @LoginCheck
    public IngredientCreateResponse postIngredient(@Parameter(hidden = true) User user,
                                                   @Parameter(name = "재료 추가 정보", required = true)
                                                   @RequestBody IngredientRequest request) {

        return ingredientService.create(user.getUserId(), request);
    }

    @Operation(summary = "나만의 재료 삭제 API")
    @DeleteMapping("/{ingredientId}")
    @LoginCheck
    public void deleteIngredient(@Parameter(hidden = true) User user, @PathVariable Long ingredientId) {

        ingredientFacadeService.deleteIngredient(user, ingredientId);
    }
}