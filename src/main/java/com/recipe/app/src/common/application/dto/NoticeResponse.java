package com.recipe.app.src.common.application.dto;

import com.recipe.app.src.common.domain.Notice;
import lombok.Builder;
import lombok.Getter;

@Getter
public class NoticeResponse {
    private final Long idx;
    private final String title;
    private final String content;

    @Builder
    public NoticeResponse(Long idx, String title, String content) {
        this.idx = idx;
        this.title = title;
        this.content = content;
    }

    public static NoticeResponse from(Notice notice) {

        return NoticeResponse.builder()
                .idx(notice.getIdx())
                .title(notice.getTitle())
                .content(notice.getContent())
                .build();
    }
}
