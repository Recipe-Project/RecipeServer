package com.recipe.app.src.scrap.application.dto;

import com.recipe.app.src.scrap.domain.ScrapPublic;
import lombok.*;

import java.util.List;

public class ScrapPublicDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ScrapPublicRequest {
        private Integer recipeId;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ScrapPublicsResponse {
        private Long scrapCount;
        private List<ScrapPublicResponse> scrapList;
    }


    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ScrapPublicResponse {
        private Integer recipeId;
        private String title;
        private String content;
        private String thumbnail;
        private Long scrapCount;

        public ScrapPublicResponse(ScrapPublic scrapPublic, long scrapCount) {
            this(
                    scrapPublic.getRecipeInfo().getRecipeId(),
                    scrapPublic.getRecipeInfo().getRecipeNmKo().length() > 30 ? scrapPublic.getRecipeInfo().getRecipeNmKo().substring(0, 30) + "..." : scrapPublic.getRecipeInfo().getRecipeNmKo(),
                    scrapPublic.getRecipeInfo().getSumry().length() > 50 ? scrapPublic.getRecipeInfo().getSumry().substring(0, 50) + "..." : scrapPublic.getRecipeInfo().getSumry(),
                    scrapPublic.getRecipeInfo().getImgUrl(),
                    scrapCount
            );
        }
    }
}
