package com.recipe.app.src.recipe.application.youtube;

import com.recipe.app.src.common.application.BadWordService;
import com.recipe.app.src.recipe.application.dto.RecipesResponse;
import com.recipe.app.src.recipe.domain.youtube.YoutubeRecipe;
import com.recipe.app.src.recipe.domain.youtube.YoutubeRecipes;
import com.recipe.app.src.recipe.domain.youtube.YoutubeScrap;
import com.recipe.app.src.recipe.exception.NotFoundRecipeException;
import com.recipe.app.src.recipe.infra.youtube.YoutubeRecipeRepository;
import com.recipe.app.src.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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

    public RecipesResponse getYoutubeRecipes(User user, String keyword, Long lastYoutubeRecipeId, int size, String sort) throws IOException {

        badWordService.checkBadWords(keyword);

        long totalCnt = youtubeRecipeRepository.countByKeyword(keyword);

        List<YoutubeRecipe> youtubeRecipes;
        if (totalCnt < MIN_RECIPE_CNT) {
            youtubeRecipes = youtubeRecipeClientSearchService.searchYoutube(keyword).subList(0, size);
            totalCnt = youtubeRecipeRepository.countByKeyword(keyword);
        } else {
            youtubeRecipes = findByKeywordSortBy(keyword, lastYoutubeRecipeId, size, sort);
        }

        return getRecipes(user, totalCnt, youtubeRecipes);
    }

    @Transactional(readOnly = true)
    public List<YoutubeRecipe> findByKeywordSortBy(String keyword, Long lastYoutubeRecipeId, int size, String sort) {

        if (sort.equals("youtubeScraps")) {
            long youtubeScrapCnt = youtubeScrapService.countByYoutubeRecipeId(lastYoutubeRecipeId);
            return youtubeRecipeRepository.findByKeywordLimitOrderByYoutubeScrapCntDesc(keyword, lastYoutubeRecipeId, youtubeScrapCnt, size);
        } else if (sort.equals("youtubeViews")) {
            long youtubeViewCnt = youtubeViewService.countByYoutubeRecipeId(lastYoutubeRecipeId);
            return youtubeRecipeRepository.findByKeywordLimitOrderByYoutubeViewCntDesc(keyword, lastYoutubeRecipeId, youtubeViewCnt, size);
        } else {
            YoutubeRecipe youtubeRecipe = null;
            if (lastYoutubeRecipeId != null && lastYoutubeRecipeId > 0) {
                youtubeRecipe = youtubeRecipeRepository.findById(lastYoutubeRecipeId).orElseThrow(() -> {
                    throw new NotFoundRecipeException();
                });
            }
            return youtubeRecipeRepository.findByKeywordLimitOrderByPostDateDesc(keyword, lastYoutubeRecipeId, youtubeRecipe != null ? youtubeRecipe.getPostDate() : null, size);
        }
    }

    @Transactional(readOnly = true)
    public RecipesResponse getScrapYoutubeRecipes(User user, Long lastYoutubeRecipeId, int size) {

        long totalCnt = youtubeScrapService.countYoutubeScrapByUser(user);
        YoutubeScrap youtubeScrap = null;
        if (lastYoutubeRecipeId != null && lastYoutubeRecipeId > 0) {
            youtubeScrap = youtubeScrapService.findByUserIdAndYoutubeRecipeId(user.getUserId(), lastYoutubeRecipeId);
        }
        List<YoutubeRecipe> youtubeRecipes = youtubeRecipeRepository.findUserScrapYoutubeRecipesLimit(user.getUserId(), lastYoutubeRecipeId, youtubeScrap != null ? youtubeScrap.getCreatedAt() : null, size);

        return getRecipes(user, totalCnt, youtubeRecipes);
    }

    private RecipesResponse getRecipes(User user, long totalCnt, List<YoutubeRecipe> youtubeRecipes) {

        List<Long> youtubeRecipeIds = youtubeRecipes.stream()
                .map(YoutubeRecipe::getYoutubeRecipeId)
                .collect(Collectors.toList());
        List<YoutubeScrap> youtubeScraps = youtubeScrapService.findByYoutubeRecipeIds(youtubeRecipeIds);

        return RecipesResponse.from(totalCnt, new YoutubeRecipes(youtubeRecipes), youtubeScraps, user);
    }

    @Transactional
    public void createYoutubeScrap(User user, Long youtubeRecipeId) {

        YoutubeRecipe youtubeRecipe = youtubeRecipeRepository.findById(youtubeRecipeId).orElseThrow(() -> {
            throw new NotFoundRecipeException();
        });
        youtubeRecipe.plusScrapCnt();
        youtubeRecipeRepository.save(youtubeRecipe);
        youtubeScrapService.createYoutubeScrap(user, youtubeRecipeId);
    }

    @Transactional
    public void deleteYoutubeScrap(User user, Long youtubeRecipeId) {

        YoutubeRecipe youtubeRecipe = youtubeRecipeRepository.findById(youtubeRecipeId).orElseThrow(() -> {
            throw new NotFoundRecipeException();
        });
        youtubeRecipe.minusScrapCnt();
        youtubeRecipeRepository.save(youtubeRecipe);
        youtubeScrapService.deleteYoutubeScrap(user, youtubeRecipeId);
    }

    @Transactional
    public void createYoutubeView(User user, Long youtubeRecipeId) {

        YoutubeRecipe youtubeRecipe = youtubeRecipeRepository.findById(youtubeRecipeId).orElseThrow(() -> {
            throw new NotFoundRecipeException();
        });
        youtubeRecipe.plusViewCnt();
        youtubeRecipeRepository.save(youtubeRecipe);
        youtubeViewService.createYoutubeView(user, youtubeRecipeId);
    }
}
