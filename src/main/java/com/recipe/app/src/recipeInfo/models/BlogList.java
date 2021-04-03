package com.recipe.app.src.recipeInfo.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class BlogList {
    private final String title;
    private final String blogUrl;
    private final String description;
    private final String bloggerName;
    private final String postDate;
    private final String thumbnail;
    private final String userScrapYN;
    private final Integer userScrapCnt;
}
