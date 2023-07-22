package com.recipe.app.src.keyword.application.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class RecipeKeywordDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class RecipesBestKeywordResponse {
        private String bestKeyword;
    }
}
