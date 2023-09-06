package com.recipe.app.src.recipe.application.dto;

import com.recipe.app.src.recipe.domain.*;
import com.recipe.app.src.user.domain.User;
import lombok.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class RecipeDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class RecipeRequest {
        private String thumbnail;
        private String title;
        private String content;
        private List<RecipeIngredientRequest> ingredients;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class RecipeIngredientRequest {
        private Long ingredientId;
        private String capacity;
    }

    @Getter
    @Builder
    public static class RecipeResponse {
        private Long recipeId;
        private String recipeNm;
        private String introduction;
        private String imgUrl;
        private String postUserName;
        private String postDate;
        private String linkUrl;
        private Boolean isUserScrap;
        private long scrapCnt;
        private long viewCnt;

        public static RecipeResponse from(Recipe recipe, User user) {

            return RecipeResponse.builder()
                    .recipeId(recipe.getRecipeId())
                    .recipeNm(recipe.getRecipeNm())
                    .imgUrl(recipe.getImgUrl())
                    .postUserName(recipe.getUser() != null ? recipe.getUser().getNickname() : null)
                    .postDate(recipe.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.M.d")))
                    .isUserScrap(recipe.isScrapByUser(user))
                    .scrapCnt(recipe.getScrapUsers().size())
                    .viewCnt(recipe.getViewUsers().size())
                    .build();
        }

        public static RecipeResponse from(BlogRecipe recipe, User user) {
            return RecipeResponse.builder()
                    .recipeId(recipe.getBlogRecipeId())
                    .recipeNm(recipe.getTitle())
                    .imgUrl(recipe.getBlogThumbanilImgUrl())
                    .postUserName(recipe.getBlogName())
                    .postDate(recipe.getPublishedAt().format(DateTimeFormatter.ofPattern("yyyy.M.d")))
                    .linkUrl(recipe.getBlogUrl())
                    .isUserScrap(recipe.isScrapByUser(user))
                    .scrapCnt(recipe.getScrapUsers().size())
                    .viewCnt(recipe.getViewUsers().size())
                    .build();
        }

        public static RecipeResponse from(YoutubeRecipe recipe, User user) {
            return RecipeResponse.builder()
                    .recipeId(recipe.getYoutubeRecipeId())
                    .recipeNm(recipe.getTitle())
                    .imgUrl(recipe.getThumbnailImgUrl())
                    .postUserName(recipe.getChannelName())
                    .postDate(recipe.getPostDate().format(DateTimeFormatter.ofPattern("yyyy.M.d")))
                    .linkUrl("https://www.youtube.com/watch?v=" + recipe.getYoutubeId())
                    .isUserScrap(recipe.isScrapByUser(user))
                    .scrapCnt(recipe.getScrapUsers().size())
                    .viewCnt(recipe.getViewUsers().size())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class RecipeDetailResponse {
        private Long recipeId;
        private String recipeNm;
        private String introduction;
        private String imgUrl;
        private Long cookingTime;
        private String level;
        private List<RecipeIngredientResponse> recipeIngredients;
        private List<RecipeProcessResponse> recipeProcesses;
        private Boolean isUserScrap;
        private long scrapCnt;
        private long viewCnt;

        public static RecipeDetailResponse from(Recipe recipe, User user) {
            return RecipeDetailResponse.builder()
                    .recipeId(recipe.getRecipeId())
                    .recipeNm(recipe.getRecipeNm())
                    .introduction(recipe.getIntroduction())
                    .imgUrl(recipe.getImgUrl())
                    .cookingTime(recipe.getCookingTime())
                    .level(recipe.getLevelNm())
                    .recipeIngredients(recipe.getRecipeIngredients().stream()
                            .map(ri -> RecipeIngredientResponse.from(ri, user))
                            .collect(Collectors.toList()))
                    .recipeProcesses(recipe.getRecipeProcesses().stream()
                            .map(RecipeProcessResponse::from)
                            .collect(Collectors.toList()))
                    .isUserScrap(recipe.isScrapByUser(user))
                    .scrapCnt(recipe.getScrapUsers().size())
                    .viewCnt(recipe.getViewUsers().size())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class RecipeIngredientResponse {
        private Long recipeIngredientId;
        private String recipeIngredientName;
        private String recipeIngredientIconUrl;
        private String recipeIngredientCapacity;
        private Boolean isInUserFridge;

        public static RecipeIngredientResponse from(RecipeIngredient recipeIngredient, User user) {
            return RecipeIngredientResponse.builder()
                    .recipeIngredientId(recipeIngredient.getRecipeIngredientId())
                    .recipeIngredientName(recipeIngredient.getIngredient().getIngredientName())
                    .recipeIngredientIconUrl(recipeIngredient.getIngredient().getIngredientIconUrl())
                    .recipeIngredientCapacity(recipeIngredient.getCapacity())
                    .isInUserFridge(user.hasIngredientInFridges(recipeIngredient.getIngredient()))
                    .build();
        }
    }

    @Getter
    @Builder
    public static class RecipeProcessResponse {
        private Long recipeProcessId;
        private Integer recipeProcessNo;
        private String recipeProcessDescription;
        private String recipeProcessImgUrl;

        public static RecipeProcessResponse from(RecipeProcess recipeProcess) {
            return RecipeProcessResponse.builder()
                    .recipeProcessId(recipeProcess.getRecipeProcessId())
                    .recipeProcessNo(recipeProcess.getCookingNo())
                    .recipeProcessDescription(recipeProcess.getCookingDescription())
                    .recipeProcessImgUrl(recipeProcess.getRecipeProcessImgUrl())
                    .build();
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
