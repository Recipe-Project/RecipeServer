package com.recipe.app.src.recipe.application;

import com.google.common.base.Preconditions;
import com.recipe.app.src.common.application.BadWordService;
import com.recipe.app.src.fridge.application.FridgeService;
import com.recipe.app.src.ingredient.application.IngredientService;
import com.recipe.app.src.ingredient.domain.Ingredient;
import com.recipe.app.src.recipe.application.dto.RecipeDetailResponse;
import com.recipe.app.src.recipe.application.dto.RecipeIngredientResponse;
import com.recipe.app.src.recipe.application.dto.RecipeProcessResponse;
import com.recipe.app.src.recipe.application.dto.RecipeRequest;
import com.recipe.app.src.recipe.application.dto.RecipeResponse;
import com.recipe.app.src.recipe.application.dto.RecipesResponse;
import com.recipe.app.src.recipe.application.dto.RecommendedRecipeResponse;
import com.recipe.app.src.recipe.application.dto.RecommendedRecipesResponse;
import com.recipe.app.src.recipe.domain.Recipe;
import com.recipe.app.src.recipe.domain.RecipeIngredient;
import com.recipe.app.src.recipe.domain.RecipeScrap;
import com.recipe.app.src.recipe.domain.RecipeView;
import com.recipe.app.src.recipe.exception.NotFoundRecipeException;
import com.recipe.app.src.recipe.infra.RecipeRepository;
import com.recipe.app.src.user.application.UserService;
import com.recipe.app.src.user.application.dto.UserRecipeResponse;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.user.exception.ForbiddenUserException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeIngredientService recipeIngredientService;
    private final RecipeProcessService recipeProcessService;
    private final RecipeScrapService recipeScrapService;
    private final RecipeViewService recipeViewService;
    private final FridgeService fridgeService;
    private final UserService userService;
    private final BadWordService badWordService;
    private final IngredientService ingredientService;

    public RecipeService(RecipeRepository recipeRepository, RecipeIngredientService recipeIngredientService, RecipeProcessService recipeProcessService,
                         RecipeScrapService recipeScrapService, RecipeViewService recipeViewService, FridgeService fridgeService,
                         UserService userService, BadWordService badWordService, IngredientService ingredientService) {
        this.recipeRepository = recipeRepository;
        this.recipeIngredientService = recipeIngredientService;
        this.recipeProcessService = recipeProcessService;
        this.recipeScrapService = recipeScrapService;
        this.recipeViewService = recipeViewService;
        this.fridgeService = fridgeService;
        this.userService = userService;
        this.badWordService = badWordService;
        this.ingredientService = ingredientService;
    }

    @Transactional(readOnly = true)
    public RecipesResponse getRecipesByKeyword(User user, String keyword, Long lastRecipeId, int size, String sort) {

        badWordService.checkBadWords(keyword);

        long totalCnt = recipeRepository.countByKeyword(keyword);
        List<Recipe> recipes;
        if (sort.equals("recipeScraps")) {
            long recipeScrapCnt = recipeScrapService.countByRecipeId(lastRecipeId);
            recipes = recipeRepository.findByKeywordLimitOrderByRecipeScrapCntDesc(keyword, lastRecipeId, recipeScrapCnt, size);
        } else if (sort.equals("recipeViews")) {
            long recipeViewCnt = recipeViewService.countByRecipeId(lastRecipeId);
            recipes = recipeRepository.findByKeywordLimitOrderByRecipeViewCntDesc(keyword, lastRecipeId, recipeViewCnt, size);
        } else {
            Recipe recipe = null;
            if (lastRecipeId != null && lastRecipeId > 0) {
                recipe = recipeRepository.findById(lastRecipeId).orElseThrow(() -> {
                    throw new NotFoundRecipeException();
                });
            }
            recipes = recipeRepository.findByKeywordLimitOrderByCreatedAtDesc(keyword, lastRecipeId, recipe != null ? recipe.getCreatedAt() : null, size);
        }

        return getRecipes(user, totalCnt, recipes);
    }

    @Transactional(readOnly = true)
    public RecipeDetailResponse getRecipe(User user, Long recipeId) {

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> {
                    throw new NotFoundRecipeException();
                });
        if (recipe.isHidden() && !user.getUserId().equals(recipe.getUserId()))
            throw new ForbiddenUserException();

        List<RecipeIngredientResponse> recipeIngredients = recipeIngredientService.findRecipeIngredientsByUserIdAndRecipeId(user.getUserId(), recipeId);
        List<RecipeProcessResponse> recipeProcesses = recipeProcessService.fineRecipeProcessesByRecipeId(recipeId);
        List<RecipeScrap> recipeScraps = recipeScrapService.findByRecipeId(recipeId);
        List<RecipeView> recipeViews = recipeViewService.findByRecipeId(recipeId);
        boolean isUserScrap = recipeScraps.stream()
                .anyMatch(recipeScrap -> recipeScrap.getUserId().equals(user.getUserId()));

        return RecipeDetailResponse.from(recipe, recipeIngredients, recipeProcesses, isUserScrap, recipeScraps.size(), recipeViews.size());
    }

    @Transactional(readOnly = true)
    public RecipesResponse getScrapRecipes(User user, Long lastRecipeId, int size) {

        long totalCnt = recipeScrapService.countByUserId(user.getUserId());
        RecipeScrap recipeScrap = null;
        if (lastRecipeId != null && lastRecipeId > 0) {
            recipeScrap = recipeScrapService.findByUserIdAndRecipeId(user.getUserId(), lastRecipeId);
        }
        List<Recipe> recipes = recipeRepository.findUserScrapRecipesLimit(user.getUserId(), lastRecipeId, recipeScrap != null ? recipeScrap.getCreatedAt() : null, size);

        return getRecipes(user, totalCnt, recipes);
    }

    @Transactional(readOnly = true)
    public RecipesResponse getRecipesByUser(User user, Long lastRecipeId, int size) {

        long totalCnt = recipeRepository.countByUserId(user.getUserId());
        List<Recipe> recipes = recipeRepository.findLimitByUserId(user.getUserId(), lastRecipeId, size);

        return getRecipes(user, totalCnt, recipes);
    }

    @Transactional(readOnly = true)
    public List<UserRecipeResponse> getUserRecipesByUser(User user, Long lastRecipeId, int size) {

        List<Recipe> recipes = recipeRepository.findLimitByUserId(user.getUserId(), lastRecipeId, size);

        return recipes.stream()
                .map(r -> new UserRecipeResponse(r.getRecipeId(), r.getImgUrl()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteRecipe(User user, Long recipeId) {

        Recipe recipe = getRecipeByUserIdAndRecipeId(user, recipeId);

        recipeScrapService.deleteAllByRecipeId(recipe.getRecipeId());
        recipeViewService.deleteAllByRecipeId(recipe.getRecipeId());
        recipeIngredientService.deleteAllByRecipeId(recipe.getRecipeId());
        recipeProcessService.deleteAllByRecipeId(recipe.getRecipeId());
        recipeRepository.delete(recipe);
    }

    @Transactional
    public void createRecipe(User user, RecipeRequest request) {

        Preconditions.checkArgument(StringUtils.hasText(request.getTitle()), "레시피 제목을 입력해주세요.");
        Preconditions.checkArgument(StringUtils.hasText(request.getContent()), "레시피 설명을 입력해주세요.");
        Objects.requireNonNull(request.getIngredients(), "레시피 재료 목록을 입력해주세요.");

        badWordService.checkBadWords(request.getTitle());
        badWordService.checkBadWords(request.getContent());

        Recipe recipe = Recipe.builder()
                .recipeNm(request.getTitle())
                .imgUrl(request.getThumbnail())
                .userId(user.getUserId())
                .build();

        recipeRepository.save(recipe);
        recipeProcessService.createRecipeProcesses(recipe.getRecipeId(), request.getContent());
        recipeIngredientService.createRecipeIngredients(recipe.getRecipeId(), request.getIngredients());
    }

    @Transactional
    public void updateRecipe(User user, Long recipeId, RecipeRequest request) {

        Preconditions.checkArgument(StringUtils.hasText(request.getTitle()), "레시피 제목을 입력해주세요.");
        Preconditions.checkArgument(StringUtils.hasText(request.getContent()), "레시피 설명을 입력해주세요.");
        Objects.requireNonNull(request.getIngredients(), "레시피 재료 목록을 입력해주세요.");

        badWordService.checkBadWords(request.getTitle());
        badWordService.checkBadWords(request.getContent());

        Recipe recipe = getRecipeByUserIdAndRecipeId(user, recipeId);

        recipe.updateRecipe(request.getTitle(), request.getThumbnail());
        recipeRepository.save(recipe);

        recipeProcessService.deleteAllByRecipeId(recipeId);
        recipeProcessService.createRecipeProcesses(recipeId, request.getContent());

        recipeIngredientService.deleteAllByRecipeId(recipeId);
        recipeIngredientService.createRecipeIngredients(recipeId, request.getIngredients());
    }

    private Recipe getRecipeByUserIdAndRecipeId(User user, Long recipeId) {
        return recipeRepository.findByUserIdAndRecipeId(user.getUserId(), recipeId)
                .orElseThrow(() -> {
                    throw new NotFoundRecipeException();
                });
    }

    @Transactional(readOnly = true)
    public RecommendedRecipesResponse findRecommendedRecipesByUserFridge(User user, Long lastRecipeId, int size) {

        List<Ingredient> ingredientsInFridge = fridgeService.findIngredientsInUserFridge(user);
        List<Long> ingredientIdsInFridge = ingredientsInFridge.stream()
                .map(Ingredient::getIngredientId)
                .collect(Collectors.toList());
        List<String> ingredientNamesInFridge = ingredientsInFridge.stream()
                .flatMap(ingredient -> Stream.of(ingredient.getIngredientName(), ingredient.getSimilarIngredientName()))
                .map(Object::toString)
                .collect(Collectors.toList());

        List<Recipe> recipes = recipeRepository.findRecipesInFridge(ingredientNamesInFridge);
        List<Long> recipeIds = recipes.stream()
                .map(Recipe::getRecipeId)
                .collect(Collectors.toList());

        Map<Long, Long> matchRateMapByRecipeId = getMatchRateMapByRecipeId(recipeIds, ingredientIdsInFridge, ingredientNamesInFridge);

        List<RecipeScrap> recipeScraps = recipeScrapService.findByRecipeIds(recipeIds);
        List<RecipeView> recipeViews = recipeViewService.findByRecipeIds(recipeIds);

        return new RecommendedRecipesResponse(recipes.size(), recipes.stream()
                .map((recipe) -> {
                    long scrapCnt = getRecipeScrapCnt(recipeScraps, recipe.getRecipeId(), user);
                    long viewCnt = getRecipeViewCnt(recipeViews, recipe.getRecipeId(), user);
                    boolean isUserScrap = isUserScrap(recipeScraps, recipe.getRecipeId(), user);

                    return RecommendedRecipeResponse.from(recipe, user, matchRateMapByRecipeId.get(recipe.getRecipeId()).intValue(), isUserScrap, scrapCnt, viewCnt);
                })
                .sorted(Comparator.comparing(RecommendedRecipeResponse::getIngredientsMatchRate).thenComparing(RecommendedRecipeResponse::getRecipeId).reversed())
                .filter(recommendedRecipe -> lastRecipeId == null || lastRecipeId <= 0 || recommendedRecipe.getRecipeId() < lastRecipeId)
                .limit(size)
                .collect(Collectors.toList()));
    }

    private Map<Long, Long> getMatchRateMapByRecipeId(List<Long> recipeIds, List<Long> ingredientIdsInFridge, List<String> ingredientNamesInFridge) {

        List<RecipeIngredient> recipeIngredients = recipeIngredientService.findByRecipeIds(recipeIds);
        Map<Long, List<String>> ingredientNamesMapByRecipeId = recipeIngredients.stream()
                .collect(Collectors.groupingBy(RecipeIngredient::getRecipeId, Collectors.mapping(RecipeIngredient::getIngredientName, Collectors.toList())));

        return ingredientNamesMapByRecipeId.keySet().stream()
                .collect(Collectors.toMap(Function.identity(), recipeId -> {
                    List<String> ingredientNamesInRecipe = ingredientNamesMapByRecipeId.get(recipeId);
                    long recipeIngredientCnt = ingredientNamesInRecipe.size();
                    long recipeIngredientInFridgeCnt = ingredientNamesInRecipe.stream()
                            .filter(ingredientNamesInFridge::contains)
                            .count();

                    return (long) ((double) recipeIngredientInFridgeCnt / recipeIngredientCnt * 100);
                }));
    }

    @Transactional(readOnly = true)
    public long countRecipeScrapByUser(User user) {

        return recipeScrapService.countByUserId(user.getUserId());
    }

    @Transactional
    public void deleteRecipesByUser(User user) {

        List<Recipe> recipes = recipeRepository.findByUserId(user.getUserId());
        List<Long> recipeIds = recipes.stream()
                .map(Recipe::getRecipeId)
                .collect(Collectors.toList());

        recipeIngredientService.deleteAllByRecipeIds(recipeIds);
        recipeProcessService.deleteAllByRecipeIds(recipeIds);
        recipeRepository.deleteAll(recipes);
    }

    private RecipesResponse getRecipes(User user, long totalCnt, List<Recipe> recipes) {

        List<Long> recipePostUserIds = recipes.stream()
                .map(Recipe::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        Map<Long, User> recipePostUserMapByUserId = userService.findByUserIds(recipePostUserIds).stream()
                .collect(Collectors.toMap(User::getUserId, Function.identity()));

        List<Long> recipeIds = recipes.stream()
                .map(Recipe::getRecipeId)
                .collect(Collectors.toList());
        List<RecipeScrap> recipeScraps = recipeScrapService.findByRecipeIds(recipeIds);
        List<RecipeView> recipeViews = recipeViewService.findByRecipeIds(recipeIds);

        return new RecipesResponse(totalCnt, recipes.stream()
                .map((recipe) -> {
                    long scrapCnt = getRecipeScrapCnt(recipeScraps, recipe.getRecipeId(), user);
                    long viewCnt = getRecipeViewCnt(recipeViews, recipe.getRecipeId(), user);
                    boolean isUserScrap = isUserScrap(recipeScraps, recipe.getRecipeId(), user);

                    return RecipeResponse.from(recipe, recipePostUserMapByUserId.get(recipe.getUserId()), isUserScrap, scrapCnt, viewCnt);
                })
                .collect(Collectors.toList()));
    }

    private boolean isUserScrap(List<RecipeScrap> recipeScraps, Long recipeId, User user) {
        return recipeScraps.stream()
                .anyMatch(recipeScrap -> recipeScrap.getRecipeId().equals(recipeId) && recipeScrap.getUserId().equals(user.getUserId()));
    }

    private long getRecipeViewCnt(List<RecipeView> recipeViews, Long recipeId, User user) {
        return recipeViews.stream()
                .filter(recipeView -> recipeView.getRecipeId().equals(recipeId) && recipeView.getUserId().equals(user.getUserId()))
                .count();
    }

    private long getRecipeScrapCnt(List<RecipeScrap> recipeScraps, Long recipeId, User user) {
        return recipeScraps.stream()
                .filter(recipeScrap -> recipeScrap.getRecipeId().equals(recipeId) && recipeScrap.getUserId().equals(user.getUserId()))
                .count();
    }

    @Transactional
    public void createRecipeScrap(User user, Long recipeId) {

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> {
                    throw new NotFoundRecipeException();
                });
        recipe.plusScrapCnt();
        recipeRepository.save(recipe);
        recipeScrapService.createRecipeScrap(user, recipeId);
    }

    @Transactional
    public void deleteRecipeScrap(User user, Long recipeId) {

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> {
                    throw new NotFoundRecipeException();
                });
        recipe.minusScrapCnt();
        recipeRepository.save(recipe);
        recipeScrapService.deleteRecipeScrap(user, recipeId);
    }

    @Transactional
    public void createRecipeView(User user, Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> {
                    throw new NotFoundRecipeException();
                });
        recipe.plusViewCnt();
        recipeRepository.save(recipe);
        recipeViewService.createRecipeView(user, recipeId);
    }
}

