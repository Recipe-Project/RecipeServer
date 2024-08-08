package com.recipe.app.src.user.application.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@ApiModel(value = "수정할 회원 정보 요청 DTO", description = "프로필 사진, 닉네임 정보")
@Getter
@NoArgsConstructor
public class UserProfileRequest {

    @ApiModelProperty(value = "프로필 이미지")
    private String profileImgUrl;
    @ApiModelProperty(value = "닉네임")
    private String nickname;

    @Builder
    public UserProfileRequest(String profileImgUrl, String nickname) {
        this.profileImgUrl = profileImgUrl;
        this.nickname = nickname;
    }
}