package com.recipe.app.src.recipe.application.dto;

import com.recipe.app.src.recipe.domain.Recipe;
import com.recipe.app.src.recipe.domain.RecipeScrap;
import com.recipe.app.src.recipe.domain.Recipes;
import com.recipe.app.src.recipe.domain.blog.BlogRecipes;
import com.recipe.app.src.recipe.domain.blog.BlogScrap;
import com.recipe.app.src.recipe.domain.youtube.YoutubeRecipes;
import com.recipe.app.src.recipe.domain.youtube.YoutubeScrap;
import com.recipe.app.src.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Schema(description = "레시피 목록 응답 DTO")
@Getter
public class RecipesResponse {

    @Schema(description = "레시피 전체 갯수")
    private final long totalCnt;
    @Schema(description = "레시피 목록")
    private final List<RecipeResponse> recipes;

    @Builder
    public RecipesResponse(long totalCnt, List<RecipeResponse> recipes) {
        this.totalCnt = totalCnt;
        this.recipes = recipes;
    }

    public static RecipesResponse from(long totalCnt, Recipes recipes, List<User> recipePostUsers, List<RecipeScrap> recipeScraps, User user) {

        Map<Long, User> recipePostUserMapByUserId = recipePostUsers.stream()
                .collect(Collectors.toMap(User::getUserId, Function.identity()));

        return RecipesResponse.builder()
                .totalCnt(totalCnt)
                .recipes(recipes.getRecipes().stream()
                        .map((recipe) -> RecipeResponse.from(recipe,
                                recipePostUserMapByUserId.get(recipe.getUserId()),
                                recipeScraps,
                                user))
                        .collect(Collectors.toList()))
                .build();
    }

    public static RecipesResponse from(long totalCnt, Recipes recipes, List<User> recipePostUsers, List<RecipeScrap> recipeScraps, User user,
                                       List<String> ingredientNamesInFridge) {

        Set<String> normalizedFridge = ingredientNamesInFridge.stream()
                .filter(Objects::nonNull)
                .map(s -> s.toLowerCase().trim())
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());

        Map<Long, User> recipePostUserMapByUserId = recipePostUsers.stream()
                .collect(Collectors.toMap(User::getUserId, Function.identity()));

        return RecipesResponse.builder()
                .totalCnt(totalCnt)
                .recipes(recipes.getRecipes().stream()
                        .map((recipe) -> RecipeResponse.from(recipe,
                                recipePostUserMapByUserId.get(recipe.getUserId()),
                                recipeScraps,
                                user,
                                recipe.calculateIngredientMatchRate(normalizedFridge)))
                        .collect(Collectors.toList()))
                .build();
    }

    // 냉장고 추천용: 재료 일치도 계산 후 일치도(내림차순)로 정렬하고 keyset 페이지네이션을 적용한다.
    public static RecipesResponse from(Recipes recipes, List<User> recipePostUsers, List<RecipeScrap> recipeScraps, User user,
                                       List<String> ingredientNamesInFridge, Recipe lastRecipe, int size) {

        Set<String> normalizedFridge = ingredientNamesInFridge.stream()
                .filter(Objects::nonNull)
                .map(s -> s.toLowerCase().trim())
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());

        Map<Long, User> recipePostUserMapByUserId = recipePostUsers.stream()
                .collect(Collectors.toMap(User::getUserId, Function.identity()));

        return RecipesResponse.builder()
                .totalCnt(recipes.size())
                .recipes(recipes.getRecipes().stream()
                        .map((recipe) -> RecipeResponse.from(recipe,
                                recipePostUserMapByUserId.get(recipe.getUserId()),
                                recipeScraps,
                                user,
                                recipe.calculateIngredientMatchRate(normalizedFridge)))
                        .sorted(Comparator.comparing(RecipeResponse::getIngredientsMatchRate).thenComparing(RecipeResponse::getRecipeId).reversed())
                        .filter(recommendedRecipe -> {
                            if (lastRecipe == null) {
                                return true;
                            }

                            return recommendedRecipe.getRecipeId() < lastRecipe.getRecipeId()
                                    && recommendedRecipe.getIngredientsMatchRate() <= lastRecipe.calculateIngredientMatchRate(normalizedFridge);
                        })
                        .limit(size)
                        .collect(Collectors.toList()))
                .build();
    }

    public static RecipesResponse from(long totalCnt, BlogRecipes recipes, List<BlogScrap> recipeScraps, User user) {

        return RecipesResponse.builder()
                .totalCnt(totalCnt)
                .recipes(recipes.getBlogRecipes().stream()
                        .map((recipe) -> RecipeResponse.from(recipe, recipeScraps, user))
                        .collect(Collectors.toList()))
                .build();
    }

    public static RecipesResponse from(long totalCnt, YoutubeRecipes recipes, List<YoutubeScrap> recipeScraps, User user) {

        return RecipesResponse.builder()
                .totalCnt(totalCnt)
                .recipes(recipes.getYoutubeRecipes().stream()
                        .map((recipe) -> RecipeResponse.from(recipe, recipeScraps, user))
                        .collect(Collectors.toList()))
                .build();
    }
}
