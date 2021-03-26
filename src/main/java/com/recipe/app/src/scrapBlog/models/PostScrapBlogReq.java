package com.recipe.app.src.scrapBlog.models;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
public class PostScrapBlogReq {
    private String title;
    private String thumbnail;
    private String blogUrl;
    private String description;
    private String bloggerName;
    private String postDate;
}
