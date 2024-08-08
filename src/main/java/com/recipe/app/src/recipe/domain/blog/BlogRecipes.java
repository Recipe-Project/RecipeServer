package com.recipe.app.src.recipe.domain.blog;

import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class BlogRecipes {

    private final List<BlogRecipe> blogRecipes;

    public BlogRecipes(List<BlogRecipe> blogRecipes) {
        this.blogRecipes = blogRecipes;
    }

    public List<Long> getBlogRecipeIds() {

        return blogRecipes.stream()
                .map(BlogRecipe::getBlogRecipeId)
                .collect(Collectors.toList());
    }
}
