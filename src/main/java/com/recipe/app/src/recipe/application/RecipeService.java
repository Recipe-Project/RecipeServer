package com.recipe.app.src.recipe.application;

import com.google.common.base.Preconditions;
import com.recipe.app.src.etc.application.BadWordService;
import com.recipe.app.src.fridge.application.FridgeService;
import com.recipe.app.src.recipe.application.dto.RecipeDetailResponse;
import com.recipe.app.src.recipe.application.dto.RecipeRequest;
import com.recipe.app.src.recipe.application.dto.RecipesResponse;
import com.recipe.app.src.recipe.application.dto.RecommendedRecipesResponse;
import com.recipe.app.src.recipe.domain.Recipe;
import com.recipe.app.src.recipe.domain.RecipeScrap;
import com.recipe.app.src.recipe.exception.NotFoundRecipeException;
import com.recipe.app.src.recipe.infra.RecipeRepository;
import com.recipe.app.src.user.application.UserService;
import com.recipe.app.src.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final FridgeService fridgeService;
    private final UserService userService;
    private final BadWordService badWordService;
    private final RecipeScrapService recipeScrapService;
    private final RecipeViewService recipeViewService;
    private final RecipeReportService recipeReportService;

    public RecipeService(RecipeRepository recipeRepository, FridgeService fridgeService, UserService userService, BadWordService badWordService,
                         RecipeScrapService recipeScrapService, RecipeViewService recipeViewService, RecipeReportService recipeReportService) {
        this.recipeRepository = recipeRepository;
        this.fridgeService = fridgeService;
        this.userService = userService;
        this.badWordService = badWordService;
        this.recipeScrapService = recipeScrapService;
        this.recipeViewService = recipeViewService;
        this.recipeReportService = recipeReportService;
    }

    @Transactional(readOnly = true)
    public RecipesResponse findRecipesByKeywordOrderBy(User user, String keyword, long lastRecipeId, int size, String sort) {

        badWordService.checkBadWords(keyword);

        long totalCnt = recipeRepository.countByKeyword(keyword);

        List<Recipe> recipes;
        if (sort.equals("recipeScraps")) {
            recipes = findByKeywordOrderByRecipeScrapCnt(keyword, lastRecipeId, size);
        } else if (sort.equals("recipeViews")) {
            recipes = findByKeywordOrderByRecipeViewCnt(keyword, lastRecipeId, size);
        } else {
            recipes = findByKeywordOrderByCreatedAt(keyword, lastRecipeId, size);
        }

        return getRecipes(user, totalCnt, recipes);
    }

    private List<Recipe> findByKeywordOrderByRecipeScrapCnt(String keyword, long lastRecipeId, int size) {

        long recipeScrapCnt = recipeScrapService.countByRecipeId(lastRecipeId);

        return recipeRepository.findByKeywordLimitOrderByRecipeScrapCntDesc(keyword, lastRecipeId, recipeScrapCnt, size);
    }

    private List<Recipe> findByKeywordOrderByRecipeViewCnt(String keyword, long lastRecipeId, int size) {

        long recipeViewCnt = recipeViewService.countByRecipeId(lastRecipeId);

        return recipeRepository.findByKeywordLimitOrderByRecipeViewCntDesc(keyword, lastRecipeId, recipeViewCnt, size);
    }

    private List<Recipe> findByKeywordOrderByCreatedAt(String keyword, long lastRecipeId, int size) {

        Recipe recipe = recipeRepository.findById(lastRecipeId).orElse(null);

        return recipeRepository.findByKeywordLimitOrderByCreatedAtDesc(keyword, lastRecipeId, recipe != null ? recipe.getCreatedAt() : null, size);
    }

    @Transactional(readOnly = true)
    public RecipeDetailResponse findRecipeDetail(User user, long recipeId) {

        Recipe recipe = recipeRepository.findRecipeDetail(recipeId, user.getUserId())
                .orElseThrow(() -> {
                    throw new NotFoundRecipeException();
                });

        List<String> ingredientNamesInFridge = fridgeService.findIngredientNamesInFridge(user.getUserId());

        boolean isUserScrap = recipeScrapService.existsByUserIdAndRecipeId(user.getUserId(), recipe.getRecipeId());

        User postUser = null;
        if (recipe.getUserId() != null) {
            postUser = userService.findByUserId(recipe.getUserId());
        }

        return RecipeDetailResponse.from(recipe, isUserScrap, postUser, ingredientNamesInFridge);
    }

    @Transactional(readOnly = true)
    public RecipesResponse findScrapRecipes(User user, long lastRecipeId, int size) {

        long totalCnt = recipeScrapService.countByUserId(user.getUserId());

        RecipeScrap recipeScrap = recipeScrapService.findByUserIdAndRecipeId(user.getUserId(), lastRecipeId);

        List<Recipe> recipes = recipeRepository.findUserScrapRecipesLimit(user.getUserId(), lastRecipeId, recipeScrap != null ? recipeScrap.getCreatedAt() : null, size);

        return getRecipes(user, totalCnt, recipes);
    }

    @Transactional(readOnly = true)
    public RecipesResponse findRecipesByUser(User user, long lastRecipeId, int size) {

        long totalCnt = recipeRepository.countByUserId(user.getUserId());

        List<Recipe> recipes = findLimitByUserId(user.getUserId(), lastRecipeId, size);

        return getRecipes(user, totalCnt, recipes);
    }

    @Transactional(readOnly = true)
    public List<Recipe> findLimitByUserId(Long userId, long lastRecipeId, int size) {

        return recipeRepository.findLimitByUserId(userId, lastRecipeId, size);
    }

    private RecipesResponse getRecipes(User user, long totalCnt, List<Recipe> recipes) {

        List<User> recipePostUsers = userService.findRecipePostUsers(recipes);

        List<RecipeScrap> recipeScraps = recipeScrapService.findByRecipeIds(recipes);

        return RecipesResponse.from(totalCnt, recipes, recipePostUsers, recipeScraps, user);
    }

    @Transactional(readOnly = true)
    public RecommendedRecipesResponse findRecommendedRecipesByUserFridge(User user, long lastRecipeId, int size) {

        List<String> ingredientNamesInFridge = fridgeService.findIngredientNamesInFridge(user.getUserId());

        List<Recipe> recipes = recipeRepository.findRecipesInFridge(ingredientNamesInFridge);

        List<User> recipePostUsers = userService.findRecipePostUsers(recipes);

        List<RecipeScrap> recipeScraps = recipeScrapService.findByRecipeIds(recipes);

        Recipe lastRecipe = recipeRepository.findById(lastRecipeId).orElse(null);

        return RecommendedRecipesResponse.from(recipes, recipePostUsers, recipeScraps, user, ingredientNamesInFridge, lastRecipe, size);
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
    public void deleteRecipesByUser(User user) {

        List<Recipe> recipes = recipeRepository.findByUserId(user.getUserId());

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

