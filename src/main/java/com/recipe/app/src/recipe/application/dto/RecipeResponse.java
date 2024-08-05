package com.recipe.app.src.recipe.application.dto;

import com.recipe.app.src.recipe.domain.Recipe;
import com.recipe.app.src.recipe.domain.RecipeScrap;
import com.recipe.app.src.recipe.domain.blog.BlogRecipe;
import com.recipe.app.src.recipe.domain.blog.BlogScrap;
import com.recipe.app.src.recipe.domain.youtube.YoutubeRecipe;
import com.recipe.app.src.recipe.domain.youtube.YoutubeScrap;
import com.recipe.app.src.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Schema(description = "레시피 응답 DTO")
@Getter
public class RecipeResponse {

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

    @Builder
    public RecipeResponse(Long recipeId, String recipeName, String introduction, String thumbnailImgUrl, String postUserName, String postDate,
                          String linkUrl, Boolean isUserScrap, long scrapCnt, long viewCnt) {

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
    }

    public static RecipeResponse from(Recipe recipe, User recipePostUser, List<RecipeScrap> recipeScraps, User user) {

        return RecipeResponse.builder()
                .recipeId(recipe.getRecipeId())
                .recipeName(recipe.getRecipeNm())
                .introduction(recipe.getIntroduction())
                .thumbnailImgUrl(recipe.getImgUrl())
                .postUserName(recipePostUser != null ? recipePostUser.getNickname() : null)
                .postDate(recipe.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.M.d")))
                .isUserScrap(recipeScraps.stream()
                        .anyMatch(recipeScrap ->
                                recipeScrap.getRecipeId().equals(recipe.getRecipeId())
                                        && recipeScrap.getUserId().equals(user.getUserId())))
                .scrapCnt(recipe.getScrapCnt())
                .viewCnt(recipe.getViewCnt())
                .build();
    }

    public static RecipeResponse from(BlogRecipe recipe, List<BlogScrap> blogScraps, User user) {
        return RecipeResponse.builder()
                .recipeId(recipe.getBlogRecipeId())
                .recipeName(recipe.getTitle())
                .introduction(recipe.getDescription())
                .thumbnailImgUrl(recipe.getBlogThumbnailImgUrl())
                .postUserName(recipe.getBlogName())
                .postDate(recipe.getPublishedAt().format(DateTimeFormatter.ofPattern("yyyy.M.d")))
                .linkUrl(recipe.getBlogUrl())
                .isUserScrap(blogScraps.stream()
                        .anyMatch(blogScrap ->
                                blogScrap.getBlogRecipeId().equals(recipe.getBlogRecipeId())
                                        && blogScrap.getUserId().equals(user.getUserId())))
                .scrapCnt(recipe.getScrapCnt())
                .viewCnt(recipe.getViewCnt())
                .build();
    }

    public static RecipeResponse from(YoutubeRecipe recipe, List<YoutubeScrap> youtubeScraps, User user) {
        return RecipeResponse.builder()
                .recipeId(recipe.getYoutubeRecipeId())
                .recipeName(recipe.getTitle())
                .introduction(recipe.getDescription())
                .thumbnailImgUrl(recipe.getThumbnailImgUrl())
                .postUserName(recipe.getChannelName())
                .postDate(recipe.getPostDate().format(DateTimeFormatter.ofPattern("yyyy.M.d")))
                .linkUrl("https://www.youtube.com/watch?v=" + recipe.getYoutubeId())
                .isUserScrap(youtubeScraps.stream()
                        .anyMatch(youtubeScrap ->
                                youtubeScrap.getYoutubeRecipeId().equals(recipe.getYoutubeRecipeId())
                                        && youtubeScrap.getUserId().equals(user.getUserId())))
                .scrapCnt(recipe.getScrapCnt())
                .viewCnt(recipe.getViewCnt())
                .build();
    }
}