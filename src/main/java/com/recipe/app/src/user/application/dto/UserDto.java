package com.recipe.app.src.user.application.dto;

import com.recipe.app.src.recipe.domain.Recipe;
import com.recipe.app.src.user.domain.LoginProvider;
import com.recipe.app.src.user.domain.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserDto {

    @ApiModel(value = "수정할 회원 정보 요청 DTO", description = "프로필 사진, 닉네임 정보")
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserProfileRequest {
        @ApiModelProperty(value = "프로필 이미지")
        private String profileImgUrl;
        @ApiModelProperty(value = "닉네임")
        private String nickname;
    }

    @Schema(description = "회원 프로필 응답 DTO")
    @Getter
    @Builder
    public static class UserProfileResponse {
        @Schema(description = "회원 고유 번호")
        private Long userId;
        @Schema(description = "프로필 사진")
        private String profileImgUrl;
        @Schema(description = "닉네임")
        private String nickname;
        @Schema(description = "이메일")
        private String email;
        @Schema(description = "로그인 정보", enumAsRef = true)
        private LoginProvider loginProvider;
        @Schema(description = "유튜브 레시피 스크랩 수")
        private long youtubeScrapCnt;
        @Schema(description = "블로그 레시피 스크랩 수")
        private long blogScrapCnt;
        @Schema(description = "레시피 스크랩 수")
        private long recipeScrapCnt;
        @Schema(description = "나만의 레시피 전체 등록 수")
        private int userRecipeTotalSize;
        @Schema(description = "나만의 레시피 목록")
        private List<UserDto.UserRecipeResponse> userRecipes = new ArrayList<>();

        public static UserProfileResponse from(User user, List<Recipe> userRecipes, long youtubeScrapCnt, long blogScrapCnt, long recipeScrapCnt) {
            return UserProfileResponse.builder()
                    .userId(user.getUserId())
                    .profileImgUrl(user.getProfileImgUrl())
                    .nickname(user.getNickname())
                    .email(user.getEmail())
                    .loginProvider(LoginProvider.findLoginProvider(user.getSocialId()))
                    .youtubeScrapCnt(youtubeScrapCnt)
                    .blogScrapCnt(blogScrapCnt)
                    .recipeScrapCnt(recipeScrapCnt)
                    .userRecipeTotalSize(userRecipes.size())
                    .userRecipes(userRecipes.stream()
                            .map(r -> new UserRecipeResponse(r.getRecipeId(), r.getImgUrl()))
                            .collect(Collectors.toList()))
                    .build();
        }
    }

    @Schema(description = "나만의 레시피 응답 DTO")
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class UserRecipeResponse {
        @Schema(description = "레시피 고유 번호")
        private Long recipeId;
        @Schema(description = "썸네일 이미지")
        private String thumbnailImgUrl;
    }
}
