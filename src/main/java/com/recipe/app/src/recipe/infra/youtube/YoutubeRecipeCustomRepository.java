package com.recipe.app.src.recipe.infra.youtube;

import com.recipe.app.src.recipe.domain.youtube.YoutubeRecipe;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface YoutubeRecipeCustomRepository {

    Long countByKeyword(String keyword);

    List<YoutubeRecipe> findByKeywordLimitOrderByPostDateDesc(String keyword, Long lastYoutubeRecipeId, LocalDate lastYoutubeRecipePostDate, int size);

    List<YoutubeRecipe> findByKeywordLimitOrderByYoutubeScrapCntDesc(String keyword, Long lastYoutubeRecipeId, long youtubeScrapCnt, int size);

    List<YoutubeRecipe> findByKeywordLimitOrderByYoutubeViewCntDesc(String keyword, Long lastYoutubeRecipeId, long youtubeViewCnt, int size);

    List<YoutubeRecipe> findUserScrapYoutubeRecipesLimit(Long userId, Long lastYoutubeRecipeId, LocalDateTime scrapCreatedAt, int size);
}
