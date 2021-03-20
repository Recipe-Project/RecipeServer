package com.recipe.app.src.scrapBlog.models;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class GetScrapBlogsRes {
    private final Integer scrapBlogdx;
    private final String title;
    private final String blogUrl;
    private final String description;
    private final String bloggerName;
    private final String postDate;
    private final String thumbnail;
    private final Integer userScrapCnt;
}
