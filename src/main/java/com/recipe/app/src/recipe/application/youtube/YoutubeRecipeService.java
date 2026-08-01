package com.recipe.app.src.recipe.application.youtube;

import com.recipe.app.src.common.utils.BadWordFiltering;
import com.recipe.app.src.recipe.application.keyword.SearchKeywordService;
import com.recipe.app.src.common.utils.SearchKeywordNormalizer;
import com.recipe.app.src.common.utils.SearchKeywordNormalizer.SearchQuery;
import com.recipe.app.src.recipe.application.dto.RecipesResponse;
import com.recipe.app.src.recipe.domain.youtube.YoutubeRecipe;
import com.recipe.app.src.recipe.domain.youtube.YoutubeRecipes;
import com.recipe.app.src.recipe.domain.youtube.YoutubeScrap;
import com.recipe.app.src.recipe.infra.youtube.YoutubeRecipeRepository;
import com.recipe.app.src.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class YoutubeRecipeService {

    private static final int MIN_RECIPE_CNT = 10;
    private final YoutubeRecipeRepository youtubeRecipeRepository;
    private final YoutubeScrapService youtubeScrapService;
    private final YoutubeViewService youtubeViewService;
    private final BadWordFiltering badWordFiltering;
    private final YoutubeRecipeClientSearchService youtubeRecipeClientSearchService;
    private final SearchKeywordService searchKeywordService;

    public YoutubeRecipeService(YoutubeRecipeRepository youtubeRecipeRepository, YoutubeScrapService youtubeScrapService, YoutubeViewService youtubeViewService,
                                BadWordFiltering badWordFiltering, YoutubeRecipeClientSearchService youtubeRecipeClientSearchService,
                                SearchKeywordService searchKeywordService) {
        this.youtubeRecipeRepository = youtubeRecipeRepository;
        this.youtubeScrapService = youtubeScrapService;
        this.youtubeViewService = youtubeViewService;
        this.badWordFiltering = badWordFiltering;
        this.youtubeRecipeClientSearchService = youtubeRecipeClientSearchService;
        this.searchKeywordService = searchKeywordService;
    }

    @Transactional
    public RecipesResponse findYoutubeRecipesByKeyword(User user, String keyword, long lastYoutubeRecipeId, int size, String sort) {

        badWordFiltering.check(keyword);

        SearchQuery query = SearchKeywordNormalizer.normalize(keyword);
        if (query instanceof SearchQuery.Empty) {
            return getRecipes(user, 0L, new YoutubeRecipes(List.of()));
        }

        long totalCnt = youtubeRecipeRepository.countByKeyword(query);

        if (totalCnt < MIN_RECIPE_CNT) {
            youtubeRecipeClientSearchService.searchYoutube(keyword);
        }

        List<YoutubeRecipe> youtubeRecipes = findByKeywordOrderBy(query, lastYoutubeRecipeId, size, sort);
        totalCnt = youtubeRecipeRepository.countByKeyword(query);

        // 검색 로그 적재 (비동기·fire-and-forget). 욕설/빈 검색어는 위에서 이미 걸러진 상태.
        searchKeywordService.record(keyword, user != null ? user.getUserId() : null);

        return getRecipes(user, totalCnt, new YoutubeRecipes(youtubeRecipes));
    }

    private List<YoutubeRecipe> findByKeywordOrderBy(SearchQuery query, long lastYoutubeRecipeId, int size, String sort) {

        if (sort.equals("scraps")) {
            return findByKeywordOrderByYoutubeScrapCnt(query, lastYoutubeRecipeId, size);
        } else if (sort.equals("views")) {
            return findByKeywordOrderByYoutubeViewCnt(query, lastYoutubeRecipeId, size);
        } else {
            return findByKeywordOrderByPostDate(query, lastYoutubeRecipeId, size);
        }
    }

    private List<YoutubeRecipe> findByKeywordOrderByYoutubeScrapCnt(SearchQuery query, long lastYoutubeRecipeId, int size) {

        Double lastRelevance = youtubeRecipeRepository.findRelevanceScoreByYoutubeRecipeId(query, lastYoutubeRecipeId);
        long youtubeScrapCnt = youtubeScrapService.countByYoutubeRecipeId(lastYoutubeRecipeId);

        return youtubeRecipeRepository.findByKeywordLimitOrderByYoutubeScrapCntDesc(query, lastYoutubeRecipeId, lastRelevance, youtubeScrapCnt, size);
    }

    private List<YoutubeRecipe> findByKeywordOrderByYoutubeViewCnt(SearchQuery query, long lastYoutubeRecipeId, int size) {

        Double lastRelevance = youtubeRecipeRepository.findRelevanceScoreByYoutubeRecipeId(query, lastYoutubeRecipeId);
        long youtubeViewCnt = youtubeViewService.countByYoutubeRecipeId(lastYoutubeRecipeId);

        return youtubeRecipeRepository.findByKeywordLimitOrderByYoutubeViewCntDesc(query, lastYoutubeRecipeId, lastRelevance, youtubeViewCnt, size);
    }

    private List<YoutubeRecipe> findByKeywordOrderByPostDate(SearchQuery query, long lastYoutubeRecipeId, int size) {

        Double lastRelevance = youtubeRecipeRepository.findRelevanceScoreByYoutubeRecipeId(query, lastYoutubeRecipeId);
        YoutubeRecipe youtubeRecipe = youtubeRecipeRepository.findById(lastYoutubeRecipeId).orElse(null);

        return youtubeRecipeRepository.findByKeywordLimitOrderByPostDateDesc(query, lastYoutubeRecipeId, lastRelevance, youtubeRecipe != null ? youtubeRecipe.getPostDate() : null, size);
    }

    @Transactional(readOnly = true)
    public RecipesResponse findScrapYoutubeRecipes(User user, long lastYoutubeRecipeId, int size) {

        long totalCnt = youtubeScrapService.countByUserId(user.getUserId());

        YoutubeScrap youtubeScrap = youtubeScrapService.findByUserIdAndYoutubeRecipeId(user.getUserId(), lastYoutubeRecipeId);

        List<YoutubeRecipe> youtubeRecipes = youtubeRecipeRepository.findUserScrapYoutubeRecipesLimit(user.getUserId(), lastYoutubeRecipeId, youtubeScrap != null ? youtubeScrap.getCreatedAt() : null, size);

        return getRecipes(user, totalCnt, new YoutubeRecipes(youtubeRecipes));
    }

    private RecipesResponse getRecipes(User user, long totalCnt, YoutubeRecipes youtubeRecipes) {

        List<YoutubeScrap> youtubeScraps = youtubeScrapService.findByYoutubeRecipeIds(youtubeRecipes.getYoutubeRecipeIds());

        return RecipesResponse.from(totalCnt, youtubeRecipes, youtubeScraps, user);
    }

    @Transactional
    public void createYoutubeView(User user, long youtubeRecipeId) {

        youtubeRecipeRepository.findById(youtubeRecipeId)
                .ifPresent((youtubeRecipe) -> {
                    youtubeRecipe.plusViewCnt();
                    youtubeViewService.create(user.getUserId(), youtubeRecipeId);
                });
    }

    @Transactional
    public void createYoutubeScrap(User user, long youtubeRecipeId) {

        youtubeRecipeRepository.findById(youtubeRecipeId)
                .ifPresent((youtubeRecipe) -> {
                    youtubeRecipe.plusScrapCnt();
                    youtubeScrapService.create(user.getUserId(), youtubeRecipeId);
                });
    }

    @Transactional
    public void deleteYoutubeScrap(User user, long youtubeRecipeId) {

        youtubeRecipeRepository.findById(youtubeRecipeId)
                .ifPresent((youtubeRecipe) -> {
                    youtubeRecipe.minusScrapCnt();
                    youtubeScrapService.delete(user.getUserId(), youtubeRecipeId);
                });
    }

}
