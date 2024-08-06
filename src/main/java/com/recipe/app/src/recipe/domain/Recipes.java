package com.recipe.app.src.recipe.domain;

import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class Recipes {

    private final List<Recipe> recipes;

    public Recipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    public List<Long> getRecipeIds() {

        return recipes.stream()
                .map(Recipe::getRecipeId)
                .collect(Collectors.toList());
    }

    public List<Long> getUserIds() {

        return recipes.stream()
                .map(Recipe::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public int size() {
        return recipes.size();
    }
}
