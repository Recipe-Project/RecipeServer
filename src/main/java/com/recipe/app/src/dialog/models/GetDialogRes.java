package com.recipe.app.src.dialog.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetDialogRes {
    private final int idx;
    private final String title;
    private final String content;
    private final String link;
}
