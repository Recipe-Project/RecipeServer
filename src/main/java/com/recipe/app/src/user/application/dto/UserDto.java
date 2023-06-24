package com.recipe.app.src.user.application.dto;

import com.recipe.app.src.user.domain.User;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

public class UserDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserProfileRequest {
        private String profilePhoto;
        private String userName;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class UserProfileResponse {
        private int userIdx;
        private String socialId;
        private String profilePhoto;
        private String userName;
        private long youtubeScrapCnt;
        private long blogScrapCnt;
        private long recipeScrapCnt;
        private long myRecipeTotalSize;
        private List<UserRecipeResponse> userRecipes;

        public UserProfileResponse(User user) {
            this(user.getUserIdx(),
                    user.getSocialId(),
                    user.getProfilePhoto(),
                    user.getUserName(),
                    user.getScrapYoutubes().stream()
                            .filter((scrap) -> scrap.getStatus().equals("ACTIVE"))
                            .count(),
                    user.getScrapBlogs().stream()
                            .filter((scrap) -> scrap.getStatus().equals("ACTIVE"))
                            .count(),
                    user.getScrapPublics().stream()
                            .filter((scrap) -> scrap.getStatus().equals("ACTIVE"))
                            .count(),
                    user.getUserRecipes().stream()
                            .filter((scrap) -> scrap.getStatus().equals("ACTIVE"))
                            .count(),
                    user.getUserRecipes().stream()
                            .filter((scrap) -> scrap.getStatus().equals("ACTIVE"))
                            .map((r) -> new UserRecipeResponse(r.getUserRecipeIdx(), r.getThumbnail()))
                            .collect(Collectors.toList())
            );
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class UserRecipeResponse {
        private int userRecipeIdx;
        private String thumbnail;
    }
}
