package com.recipe.app.src.recipe.domain;

import com.recipe.app.src.recipe.domain.RecipeLevel;

import java.time.LocalDateTime;

public interface RecipeWithRate {
    Long getRecipeId();

    String getRecipeNm();

    String getIntroduction();

    Long getCookingTime();

    RecipeLevel getLevel();

    String getImgUrl();

    Long getQuantity();

    Long getCalorie();

    String getHiddenYn();

    Long getUserId();

    Double getMatchRate();

    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();

    Long getScrapUserId();

    Long getViewUserId();
}
