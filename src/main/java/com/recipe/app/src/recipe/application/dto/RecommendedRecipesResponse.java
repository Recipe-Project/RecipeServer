package com.recipe.app.src.recipe.application.dto;

import com.recipe.app.src.recipe.domain.Recipe;
import com.recipe.app.src.recipe.domain.RecipeScrap;
import com.recipe.app.src.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Schema(description = "냉장고 추천 레시피 목록 응답 DTO")
@Getter
public class RecommendedRecipesResponse {

    @Schema(description = "레시피 전체 갯수")
    private final long totalCnt;
    @Schema(description = "레시피 목록")
    private final List<RecommendedRecipeResponse> recipes;

    @Builder
    public RecommendedRecipesResponse(long totalCnt, List<RecommendedRecipeResponse> recipes) {

        this.totalCnt = totalCnt;
        this.recipes = recipes;
    }

    public static RecommendedRecipesResponse from(List<Recipe> recipes, List<User> recipePostUsers, List<RecipeScrap> recipeScraps, User user,
                                                  List<String> ingredientNamesInFridge, Recipe lastRecipe, int size) {

        Map<Long, User> recipePostUserMapByUserId = recipePostUsers.stream()
                .collect(Collectors.toMap(User::getUserId, Function.identity()));

        return RecommendedRecipesResponse.builder()
                .totalCnt(recipes.size())
                .recipes(recipes.stream()
                        .map((recipe) -> RecommendedRecipeResponse.from(recipe,
                                recipePostUserMapByUserId.get(recipe.getUserId()),
                                recipe.calculateIngredientMatchRate(ingredientNamesInFridge),
                                recipeScraps,
                                user))
                        .sorted(Comparator.comparing(RecommendedRecipeResponse::getIngredientsMatchRate).thenComparing(RecommendedRecipeResponse::getRecipeId).reversed())
                        .filter(recommendedRecipe -> recommendedRecipe.getRecipeId() > lastRecipe.getRecipeId()
                                || recommendedRecipe.getIngredientsMatchRate() >= lastRecipe.calculateIngredientMatchRate(ingredientNamesInFridge))
                        .limit(size)
                        .collect(Collectors.toList()))
                .build();
    }
}
