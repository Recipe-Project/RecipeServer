package com.recipe.app.src.recipe.infra.youtube;

import com.recipe.app.src.common.utils.SearchKeywordNormalizer.SearchQuery;
import com.recipe.app.src.recipe.domain.youtube.YoutubeRecipe;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface YoutubeRecipeCustomRepository {

    Long countByKeyword(SearchQuery query);

    List<YoutubeRecipe> findByKeywordLimit(String keyword, int size);

    List<YoutubeRecipe> findByKeywordLimitOrderByPostDateDesc(SearchQuery query, Long lastYoutubeRecipeId, LocalDate lastYoutubeRecipePostDate, int size);

    List<YoutubeRecipe> findByKeywordLimitOrderByYoutubeScrapCntDesc(SearchQuery query, Long lastYoutubeRecipeId, long youtubeScrapCnt, int size);

    List<YoutubeRecipe> findByKeywordLimitOrderByYoutubeViewCntDesc(SearchQuery query, Long lastYoutubeRecipeId, long youtubeViewCnt, int size);

    List<YoutubeRecipe> findUserScrapYoutubeRecipesLimit(Long userId, Long lastYoutubeRecipeId, LocalDateTime scrapCreatedAt, int size);
}
