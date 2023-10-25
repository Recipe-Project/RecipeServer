package com.recipe.app.src.recipe.domain;

import com.recipe.app.src.user.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

@Getter
public class Recipe {

    private final Long recipeId;
    private final String recipeNm;
    private final String introduction;
    private final Long cookingTime;
    private final RecipeLevel level;
    private final String imgUrl;
    private final Long quantity;
    private final Long calorie;
    private final User user;
    private final boolean isHidden;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final List<User> scrapUsers;
    private final List<User> viewUsers;

    @Builder
    public Recipe(Long recipeId, String recipeNm, String introduction, Long cookingTime, RecipeLevel level, String imgUrl,
                  Long quantity, Long calorie, User user, boolean isHidden, LocalDateTime createdAt, LocalDateTime updatedAt,
                  List<User> scrapUsers, List<User> viewUsers) {
        this.recipeId = recipeId;
        this.recipeNm = recipeNm;
        this.introduction = introduction;
        this.cookingTime = cookingTime;
        this.level = level;
        this.imgUrl = imgUrl;
        this.quantity = quantity;
        this.calorie = calorie;
        this.user = user;
        this.isHidden = isHidden;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.scrapUsers = scrapUsers;
        this.viewUsers = viewUsers;
    }

    public static Recipe from(String recipeNm, String introduction, Long cookingTime, String levelCd,
                              String imgUrl, Long quantity, Long calorie, User user, boolean isHidden) {
        LocalDateTime now = LocalDateTime.now();
        return Recipe.builder()
                .recipeNm(recipeNm)
                .introduction(introduction)
                .cookingTime(cookingTime)
                .level(RecipeLevel.findRecipeLevelByCode(levelCd))
                .imgUrl(imgUrl)
                .quantity(quantity)
                .calorie(calorie)
                .user(user)
                .isHidden(isHidden)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public boolean isScrapByUser(User user) {
        return scrapUsers.contains(user);
    }

    public Recipe update(Long recipeId, String recipeNm, String introduction, Long cookingTime, String levelCd, String imgUrl,
                         Long quantity, Long calorie, boolean isHidden) {
        LocalDateTime now = LocalDateTime.now();
        return Recipe.builder()
                .recipeId(recipeId)
                .recipeNm(recipeNm)
                .introduction(introduction)
                .cookingTime(cookingTime)
                .level(RecipeLevel.findRecipeLevelByCode(levelCd))
                .imgUrl(imgUrl)
                .quantity(quantity)
                .calorie(calorie)
                .user(user)
                .isHidden(isHidden)
                .createdAt(createdAt)
                .updatedAt(now)
                .build();
    }

    public void addScrapUser(Long userId) {
        if (userId == null)
            return;
        this.scrapUsers.add(User.builder()
                .userId(userId)
                .build());
    }

    public void addViewUser(Long userId) {
        if (userId == null)
            return;
        this.viewUsers.add(User.builder()
                .userId(userId)
                .build());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recipe recipe = (Recipe) o;
        return getRecipeId().equals(recipe.getRecipeId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRecipeId());
    }
}
