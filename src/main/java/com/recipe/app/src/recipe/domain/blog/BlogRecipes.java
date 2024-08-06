package com.recipe.app.src.recipe.domain.blog;

import lombok.Getter;

import java.util.List;

@Getter
public class BlogRecipes {

    private final List<BlogRecipe> blogRecipes;

    public BlogRecipes(List<BlogRecipe> blogRecipes) {
        this.blogRecipes = blogRecipes;
    }
}
