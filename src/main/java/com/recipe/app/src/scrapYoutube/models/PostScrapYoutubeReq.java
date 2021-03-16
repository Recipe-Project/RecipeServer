package com.recipe.app.src.scrapYoutube.models;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;



@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
public class PostScrapYoutubeReq {
    private Integer youtubeIdx;
    private String title;
    private String thumbnail;
    private String youtubeUrl;
    private String postDate;
    private String channelName;
}
