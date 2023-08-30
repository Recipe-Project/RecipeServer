package com.recipe.app.src.recipe.domain;

import com.recipe.app.src.user.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class Recipe {

    private final Long recipeId;
    private final String recipeNm;
    private final String introduction;
    private final Long cookingTime;
    private final String levelNm;
    private final String imgUrl;
    private final Long quantity;
    private final Long calorie;
    private final User user;
    private final boolean isHidden;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final List<RecipeIngredient> recipeIngredients;
    private final List<RecipeProcess> recipeProcesses;
    private final List<User> scrapUsers;
    private final List<User> viewUsers;

    @Builder
    public Recipe(Long recipeId, String recipeNm, String introduction, Long cookingTime, String levelNm, String imgUrl,
                  Long quantity, Long calorie, User user, boolean isHidden, LocalDateTime createdAt, LocalDateTime updatedAt,
                  List<RecipeIngredient> recipeIngredients, List<RecipeProcess> recipeProcesses, List<User> scrapUsers, List<User> viewUsers) {
        this.recipeId = recipeId;
        this.recipeNm = recipeNm;
        this.introduction = introduction;
        this.cookingTime = cookingTime;
        this.levelNm = levelNm;
        this.imgUrl = imgUrl;
        this.quantity = quantity;
        this.calorie = calorie;
        this.user = user;
        this.isHidden = isHidden;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.recipeIngredients = recipeIngredients;
        this.recipeProcesses = recipeProcesses;
        this.scrapUsers = scrapUsers;
        this.viewUsers = viewUsers;
    }

    public boolean isScrapByUser(User user) {
        return scrapUsers.contains(user);
    }
}