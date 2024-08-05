package com.recipe.app.common.client.dto;

import com.recipe.app.src.recipe.domain.blog.BlogRecipe;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class NaverBlogSearchResponse {

    private int total;
    private String lastBuildDate;
    private int display;
    private int start;
    private List<NaverBlogSearchItemResponse> items;

    public List<BlogRecipe> toEntity() {

        return items.stream()
                .map(NaverBlogSearchItemResponse::toEntity)
                .collect(Collectors.toList());
    }
}
