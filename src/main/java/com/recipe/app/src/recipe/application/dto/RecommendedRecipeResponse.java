package com.recipe.app.src.recipe.application.dto;

import com.recipe.app.src.recipe.domain.Recipe;
import com.recipe.app.src.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Schema(description = "냉장고 추천 레시피 응답 DTO")
@Getter
public class RecommendedRecipeResponse {

    @Schema(description = "레시피 고유 번호")
    private final Long recipeId;
    @Schema(description = "레시피명")
    private final String recipeName;
    @Schema(description = "소개글")
    private final String introduction;
    @Schema(description = "썸네일 이미지 Url")
    private final String thumbnailImgUrl;
    @Schema(description = "게시자")
    private final String postUserName;
    @Schema(description = "게시일시")
    private final String postDate;
    @Schema(description = "연결 링크 Url")
    private final String linkUrl;
    @Schema(description = "스크랩 여부")
    private final Boolean isUserScrap;
    @Schema(description = "스크랩 갯수")
    private final long scrapCnt;
    @Schema(description = "조회수")
    private final long viewCnt;
    @Schema(description = "재료 일치도")
    private final int ingredientsMatchRate;

    @Builder
    public RecommendedRecipeResponse(Long recipeId, String recipeName, String introduction, String thumbnailImgUrl, String postUserName, String postDate,
                                     String linkUrl, Boolean isUserScrap, long scrapCnt, long viewCnt, Integer ingredientsMatchRate) {

        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.introduction = introduction;
        this.thumbnailImgUrl = thumbnailImgUrl;
        this.postUserName = postUserName;
        this.postDate = postDate;
        this.linkUrl = linkUrl;
        this.isUserScrap = isUserScrap;
        this.scrapCnt = scrapCnt;
        this.viewCnt = viewCnt;
        this.ingredientsMatchRate = ingredientsMatchRate;
    }

    public static RecommendedRecipeResponse from(Recipe recipe, User recipePostUser, int ingredientsMatchRate, boolean isScrapByUser, long scrapCnt, long viewCnt) {

        return RecommendedRecipeResponse.builder()
                .recipeId(recipe.getRecipeId())
                .recipeName(recipe.getRecipeNm())
                .introduction(recipe.getIntroduction())
                .thumbnailImgUrl(recipe.getImgUrl())
                .postUserName(recipePostUser != null ? recipePostUser.getNickname() : null)
                .postDate(recipe.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.M.d")))
                .isUserScrap(isScrapByUser)
                .scrapCnt(scrapCnt)
                .viewCnt(viewCnt)
                .ingredientsMatchRate(ingredientsMatchRate)
                .build();
    }

}
