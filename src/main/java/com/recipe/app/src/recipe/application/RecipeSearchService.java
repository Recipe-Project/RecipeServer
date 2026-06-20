package com.recipe.app.src.recipe.application;

import com.recipe.app.src.common.utils.BadWordFiltering;
import com.recipe.app.src.common.utils.SearchKeywordNormalizer;
import com.recipe.app.src.common.utils.SearchKeywordNormalizer.SearchQuery;
import com.recipe.app.src.fridge.application.FridgeService;
import com.recipe.app.src.ingredient.application.IngredientSynonymCache;
import com.recipe.app.src.recipe.application.dto.RecipeDetailResponse;
import com.recipe.app.src.recipe.application.dto.RecipesResponse;
import com.recipe.app.src.recipe.application.dto.RecommendedRecipesResponse;
import com.recipe.app.src.recipe.domain.Recipe;
import com.recipe.app.src.recipe.domain.RecipeScrap;
import com.recipe.app.src.recipe.domain.Recipes;
import com.recipe.app.src.recipe.exception.NotFoundRecipeException;
import com.recipe.app.src.recipe.infra.RecipeRepository;
import com.recipe.app.src.user.application.UserService;
import com.recipe.app.src.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RecipeSearchService {

    private final RecipeRepository recipeRepository;
    private final FridgeService fridgeService;
    private final UserService userService;
    private final BadWordFiltering badWordFiltering;
    private final RecipeScrapService recipeScrapService;
    private final RecipeViewService recipeViewService;
    private final IngredientSynonymCache ingredientSynonymCache;

    public RecipeSearchService(RecipeRepository recipeRepository, FridgeService fridgeService, UserService userService, BadWordFiltering badWordFiltering,
                               RecipeScrapService recipeScrapService, RecipeViewService recipeViewService, IngredientSynonymCache ingredientSynonymCache) {
        this.recipeRepository = recipeRepository;
        this.fridgeService = fridgeService;
        this.userService = userService;
        this.badWordFiltering = badWordFiltering;
        this.recipeScrapService = recipeScrapService;
        this.recipeViewService = recipeViewService;
        this.ingredientSynonymCache = ingredientSynonymCache;
    }

    @Transactional(readOnly = true)
    public RecipesResponse findRecipesByKeywordOrderBy(User user, String keyword, long lastRecipeId, int size, String sort) {

        badWordFiltering.check(keyword);

        SearchQuery query = SearchKeywordNormalizer.normalize(keyword);
        if (query instanceof SearchQuery.Empty) {
            return getRecipes(user, 0L, new Recipes(List.of()));
        }

        long totalCnt = recipeRepository.countByKeyword(query);

        List<Recipe> recipes;
        if (sort.equals("scraps")) {
            recipes = findByKeywordOrderByRecipeScrapCnt(query, lastRecipeId, size);
        } else if (sort.equals("views")) {
            recipes = findByKeywordOrderByRecipeViewCnt(query, lastRecipeId, size);
        } else {
            recipes = findByKeywordOrderByCreatedAt(query, lastRecipeId, size);
        }

        return getRecipes(user, totalCnt, new Recipes(recipes));
    }

    private List<Recipe> findByKeywordOrderByRecipeScrapCnt(SearchQuery query, long lastRecipeId, int size) {

        long recipeScrapCnt = recipeScrapService.countByRecipeId(lastRecipeId);

        return recipeRepository.findByKeywordLimitOrderByRecipeScrapCntDesc(query, lastRecipeId, recipeScrapCnt, size);
    }

    private List<Recipe> findByKeywordOrderByRecipeViewCnt(SearchQuery query, long lastRecipeId, int size) {

        long recipeViewCnt = recipeViewService.countByRecipeId(lastRecipeId);

        return recipeRepository.findByKeywordLimitOrderByRecipeViewCntDesc(query, lastRecipeId, recipeViewCnt, size);
    }

    private List<Recipe> findByKeywordOrderByCreatedAt(SearchQuery query, long lastRecipeId, int size) {

        Recipe recipe = recipeRepository.findById(lastRecipeId).orElse(null);

        return recipeRepository.findByKeywordLimitOrderByCreatedAtDesc(query, lastRecipeId, recipe != null ? recipe.getCreatedAt() : null, size);
    }

    @Transactional(readOnly = true)
    public RecipeDetailResponse findRecipeDetail(User user, long recipeId) {

        // user == null(비로그인): 쿼리가 공개글만 반환하고, 냉장고 매칭/스크랩 여부 등 개인화는 생략
        Long userId = user != null ? user.getUserId() : null;

        Recipe recipe = recipeRepository.findRecipeDetail(recipeId, userId)
                .orElseThrow(NotFoundRecipeException::new);

        List<String> ingredientNamesInFridge = userId != null
                ? fridgeService.findIngredientNamesInFridge(userId)
                : List.of();

        boolean isUserScrap = userId != null
                && recipeScrapService.existsByUserIdAndRecipeId(userId, recipe.getRecipeId());

        User postUser = recipe.getUserId() != null ? userService.findByUserId(recipe.getUserId()) : null;

        return RecipeDetailResponse.from(recipe, isUserScrap, postUser, ingredientNamesInFridge);
    }

    @Transactional(readOnly = true)
    public RecipesResponse findScrapRecipes(User user, long lastRecipeId, int size) {

        long totalCnt = recipeScrapService.countByUserId(user.getUserId());

        RecipeScrap recipeScrap = recipeScrapService.findByUserIdAndRecipeId(user.getUserId(), lastRecipeId);

        List<Recipe> recipes = recipeRepository.findUserScrapRecipesLimit(user.getUserId(), lastRecipeId, recipeScrap != null ? recipeScrap.getCreatedAt() : null, size);

        return getRecipes(user, totalCnt, new Recipes(recipes));
    }

    @Transactional(readOnly = true)
    public RecipesResponse findRecipesByUser(User user, long lastRecipeId, int size) {

        long totalCnt = recipeRepository.countByUserId(user.getUserId());

        List<Recipe> recipes = findLimitByUserId(user.getUserId(), lastRecipeId, size);

        return getRecipes(user, totalCnt, new Recipes(recipes));
    }

    @Transactional(readOnly = true)
    public List<Recipe> findLimitByUserId(Long userId, long lastRecipeId, int size) {

        return recipeRepository.findLimitByUserId(userId, lastRecipeId, size);
    }

    private RecipesResponse getRecipes(User user, long totalCnt, Recipes recipes) {

        List<User> recipePostUsers = userService.findByUserIds(recipes.getUserIds());

        List<RecipeScrap> recipeScraps = recipeScrapService.findByRecipeIds(recipes.getRecipeIds());

        return RecipesResponse.from(totalCnt, recipes, recipePostUsers, recipeScraps, user);
    }

    @Transactional(readOnly = true)
    public RecommendedRecipesResponse findRecommendedRecipesByUserFridge(User user, long lastRecipeId, int size) {

        List<String> ingredientNamesInFridge = fridgeService.findIngredientNamesInFridge(user.getUserId());

        Recipes recipes = new Recipes(recipeRepository.findRecipesInFridge(ingredientNamesInFridge));

        List<User> recipePostUsers = userService.findByUserIds(recipes.getUserIds());

        List<RecipeScrap> recipeScraps = recipeScrapService.findByRecipeIds(recipes.getRecipeIds());

        Recipe lastRecipe = recipeRepository.findById(lastRecipeId).orElse(null);

        return RecommendedRecipesResponse.from(recipes, recipePostUsers, recipeScraps, user, ingredientNamesInFridge, lastRecipe, size);
    }

    @Transactional(readOnly = true)
    public RecommendedRecipesResponse findPublicRecommendedRecipesByIngredients(List<String> ingredientNames, long lastRecipeId, int size) {

        List<String> expandedIngredientNames = List.copyOf(ingredientSynonymCache.expand(ingredientNames));

        Recipes recipes = new Recipes(recipeRepository.findRecipesInFridge(expandedIngredientNames));

        List<User> recipePostUsers = userService.findByUserIds(recipes.getUserIds());

        List<RecipeScrap> recipeScraps = recipeScrapService.findByRecipeIds(recipes.getRecipeIds());

        Recipe lastRecipe = recipeRepository.findById(lastRecipeId).orElse(null);

        User anonymousUser = new User();

        return RecommendedRecipesResponse.from(recipes, recipePostUsers, recipeScraps, anonymousUser, expandedIngredientNames, lastRecipe, size);
    }
}
