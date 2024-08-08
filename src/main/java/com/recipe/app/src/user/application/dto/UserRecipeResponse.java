package com.recipe.app.src.user.application.dto;

import com.recipe.app.src.recipe.domain.Recipe;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "나만의 레시피 응답 DTO")
@Getter
public class UserRecipeResponse {

    @Schema(description = "레시피 고유 번호")
    private final Long recipeId;
    @Schema(description = "썸네일 이미지")
    private final String thumbnailImgUrl;

    @Builder
    public UserRecipeResponse(Long recipeId, String thumbnailImgUrl) {

        this.recipeId = recipeId;
        this.thumbnailImgUrl = thumbnailImgUrl;
    }

    public static UserRecipeResponse from(Recipe recipe) {

        return UserRecipeResponse.builder()
                .recipeId(recipe.getRecipeId())
                .thumbnailImgUrl(recipe.getImgUrl())
                .build();
    }
}
