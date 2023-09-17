package com.recipe.app.src.recipe.infra;

import java.time.LocalDateTime;

public interface RecipeEntityWithRate {
    Long getRecipeId();
    String getRecipeNm();
    String getIntroduction();
    Long getCookingTime();
    String getLevelNm();
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
