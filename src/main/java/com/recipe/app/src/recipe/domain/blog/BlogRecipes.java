package com.recipe.app.src.recipe.domain.blog;

import java.util.List;

public class BlogRecipes {

    private List<BlogRecipe> blogRecipes;

    public BlogRecipes(List<BlogRecipe> blogRecipes) {
        this.blogRecipes = blogRecipes;
    }

    public List<BlogRecipe> getBlogRecipes() {
        return this.blogRecipes;
    }
}
