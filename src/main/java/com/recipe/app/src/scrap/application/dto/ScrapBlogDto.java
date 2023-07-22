package com.recipe.app.src.scrap.application.dto;

import com.recipe.app.src.scrap.domain.ScrapBlog;
import lombok.*;

import java.time.format.DateTimeFormatter;

public class ScrapBlogDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ScrapBlogRequest {
        private String title;
        private String thumbnail;
        private String blogUrl;
        private String description;
        private String bloggerName;
        private String postDate;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ScrapBlogResponse {
        private Integer scrapBlogIdx;
        private String title;
        private String blogUrl;
        private String description;
        private String bloggerName;
        private String postDate;
        private String thumbnail;
        private Integer userScrapCnt;

        public ScrapBlogResponse(ScrapBlog scrapBlog, int userScrapCnt) {
            this(
                    scrapBlog.getIdx(),
                    scrapBlog.getTitle(),
                    scrapBlog.getBlogUrl(),
                    scrapBlog.getDescription(),
                    scrapBlog.getBloggerName(),
                    scrapBlog.getPostDate().format(DateTimeFormatter.ofPattern("yyyy.M.d")),
                    scrapBlog.getThumbnail(),
                    userScrapCnt
            );
        }
    }

}
