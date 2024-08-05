package com.recipe.app.src.common.client.dto;

import com.recipe.app.src.recipe.domain.blog.BlogRecipe;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
public class NaverBlogSearchItemResponse {

    private String title;
    private String link;
    private String description;
    private String bloggername;
    private String postdate;

    public BlogRecipe toEntity() {

        return BlogRecipe.builder()
                .title(title.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", ""))
                .blogUrl(link)
                .description(description.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", ""))
                .blogName(bloggername)
                .publishedAt(LocalDate.parse(postdate, DateTimeFormatter.ofPattern("yyyyMMdd")))
                .build();
    }
}
