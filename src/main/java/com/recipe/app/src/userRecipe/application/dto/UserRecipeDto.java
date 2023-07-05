package com.recipe.app.src.userRecipe.application.dto;

import com.recipe.app.src.userRecipe.domain.UserRecipe;
import com.recipe.app.src.userRecipe.domain.UserRecipeIngredient;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

public class UserRecipeDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class UserRecipeRequest {
        private String thumbnail;
        private String title;
        private String content;
        private List<UserRecipeIngredientRequest> ingredientList;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class UserRecipeIngredientRequest {
        private String ingredientName;
        private String ingredientIcon;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class UserRecipesResponse {
        private int userRecipeIdx;
        private String thumbnail;
        private String title;
        private String content;

        public UserRecipesResponse(UserRecipe userRecipe) {
            this.userRecipeIdx = userRecipe.getUserRecipeIdx();
            this.thumbnail = userRecipe.getThumbnail();
            this.title = userRecipe.getTitle();
            this.content = userRecipe.getContent().length() > 50 ? this.content.substring(0, 50) : this.content;
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class UserRecipeResponse {
        private String thumbnail;
        private String title;
        private String content;
        private List<UserRecipeIngredientResponse> ingredients;

        public UserRecipeResponse(UserRecipe userRecipe) {
            this.thumbnail = userRecipe.getThumbnail();
            this.title = userRecipe.getTitle();
            this.content = userRecipe.getContent();
            this.ingredients = userRecipe.getUserRecipeIngredients().stream()
                    .map(UserRecipeIngredientResponse::new)
                    .collect(Collectors.toList());
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class UserRecipeIngredientResponse {
        private String ingredientName;
        private String ingredientIcon;

        public UserRecipeIngredientResponse(UserRecipeIngredient ingredient) {
            this.ingredientName = ingredient.getIngredientName();
            this.ingredientIcon = ingredient.getIngredientIcon();
        }
    }
}
