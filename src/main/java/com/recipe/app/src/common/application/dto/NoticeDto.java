package com.recipe.app.src.common.application.dto;

import com.recipe.app.src.common.domain.Notice;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NoticeDto {
    private int idx;
    private String title;
    private String content;

    public NoticeDto(Notice notice) {
        this.idx = notice.getIdx();
        this.title = notice.getTitle();
        this.content = notice.getContent();
    }
}
