package com.recipe.app.src.recipe.application.dto;

import com.recipe.app.src.fridge.domain.Fridge;
import com.recipe.app.src.recipe.domain.*;
import com.recipe.app.src.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class RecipeDto {

    @Schema(description = "레시피 요청 DTO")
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class RecipeRequest {
        @Schema(description = "레시피 썸네일 이미지 url")
        private String thumbnail;
        @Schema(description = "레시피 제목")
        private String title;
        @Schema(description = "레시피 설명")
        private String content;
        @Schema(description = "레시피 재료 목록")
        private List<RecipeIngredientRequest> ingredients;
    }

    @Schema(description = "레시피 재료 요청 DTO")
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class RecipeIngredientRequest {
        @Schema(description = "재료 고유 번호 (재료 선택 시 추가, 직접 재료 입력 시 null)", nullable = true)
        private Long ingredientId;
        @Schema(description = "레시피 재료명 (직접 재료 입력 시 추가, 재료 선택 시 null)", nullable = true)
        private String ingredientName;
        @Schema(description = "레시피 재료 아이콘 url (직접 재료 입력 시 추가, 재료 선택 시 null)", nullable = true)
        private String ingredientIconUrl;
        @Schema(description = "레시피 재료 카테고리 고유 번호 (직접 재료 입력 시 추가, 재료 선택 시 null)", nullable = true)
        private Long ingredientCategoryId;
        @Schema(description = "레시피 재료 용량", nullable = true)
        private String capacity;
    }

    @Schema(description = "레시피 목록 응답 DTO")
    @Getter
    @AllArgsConstructor
    public static class RecipesResponse {
        @Schema(description = "레시피 전체 갯수")
        private long totalCnt;
        @Schema(description = "레시피 목록")
        private List<RecipeResponse> recipes;
    }

    @Schema(description = "레시피 응답 DTO")
    @Getter
    @Builder
    public static class RecipeResponse {
        @Schema(description = "레시피 고유 번호")
        private Long recipeId;
        @Schema(description = "레시피명")
        private String recipeName;
        @Schema(description = "소개글")
        private String introduction;
        @Schema(description = "썸네일 이미지 Url")
        private String thumbnailImgUrl;
        @Schema(description = "게시자")
        private String postUserName;
        @Schema(description = "게시일시")
        private String postDate;
        @Schema(description = "연결 링크 Url")
        private String linkUrl;
        @Schema(description = "스크랩 여부")
        private Boolean isUserScrap;
        @Schema(description = "스크랩 갯수")
        private long scrapCnt;
        @Schema(description = "조회수")
        private long viewCnt;
        @Schema(description = "재료 일치도")
        private Integer ingredientsMatchRate;

        public static RecipeResponse from(Recipe recipe, User user, int ingredientsMatchRate) {

            return RecipeResponse.builder()
                    .recipeId(recipe.getRecipeId())
                    .recipeName(recipe.getRecipeNm())
                    .introduction(recipe.getIntroduction())
                    .thumbnailImgUrl(recipe.getImgUrl())
                    .postUserName(recipe.getUser() != null ? recipe.getUser().getNickname() : null)
                    .postDate(recipe.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.M.d")))
                    .isUserScrap(recipe.isScrapByUser(user))
                    .scrapCnt(recipe.getScrapUsers().size())
                    .viewCnt(recipe.getViewUsers().size())
                    .ingredientsMatchRate(ingredientsMatchRate)
                    .build();
        }

        public static RecipeResponse from(Recipe recipe, User user) {

            return RecipeResponse.builder()
                    .recipeId(recipe.getRecipeId())
                    .recipeName(recipe.getRecipeNm())
                    .introduction(recipe.getIntroduction())
                    .thumbnailImgUrl(recipe.getImgUrl())
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
                    .recipeName(recipe.getTitle())
                    .introduction(recipe.getDescription())
                    .thumbnailImgUrl(recipe.getBlogThumbanilImgUrl())
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
                    .recipeName(recipe.getTitle())
                    .introduction(recipe.getDescription())
                    .thumbnailImgUrl(recipe.getThumbnailImgUrl())
                    .postUserName(recipe.getChannelName())
                    .postDate(recipe.getPostDate().format(DateTimeFormatter.ofPattern("yyyy.M.d")))
                    .linkUrl("https://www.youtube.com/watch?v=" + recipe.getYoutubeId())
                    .isUserScrap(recipe.isScrapByUser(user))
                    .scrapCnt(recipe.getScrapUsers().size())
                    .viewCnt(recipe.getViewUsers().size())
                    .build();
        }
    }

    @Schema(description = "레시피 상세 응답 DTO")
    @Getter
    @Builder
    public static class RecipeDetailResponse {
        @Schema(description = "레시피 고유 번호")
        private Long recipeId;
        @Schema(description = "레시피명")
        private String recipeName;
        @Schema(description = "소개글")
        private String introduction;
        @Schema(description = "썸네일 이미지 url")
        private String thumbnailImgUrl;
        @Schema(description = "조리 시간")
        private Long cookingTime;
        @Schema(description = "난이도")
        private String level;
        @Schema(description = "레시피 재료 목록")
        private List<RecipeIngredientResponse> recipeIngredients;
        @Schema(description = "레시피 과정 목록")
        private List<RecipeProcessResponse> recipeProcesses;
        @Schema(description = "스크랩 여부")
        private Boolean isUserScrap;
        @Schema(description = "총 스크랩수")
        private long scrapCnt;
        @Schema(description = "총 조회수")
        private long viewCnt;

        public static RecipeDetailResponse from(Recipe recipe, User user, List<Fridge> fridges, List<RecipeIngredient> recipeIngredients, List<RecipeProcess> recipeProcesses) {
            return RecipeDetailResponse.builder()
                    .recipeId(recipe.getRecipeId())
                    .recipeName(recipe.getRecipeNm())
                    .introduction(recipe.getIntroduction())
                    .thumbnailImgUrl(recipe.getImgUrl())
                    .cookingTime(recipe.getCookingTime())
                    .level(recipe.getLevelNm())
                    .recipeIngredients(recipeIngredients.stream()
                            .map(ri -> RecipeIngredientResponse.from(ri, fridges))
                            .collect(Collectors.toList()))
                    .recipeProcesses(recipeProcesses.stream()
                            .map(RecipeProcessResponse::from)
                            .collect(Collectors.toList()))
                    .isUserScrap(recipe.isScrapByUser(user))
                    .scrapCnt(recipe.getScrapUsers().size())
                    .viewCnt(recipe.getViewUsers().size())
                    .build();
        }
    }

    @Schema(description = "레시피 재료 응답 DTO")
    @Getter
    @Builder
    public static class RecipeIngredientResponse {
        @Schema(description = "레시피 재료 고유 번호")
        private Long recipeIngredientId;
        @Schema(description = "레시피 재료명")
        private String recipeIngredientName;
        @Schema(description = "레시피 재료 아이콘 url")
        private String recipeIngredientIconUrl;
        @Schema(description = "레시피 재료 용량")
        private String recipeIngredientCapacity;
        @Schema(description = "레시피 재료 냉장고 존재 여부")
        private Boolean isInUserFridge;

        public static RecipeIngredientResponse from(RecipeIngredient recipeIngredient, List<Fridge> fridges) {
            return RecipeIngredientResponse.builder()
                    .recipeIngredientId(recipeIngredient.getRecipeIngredientId())
                    .recipeIngredientName(recipeIngredient.getIngredient().getIngredientName())
                    .recipeIngredientIconUrl(recipeIngredient.getIngredient().getIngredientIconUrl())
                    .recipeIngredientCapacity(recipeIngredient.getCapacity())
                    .isInUserFridge(recipeIngredient.isInFridges(fridges))
                    .build();
        }
    }

    @Schema(description = "레시피 과정 응답 DTO")
    @Getter
    @Builder
    public static class RecipeProcessResponse {
        @Schema(description = "레시피 과정 고유 번호")
        private Long recipeProcessId;
        @Schema(description = "레시피 과정 순서")
        private Integer recipeProcessNo;
        @Schema(description = "레시피 과정 설명")
        private String recipeProcessDescription;
        @Schema(description = "레시피 과정 이미지 url")
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

}
