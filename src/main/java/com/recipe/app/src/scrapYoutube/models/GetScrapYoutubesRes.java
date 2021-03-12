package com.recipe.app.src.scrapYoutube.models;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetScrapYoutubesRes {
    private final Integer youtubeIdx;
    private final String title;
    private final String thumbnail;
    private final String youtubeUrl;
}
