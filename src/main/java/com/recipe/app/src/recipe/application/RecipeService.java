package com.recipe.app.src.recipe.application;

import com.google.common.base.Preconditions;
import com.recipe.app.src.etc.application.BadWordService;
import com.recipe.app.src.recipe.application.dto.RecipeRequest;
import com.recipe.app.src.recipe.domain.Recipe;
import com.recipe.app.src.recipe.exception.NotFoundRecipeException;
import com.recipe.app.src.recipe.infra.RecipeRepository;
import com.recipe.app.src.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final BadWordService badWordService;
    private final RecipeScrapService recipeScrapService;
    private final RecipeViewService recipeViewService;
    private final RecipeReportService recipeReportService;

    public RecipeService(RecipeRepository recipeRepository, BadWordService badWordService, RecipeScrapService recipeScrapService,
                         RecipeViewService recipeViewService, RecipeReportService recipeReportService) {
        this.recipeRepository = recipeRepository;
        this.badWordService = badWordService;
        this.recipeScrapService = recipeScrapService;
        this.recipeViewService = recipeViewService;
        this.recipeReportService = recipeReportService;
    }

    @Transactional
    public void createRecipe(User user, RecipeRequest request) {

        validateRecipeRequest(request);

        badWordService.checkBadWords(request.getTitle());
        badWordService.checkBadWords(request.getIntroduction());

        recipeRepository.save(request.toRecipeEntity(user.getUserId()));
    }

    @Transactional
    public void updateRecipe(User user, Long recipeId, RecipeRequest request) {

        validateRecipeRequest(request);

        badWordService.checkBadWords(request.getTitle());
        badWordService.checkBadWords(request.getIntroduction());

        Recipe recipe = getRecipeByUserIdAndRecipeId(user, recipeId);
        recipe.updateRecipe(request.toRecipeEntity(user.getUserId()));

        recipeRepository.save(recipe);
    }

    private void validateRecipeRequest(RecipeRequest request) {

        Preconditions.checkArgument(StringUtils.hasText(request.getTitle()), "레시피 제목을 입력해주세요.");
        Preconditions.checkArgument(StringUtils.hasText(request.getIntroduction()), "레시피 설명을 입력해주세요.");
        Objects.requireNonNull(request.getIngredients(), "레시피 재료 목록을 입력해주세요.");
        Objects.requireNonNull(request.getProcesses(), "레시피 과정을 입력해주세요.");
    }

    @Transactional
    public void deleteRecipe(User user, Long recipeId) {

        Recipe recipe = getRecipeByUserIdAndRecipeId(user, recipeId);

        recipeScrapService.deleteAllByRecipeId(recipe.getRecipeId());
        recipeViewService.deleteAllByRecipeId(recipe.getRecipeId());
        recipeReportService.deleteAllByRecipeId(recipe.getRecipeId());
        recipeRepository.delete(recipe);
    }

    private Recipe getRecipeByUserIdAndRecipeId(User user, Long recipeId) {

        return recipeRepository.findByUserIdAndRecipeId(user.getUserId(), recipeId)
                .orElseThrow(() -> {
                    throw new NotFoundRecipeException();
                });
    }

    @Transactional
    public void deleteRecipesByUserId(long userId) {

        List<Recipe> recipes = recipeRepository.findByUserId(userId);

        recipeRepository.deleteAll(recipes);
    }

    @Transactional(readOnly = true)
    public long countRecipeScrapByUserId(long userId) {

        return recipeScrapService.countByUserId(userId);
    }

    @Transactional
    public void createRecipeScrap(User user, Long recipeId) {

        recipeRepository.findById(recipeId)
                .ifPresent((recipe) -> {
                    recipe.plusScrapCnt();
                    recipeScrapService.createRecipeScrap(user.getUserId(), recipeId);
                });
    }

    @Transactional
    public void deleteRecipeScrap(User user, long recipeId) {

        recipeRepository.findById(recipeId)
                .ifPresent((recipe) -> {
                    recipe.minusScrapCnt();
                    recipeScrapService.deleteRecipeScrap(user.getUserId(), recipeId);
                });
    }

    @Transactional
    public void createRecipeView(User user, long recipeId) {

        recipeRepository.findById(recipeId)
                .ifPresent((recipe) -> {
                    recipe.plusViewCnt();
                    recipeViewService.createRecipeView(user.getUserId(), recipeId);
                });
    }

    @Transactional
    public void createRecipeReport(User user, Long recipeId) {

        recipeRepository.findById(recipeId)
                .ifPresent((recipe) -> {

                    recipeReportService.createRecipeReport(user.getUserId(), recipeId);

                    if (recipeReportService.isRecipeReported(recipeId)) {
                        recipe.report();
                    }
                });
    }
}

