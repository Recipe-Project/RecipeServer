package com.recipe.app.src.recipe.domain;

import com.recipe.app.src.recipe.exception.NotFoundRecipeLevelException;

import java.util.Arrays;

public enum RecipeLevel {
    HARD("00", "어려움"),
    NORMAL("01", "보통"),
    EASY("02", "초보환영");

    private final String code;
    private final String name;

    RecipeLevel(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static RecipeLevel findRecipeLevelByCode(String code) {
        return Arrays.stream(values())
                .filter(recipeLevel -> recipeLevel.code.equals(code))
                .findFirst()
                .orElseThrow(() -> {
                    throw new NotFoundRecipeLevelException();
                });
    }

    public String getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

}
