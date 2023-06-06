package com.recipe.app.src.user.application.dto;

import com.recipe.app.src.user.domain.User;
import lombok.*;

public class UserDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserProfileRequest {

    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class UserProfileResponse {
        private Integer userIdx;
        private String jwt;

        public UserProfileResponse(User user, String jwt) {
            this(user.getUserIdx(), jwt);
        }
    }
}
