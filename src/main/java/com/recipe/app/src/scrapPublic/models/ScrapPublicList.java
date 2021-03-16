package com.recipe.app.src.scrapPublic.models;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class ScrapPublicList {
    private final Integer recipeId;
    private final String title;
    private final String content;
    private final String thumbnail;
    private final Long scrapCount;
}
