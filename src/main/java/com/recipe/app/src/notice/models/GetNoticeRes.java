package com.recipe.app.src.notice.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetNoticeRes {
    private final String title;
    private final String content;
}
