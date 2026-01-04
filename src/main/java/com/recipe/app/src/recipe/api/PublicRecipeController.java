package com.recipe.app.src.recipe.api;

import com.recipe.app.src.recipe.application.RecipeSearchService;
import com.recipe.app.src.recipe.application.dto.RecipeDetailResponse;
import com.recipe.app.src.recipe.application.dto.RecommendedRecipesResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@Tag(name = "공개 레시피 Controller")
@RestController
@RequestMapping("/recipes/public")
public class PublicRecipeController {

    private final RecipeSearchService recipeSearchService;

    public PublicRecipeController(RecipeSearchService recipeSearchService) {
        this.recipeSearchService = recipeSearchService;
    }

    @Operation(summary = "공개 레시피 상세 조회 API (로그인 불필요)")
    @GetMapping("/{recipeId}")
    public RecipeDetailResponse getPublicRecipe(@PathVariable long recipeId) {

        return recipeSearchService.findPublicRecipeDetail(recipeId);
    }

    @Operation(summary = "공개 추천 레시피 목록 조회 API (로그인 불필요)")
    @GetMapping("/recommendation")
    public RecommendedRecipesResponse getPublicRecipesByIngredients(
            @Parameter(example = "감자,당근,양파", name = "재료 목록 (쉼표로 구분)")
            @RequestParam(value = "ingredients") String ingredients,
            @Parameter(example = "0", name = "마지막 조회 레시피 아이디")
            @RequestParam(value = "startAfter", defaultValue = "0") long startAfter,
            @Parameter(example = "20", name = "사이즈")
            @RequestParam(value = "size", defaultValue = "20") int size) {

        List<String> ingredientNames = Arrays.stream(ingredients.split(","))
                .map(String::trim)
                .toList();

        return recipeSearchService.findPublicRecommendedRecipesByIngredients(ingredientNames, startAfter, size);
    }
}