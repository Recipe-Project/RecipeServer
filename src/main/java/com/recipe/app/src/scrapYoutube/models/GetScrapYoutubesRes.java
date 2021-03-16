package com.recipe.app.src.scrapYoutube.models;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;
import java.util.List;


@Getter
@AllArgsConstructor
public class GetScrapYoutubesRes {
    private final Long scrapYoutubeCount;
    private final List scrapYoutubeList;
}
