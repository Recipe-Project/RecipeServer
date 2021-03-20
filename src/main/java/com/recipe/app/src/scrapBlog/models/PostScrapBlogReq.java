package com.recipe.app.src.scrapBlog.models;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class PostScrapBlogReq {
    private String title;
    private String thumbnail;
    private String blogUrl;
    private String description;
    private String bloggerName;
    private String postDate;
}
