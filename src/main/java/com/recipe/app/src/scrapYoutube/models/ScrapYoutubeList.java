package com.recipe.app.src.scrapYoutube.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScrapYoutubeList {
    private final Integer userIdx;
    private final String youtubeId;
    private final String title;
    private final String thumbnail;
    private final Long heartCount;
    private final String youtubeUrl;
    private final String postDate;
    private final String channelName;
    private final String playTime;
}
