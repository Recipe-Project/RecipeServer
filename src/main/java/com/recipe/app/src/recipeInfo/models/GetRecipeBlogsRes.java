package com.recipe.app.src.recipeInfo.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetRecipeBlogsRes {
    private final Integer total;
    private final List<BlogList> blogList;
}
