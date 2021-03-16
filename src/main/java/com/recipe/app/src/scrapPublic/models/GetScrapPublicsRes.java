package com.recipe.app.src.scrapPublic.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetScrapPublicsRes {
    private final Long scrapCount;
    private final List scrapList;
}

