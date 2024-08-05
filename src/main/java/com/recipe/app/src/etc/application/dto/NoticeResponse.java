package com.recipe.app.src.etc.application.dto;

import com.recipe.app.src.etc.domain.Notice;
import lombok.Builder;
import lombok.Getter;

@Getter
public class NoticeResponse {
    private final Long noticeId;
    private final String title;
    private final String content;

    @Builder
    public NoticeResponse(Long noticeId, String title, String content) {
        this.noticeId = noticeId;
        this.title = title;
        this.content = content;
    }

    public static NoticeResponse from(Notice notice) {

        return NoticeResponse.builder()
                .noticeId(notice.getNoticeId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .build();
    }
}
