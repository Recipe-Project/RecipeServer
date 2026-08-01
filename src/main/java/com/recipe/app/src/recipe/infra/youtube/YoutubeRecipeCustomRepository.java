package com.recipe.app.src.recipe.infra.youtube;

import com.recipe.app.src.common.utils.SearchKeywordNormalizer.SearchQuery;
import com.recipe.app.src.recipe.domain.youtube.YoutubeRecipe;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface YoutubeRecipeCustomRepository {

    Long countByKeyword(SearchQuery query);

    List<YoutubeRecipe> findByKeywordLimit(String keyword, int size);

    // 아래 정렬들은 모두 "검색어 일치율(relevance) 대분류 → 각 정렬키 소분류 → youtubeRecipeId" 순.
    Double findRelevanceScoreByYoutubeRecipeId(SearchQuery query, Long youtubeRecipeId);

    List<YoutubeRecipe> findByKeywordLimitOrderByPostDateDesc(SearchQuery query, Long lastYoutubeRecipeId, Double lastRelevance, LocalDate lastYoutubeRecipePostDate, int size);

    List<YoutubeRecipe> findByKeywordLimitOrderByYoutubeScrapCntDesc(SearchQuery query, Long lastYoutubeRecipeId, Double lastRelevance, long youtubeScrapCnt, int size);

    List<YoutubeRecipe> findByKeywordLimitOrderByYoutubeViewCntDesc(SearchQuery query, Long lastYoutubeRecipeId, Double lastRelevance, long youtubeViewCnt, int size);

    List<YoutubeRecipe> findUserScrapYoutubeRecipesLimit(Long userId, Long lastYoutubeRecipeId, LocalDateTime scrapCreatedAt, int size);
}
