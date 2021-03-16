package com.recipe.app.src.scrapPublic.models;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
public class PostScrapPublicReq {
    private Integer recipeId;
}

