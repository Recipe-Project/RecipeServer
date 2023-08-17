package com.recipe.app.src.recipe.domain;

import com.recipe.app.src.user.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class YoutubeRecipe {

    private final Long youtubeRecipeId;
    private final String title;
    private final String description;
    private final String thumbnailImgUrl;
    private final LocalDate postDate;
    private final String channelName;
    private final String youtubeId;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final List<User> scrapUsers;
    private final List<User> viewUsers;

    @Builder
    public YoutubeRecipe(Long youtubeRecipeId, String title, String description,
                         String thumbnailImgUrl, LocalDate postDate, String channelName,
                         String youtubeId, LocalDateTime createdAt, LocalDateTime updatedAt,
                         List<User> scrapUsers, List<User> viewUsers) {
        this.youtubeRecipeId = youtubeRecipeId;
        this.title = title;
        this.description = description;
        this.thumbnailImgUrl = thumbnailImgUrl;
        this.postDate = postDate;
        this.channelName = channelName;
        this.youtubeId = youtubeId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.scrapUsers = scrapUsers;
        this.viewUsers = viewUsers;
    }

    public static YoutubeRecipe from(String title, String description, String thumbnailImgUrl,
                                     LocalDate postDate, String channelName, String youtubeId) {
        LocalDateTime now = LocalDateTime.now();
        return YoutubeRecipe.builder()
                .title(title)
                .description(description)
                .thumbnailImgUrl(thumbnailImgUrl)
                .postDate(postDate)
                .channelName(channelName)
                .youtubeId(youtubeId)
                .build();
    }

    public boolean isScrapByUser(User user) {
        return scrapUsers.contains(user);
    }
}
