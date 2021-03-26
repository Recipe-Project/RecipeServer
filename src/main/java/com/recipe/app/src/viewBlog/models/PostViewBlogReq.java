package com.recipe.app.src.viewBlog.models;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
public class PostViewBlogReq {
    private String blogUrl;
}
