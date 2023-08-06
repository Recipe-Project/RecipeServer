package com.recipe.app.src.user.application.dto;

import com.recipe.app.src.user.domain.LoginProvider;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.user.infra.UserEntity;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

public class UserDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserProfileRequest {
        private String profileImgUrl;
        private String nickname;
    }

    @Getter
    @Builder
    public static class UserProfileResponse {
        private Long userId;
        private String profileImgUrl;
        private String nickname;
        private String email;
        private LoginProvider loginProvider;
        private long youtubeScrapCnt;
        private long blogScrapCnt;
        private long recipeScrapCnt;
        private long myRecipeTotalSize;
        private List<UserRecipeResponse> userRecipes;

        public static UserProfileResponse from(User user) {
            return UserProfileResponse.builder()
                    .userId(user.getUserId())
                    .profileImgUrl(user.getProfileImgUrl())
                    .nickname(user.getNickname())
                    .email(user.getEmail())
                    .loginProvider(LoginProvider.findLoginProvider(user.getSocialId()))
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class UserRecipeResponse {
        private Long recipeId;
        private String thumbnailImgUrl;
    }
}
