package com.recipe.app.src.recipe.application.dto;

import com.recipe.app.src.ingredient.domain.Ingredient;
import com.recipe.app.src.ingredient.infra.IngredientEntity;
import com.recipe.app.src.recipe.domain.RecipeInfo;
import com.recipe.app.src.recipe.domain.RecipeIngredient;
import com.recipe.app.src.recipe.domain.RecipeProcess;
import com.recipe.app.src.user.domain.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

public class RecipeInfoDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class RecipeResponse {
        private Integer recipeId;
        private String title;
        private String description;
        private String thumbnail;
        private String UserScrapYN;
        private long userScrapCnt;

        public RecipeResponse(RecipeInfo recipe, User user) {
            this(recipe.getRecipeId(),
                    recipe.getRecipeNmKo(),
                    recipe.getSumry(),
                    recipe.getImgUrl(),
                    user.getScrapPublics().stream()
                            .anyMatch((s) -> s.getRecipeInfo().getRecipeId().equals(recipe.getRecipeId()) && s.getStatus().equals("ACTIVE")) ? "Y" : "N",
                    recipe.getScrapPublics().stream()
                            .filter((s) -> s.getStatus().equals("ACTIVE"))
                            .count()
            );
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class RecipeDetailResponse {
        private Integer recipeId;
        private String recipeName;
        private String summary;
        private String thumbnail;
        private String cookingTime;
        private String level;
        private List<RecipeIngredientResponse> recipeIngredientList;
        private List<RecipeProcessResponse> recipeProcessList;
        private String userScrapYN;
        private long userScrapCnt;

        public RecipeDetailResponse(RecipeInfo recipe, User user, List<Ingredient> ingredients) {
            this.recipeId = recipe.getRecipeId();
            this.recipeName = recipe.getRecipeNmKo();
            this.summary = recipe.getSumry();
            this.thumbnail = recipe.getImgUrl();
            this.cookingTime = recipe.getCookingTime();
            this.level = recipe.getLevelNm();
            this.recipeIngredientList = recipe.getRecipeIngredients().stream()
                    .map((ingredient) -> new RecipeIngredientResponse(ingredient, user, ingredients))
                    .collect(Collectors.toList());
            this.recipeProcessList = recipe.getRecipeProcesses().stream()
                    .map(RecipeProcessResponse::new)
                    .collect(Collectors.toList());
            this.userScrapYN = user.getScrapPublics().stream()
                    .anyMatch((s) -> s.getRecipeInfo().getRecipeId().equals(recipe.getRecipeId()) && s.getStatus().equals("ACTIVE")) ? "Y" : "N";
            this.userScrapCnt = recipe.getScrapPublics().stream()
                    .filter((s) -> s.getStatus().equals("ACTIVE"))
                    .count();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class RecipeIngredientResponse {
        private Integer recipeIngredientIdx;
        private String recipeIngredientName;
        private String recipeIngredientIcon;
        private String recipeIngredientCpcty;
        private String inFridgeYN;

        public RecipeIngredientResponse(RecipeIngredient ingredient, User user, List<Ingredient> ingredients) {
            this.recipeIngredientIdx = ingredient.getIdx();
            this.recipeIngredientName = ingredient.getIrdntNm();
            this.recipeIngredientIcon = ingredient.getIcon(ingredients);
            this.recipeIngredientCpcty = ingredient.getIrdntCpcty();
            this.inFridgeYN = user.getFridges().stream()
                    .anyMatch((f) -> f.getStatus().equals("ACTIVE")
                            && recipeIngredientName.contains(f.getIngredientName())
                            && recipeIngredientIcon != null
                            && recipeIngredientIcon.equals(f.getIngredientIcon())) ? "Y" : "N";
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class RecipeProcessResponse {
        private Integer recipeProcessIdx;
        private Integer recipeProcessNo;
        private String recipeProcessDc;
        private String recipeProcessImg;

        public RecipeProcessResponse(RecipeProcess recipeProcess) {
            this(
                    recipeProcess.getIdx(),
                    recipeProcess.getCookingNo(),
                    recipeProcess.getCookingDc(),
                    recipeProcess.getStreStepImageUrl()
            );
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class BlogRecipesResponse {
        private Integer total;
        private List<BlogRecipeResponse> blogList;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class BlogRecipeResponse {
        private String title;
        private String blogUrl;
        private String description;
        private String bloggerName;
        private String postDate;
        private String thumbnail;
        private String userScrapYN;
        private Integer userScrapCnt;
    }

}
