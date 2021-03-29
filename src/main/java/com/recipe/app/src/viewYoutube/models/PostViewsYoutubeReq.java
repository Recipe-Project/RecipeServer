package com.recipe.app.src.viewYoutube.models;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
public class PostViewsYoutubeReq {
    private Integer youtubeIdx;
}
