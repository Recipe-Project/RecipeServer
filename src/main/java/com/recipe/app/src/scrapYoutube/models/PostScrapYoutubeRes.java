package com.recipe.app.src.scrapYoutube.models;


import lombok.AllArgsConstructor;
import lombok.Getter;



@Getter
@AllArgsConstructor
public class PostScrapYoutubeRes {
    private final Integer userIdx;
    private final String youtubeId;
    private final String title;
    private final String thumbnail;
    private final String youtubeUrl;
    private final String postDate;
    private final String channelName;
    private final String playTime;
}
