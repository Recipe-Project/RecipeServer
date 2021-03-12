package com.recipe.app.src.scrapYoutube.models;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;


@Getter
@AllArgsConstructor
public class PostScrapYoutubeRes {
    private final Integer userIdx;
    private final Integer youtubeIdx;
    private final String title;
    private final String thumbnail;
    private final String youtubeUrl;
}
