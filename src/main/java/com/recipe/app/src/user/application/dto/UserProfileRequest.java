package com.recipe.app.src.user.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "수정할 회원 정보 요청 DTO")
@Getter
@NoArgsConstructor
public class UserProfileRequest {

    @Schema(description = "프로필 이미지")
    private String profileImgUrl;
    @Schema(description = "닉네임")
    private String nickname;

    @Builder
    public UserProfileRequest(String profileImgUrl, String nickname) {
        this.profileImgUrl = profileImgUrl;
        this.nickname = nickname;
    }
}