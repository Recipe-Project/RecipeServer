package com.recipe.app.src.scrap.application.dto;

import com.recipe.app.src.scrap.domain.ScrapYoutube;
import lombok.*;

import java.util.List;

public class ScrapYoutubeDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ScrapYoutubeRequest {
        private String youtubeId;
        private String title;
        private String thumbnail;
        private String youtubeUrl;
        private String postDate;
        private String channelName;
        private String playTime;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ScrapYoutubesResponse {
        private Long scrapYoutubeCount;
        private List<ScrapYoutubeResponse> scrapYoutubeList;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ScrapYoutubeResponse {
        private Long userIdx;
        private String youtubeId;
        private String title;
        private String thumbnail;
        private Long heartCount;
        private String youtubeUrl;
        private String postDate;
        private String channelName;
        private String playTime;

        public ScrapYoutubeResponse(ScrapYoutube scrapYoutube, long heartCount) {
            this(
                    scrapYoutube.getUser().getUserId(),
                    scrapYoutube.getYoutubeId(),
                    scrapYoutube.getTitle().length() > 30 ? scrapYoutube.getTitle().substring(0, 30) + "..." : scrapYoutube.getTitle(),
                    scrapYoutube.getThumbnail(),
                    heartCount,
                    scrapYoutube.getYoutubeUrl(),
                    scrapYoutube.getPostDate(),
                    scrapYoutube.getChannelName(),
                    scrapYoutube.getPlayTime()
            );
        }
    }
}
