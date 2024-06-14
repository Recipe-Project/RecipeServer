package com.recipe.app.src.user.application.dto;

import com.recipe.app.src.user.domain.LoginProvider;
import com.recipe.app.src.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Schema(description = "회원 프로필 응답 DTO")
@Getter
public class UserProfileResponse {

    @Schema(description = "회원 고유 번호")
    private final Long userId;
    @Schema(description = "프로필 사진")
    private final String profileImgUrl;
    @Schema(description = "닉네임")
    private final String nickname;
    @Schema(description = "이메일")
    private final String email;
    @Schema(description = "로그인 정보", enumAsRef = true)
    private final LoginProvider loginProvider;
    @Schema(description = "유튜브 레시피 스크랩 수")
    private final long youtubeScrapCnt;
    @Schema(description = "블로그 레시피 스크랩 수")
    private final long blogScrapCnt;
    @Schema(description = "레시피 스크랩 수")
    private final long recipeScrapCnt;
    @Schema(description = "나만의 레시피 전체 등록 수")
    private final int userRecipeTotalSize;
    @Schema(description = "나만의 레시피 목록")
    private final List<UserRecipeResponse> userRecipes;

    @Builder
    public UserProfileResponse(Long userId, String profileImgUrl, String nickname, String email, LoginProvider loginProvider, long youtubeScrapCnt, long blogScrapCnt, long recipeScrapCnt, int userRecipeTotalSize, List<UserRecipeResponse> userRecipes) {

        this.userId = userId;
        this.profileImgUrl = profileImgUrl;
        this.nickname = nickname;
        this.email = email;
        this.loginProvider = loginProvider;
        this.youtubeScrapCnt = youtubeScrapCnt;
        this.blogScrapCnt = blogScrapCnt;
        this.recipeScrapCnt = recipeScrapCnt;
        this.userRecipeTotalSize = userRecipeTotalSize;
        this.userRecipes = userRecipes;
    }


    public static UserProfileResponse from(User user, List<UserRecipeResponse> userRecipes, long youtubeScrapCnt, long blogScrapCnt, long recipeScrapCnt) {

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
                .userRecipes(userRecipes)
                .build();
    }
}