package com.recipe.app.src.common.application.dto;

import com.recipe.app.src.common.domain.Dialog;
import lombok.Builder;
import lombok.Getter;

@Getter
public class DialogResponse {
    private final int idx;
    private final String title;
    private final String content;
    private final String link;

    @Builder
    public DialogResponse(int idx, String title, String content, String link) {
        this.idx = idx;
        this.title = title;
        this.content = content;
        this.link = link;
    }

    public static DialogResponse from(Dialog dialog) {
        return DialogResponse.builder()
                .idx(dialog.getIdx())
                .title(dialog.getTitle())
                .content(dialog.getContent())
                .link(dialog.getLink())
                .build();
    }
}
