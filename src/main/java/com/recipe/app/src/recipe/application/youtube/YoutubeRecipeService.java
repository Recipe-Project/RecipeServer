package com.recipe.app.src.recipe.application.youtube;

import com.recipe.app.src.etc.application.BadWordService;
import com.recipe.app.src.recipe.application.dto.RecipesResponse;
import com.recipe.app.src.recipe.domain.youtube.YoutubeRecipe;
import com.recipe.app.src.recipe.domain.youtube.YoutubeRecipes;
import com.recipe.app.src.recipe.domain.youtube.YoutubeScrap;
import com.recipe.app.src.recipe.infra.youtube.YoutubeRecipeRepository;
import com.recipe.app.src.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
public class YoutubeRecipeService {

    private static final int MIN_RECIPE_CNT = 10;
    private final YoutubeRecipeRepository youtubeRecipeRepository;
    private final YoutubeScrapService youtubeScrapService;
    private final YoutubeViewService youtubeViewService;
    private final BadWordService badWordService;
    private final YoutubeRecipeClientSearchService youtubeRecipeClientSearchService;

    public YoutubeRecipeService(YoutubeRecipeRepository youtubeRecipeRepository, YoutubeScrapService youtubeScrapService, YoutubeViewService youtubeViewService,
                                BadWordService badWordService, YoutubeRecipeClientSearchService youtubeRecipeClientSearchService) {
        this.youtubeRecipeRepository = youtubeRecipeRepository;
        this.youtubeScrapService = youtubeScrapService;
        this.youtubeViewService = youtubeViewService;
        this.badWordService = badWordService;
        this.youtubeRecipeClientSearchService = youtubeRecipeClientSearchService;
    }

    public RecipesResponse findYoutubeRecipesByKeyword(User user, String keyword, long lastYoutubeRecipeId, int size, String sort) throws IOException {

        badWordService.checkBadWords(keyword);

        long totalCnt = youtubeRecipeRepository.countByKeyword(keyword);

        List<YoutubeRecipe> youtubeRecipes;
        if (totalCnt < MIN_RECIPE_CNT) {

            youtubeRecipes = youtubeRecipeClientSearchService.searchYoutube(keyword, size);

            totalCnt = youtubeRecipeRepository.countByKeyword(keyword);
        } else {
            youtubeRecipes = findByKeywordOrderBy(keyword, lastYoutubeRecipeId, size, sort);
        }

        return getRecipes(user, totalCnt, new YoutubeRecipes(youtubeRecipes));
    }

    @Transactional(readOnly = true)
    public List<YoutubeRecipe> findByKeywordOrderBy(String keyword, long lastYoutubeRecipeId, int size, String sort) {

        if (sort.equals("youtubeScraps")) {
            return findByKeywordOrderByYoutubeScrapCnt(keyword, lastYoutubeRecipeId, size);
        } else if (sort.equals("youtubeViews")) {
            return findByKeywordOrderByYoutubeViewCnt(keyword, lastYoutubeRecipeId, size);
        } else {
            return findByKeywordOrderByPostDate(keyword, lastYoutubeRecipeId, size);
        }
    }

    private List<YoutubeRecipe> findByKeywordOrderByYoutubeScrapCnt(String keyword, long lastYoutubeRecipeId, int size) {

        long youtubeScrapCnt = youtubeScrapService.countByYoutubeRecipeId(lastYoutubeRecipeId);

        return youtubeRecipeRepository.findByKeywordLimitOrderByYoutubeScrapCntDesc(keyword, lastYoutubeRecipeId, youtubeScrapCnt, size);
    }

    private List<YoutubeRecipe> findByKeywordOrderByYoutubeViewCnt(String keyword, long lastYoutubeRecipeId, int size) {

        long youtubeViewCnt = youtubeViewService.countByYoutubeRecipeId(lastYoutubeRecipeId);

        return youtubeRecipeRepository.findByKeywordLimitOrderByYoutubeViewCntDesc(keyword, lastYoutubeRecipeId, youtubeViewCnt, size);
    }

    private List<YoutubeRecipe> findByKeywordOrderByPostDate(String keyword, long lastYoutubeRecipeId, int size) {

        YoutubeRecipe youtubeRecipe = youtubeRecipeRepository.findById(lastYoutubeRecipeId).orElse(null);

        return youtubeRecipeRepository.findByKeywordLimitOrderByPostDateDesc(keyword, lastYoutubeRecipeId, youtubeRecipe != null ? youtubeRecipe.getPostDate() : null, size);
    }

    @Transactional(readOnly = true)
    public RecipesResponse getScrapYoutubeRecipes(User user, long lastYoutubeRecipeId, int size) {

        long totalCnt = youtubeScrapService.countYoutubeScrapByUserId(user.getUserId());

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
                    youtubeViewService.createYoutubeView(user.getUserId(), youtubeRecipeId);
                });
    }

    @Transactional
    public void createYoutubeScrap(User user, long youtubeRecipeId) {

        youtubeRecipeRepository.findById(youtubeRecipeId)
                .ifPresent((youtubeRecipe) -> {
                    youtubeRecipe.plusScrapCnt();
                    youtubeScrapService.createYoutubeScrap(user.getUserId(), youtubeRecipeId);
                });
    }

    @Transactional
    public void deleteYoutubeScrap(User user, long youtubeRecipeId) {

        youtubeRecipeRepository.findById(youtubeRecipeId)
                .ifPresent((youtubeRecipe) -> {
                    youtubeRecipe.minusScrapCnt();
                    youtubeScrapService.deleteYoutubeScrap(user.getUserId(), youtubeRecipeId);
                });
    }

}
