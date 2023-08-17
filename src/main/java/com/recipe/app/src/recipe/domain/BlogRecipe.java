package com.recipe.app.src.recipe.domain;

import com.recipe.app.src.user.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class BlogRecipe {

    private final Long blogRecipeId;
    private final String blogUrl;
    private final String blogThumbanilImgUrl;
    private final String title;
    private final String description;
    private final LocalDate publishedAt;
    private final String blogName;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final List<User> scrapUsers;
    private final List<User> viewUsers;

    @Builder
    public BlogRecipe(Long blogRecipeId, String blogUrl, String blogThumbanilImgUrl,
                      String title, String description, LocalDate publishedAt, String blogName,
                      LocalDateTime createdAt, LocalDateTime updatedAt, List<User> scrapUsers, List<User> viewUsers) {
        this.blogRecipeId = blogRecipeId;
        this.blogUrl = blogUrl;
        this.blogThumbanilImgUrl = blogThumbanilImgUrl;
        this.title = title;
        this.description = description;
        this.publishedAt = publishedAt;
        this.blogName = blogName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.scrapUsers = scrapUsers;
        this.viewUsers = viewUsers;
    }

    public static BlogRecipe from(String blogUrl, String blogThumbanilImgUrl, String title, String description,
                                  LocalDate publishedAt, String blogName) {
        LocalDateTime now = LocalDateTime.now();
        return BlogRecipe.builder()
                .blogUrl(blogUrl)
                .blogThumbanilImgUrl(blogThumbanilImgUrl)
                .title(title)
                .description(description)
                .publishedAt(publishedAt)
                .blogName(blogName)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public boolean isScrapByUser(User user) {
        return scrapUsers.contains(user);
    }
}
