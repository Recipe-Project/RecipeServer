package com.recipe.app.src.common.application.dto;

import com.recipe.app.src.common.domain.Dialog;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DialogDto {
    private int idx;
    private String title;
    private String content;
    private String link;

    public DialogDto(Dialog dialog) {
        this.idx = dialog.getIdx();
        this.title = dialog.getTitle();
        this.content = dialog.getContent();
        this.link = dialog.getLink();
    }
}
