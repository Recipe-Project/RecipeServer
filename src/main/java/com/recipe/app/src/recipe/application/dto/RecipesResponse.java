package com.recipe.app.src.recipe.application.dto;

import com.recipe.app.src.recipe.domain.Recipe;
import com.recipe.app.src.recipe.domain.RecipeScrap;
import com.recipe.app.src.recipe.domain.blog.BlogRecipes;
import com.recipe.app.src.recipe.domain.blog.BlogScrap;
import com.recipe.app.src.recipe.domain.youtube.YoutubeRecipes;
import com.recipe.app.src.recipe.domain.youtube.YoutubeScrap;
import com.recipe.app.src.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;
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

    public static RecipesResponse from(long totalCnt, List<Recipe> recipes, List<User> recipePostUsers, List<RecipeScrap> recipeScraps, User user) {

        Map<Long, User> recipePostUserMapByUserId = recipePostUsers.stream()
                .collect(Collectors.toMap(User::getUserId, Function.identity()));

        return RecipesResponse.builder()
                .totalCnt(totalCnt)
                .recipes(recipes.stream()
                        .map((recipe) -> RecipeResponse.from(recipe,
                                recipePostUserMapByUserId.get(recipe.getUserId()),
                                recipeScraps,
                                user))
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

        return new RecipesResponse(totalCnt, recipes.getYoutubeRecipes().stream()
                .map((recipe) -> RecipeResponse.from(recipe, recipeScraps, user))
                .collect(Collectors.toList()));
    }
}
