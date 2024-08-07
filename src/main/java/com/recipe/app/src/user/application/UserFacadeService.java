package com.recipe.app.src.user.application;

import com.recipe.app.src.fridge.application.FridgeService;
import com.recipe.app.src.fridgeBasket.application.FridgeBasketService;
import com.recipe.app.src.ingredient.application.IngredientService;
import com.recipe.app.src.recipe.application.RecipeScrapService;
import com.recipe.app.src.recipe.application.RecipeSearchService;
import com.recipe.app.src.recipe.application.RecipeService;
import com.recipe.app.src.recipe.application.RecipeViewService;
import com.recipe.app.src.recipe.application.blog.BlogScrapService;
import com.recipe.app.src.recipe.application.blog.BlogViewService;
import com.recipe.app.src.recipe.application.youtube.YoutubeScrapService;
import com.recipe.app.src.recipe.application.youtube.YoutubeViewService;
import com.recipe.app.src.recipe.domain.Recipe;
import com.recipe.app.src.user.application.dto.UserProfileResponse;
import com.recipe.app.src.user.domain.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserFacadeService {

    private final UserService userService;
    private final RecipeService recipeService;
    private final RecipeSearchService recipeSearchService;
    private final RecipeScrapService recipeScrapService;
    private final RecipeViewService recipeViewService;
    private final YoutubeScrapService youtubeScrapService;
    private final YoutubeViewService youtubeViewService;
    private final BlogScrapService blogScrapService;
    private final BlogViewService blogViewService;
    private final FridgeService fridgeService;
    private final FridgeBasketService fridgeBasketService;
    private final IngredientService ingredientService;

    public UserFacadeService(UserService userService, RecipeService recipeService, RecipeSearchService recipeSearchService,
                             RecipeScrapService recipeScrapService, RecipeViewService recipeViewService,
                             YoutubeScrapService youtubeScrapService, YoutubeViewService youtubeViewService,
                             BlogScrapService blogScrapService, BlogViewService blogViewService,
                             FridgeService fridgeService, FridgeBasketService fridgeBasketService, IngredientService ingredientService) {

        this.userService = userService;
        this.recipeService = recipeService;
        this.recipeSearchService = recipeSearchService;
        this.recipeScrapService = recipeScrapService;
        this.recipeViewService = recipeViewService;
        this.youtubeScrapService = youtubeScrapService;
        this.youtubeViewService = youtubeViewService;
        this.blogScrapService = blogScrapService;
        this.blogViewService = blogViewService;
        this.fridgeService = fridgeService;
        this.fridgeBasketService = fridgeBasketService;
        this.ingredientService = ingredientService;
    }

    @Transactional(readOnly = true)
    public UserProfileResponse findUserProfile(User user) {

        long youtubeScrapCnt = youtubeScrapService.countYoutubeScrapByUserId(user.getUserId());
        long blogScrapCnt = blogScrapService.countBlogScrapByUserId(user.getUserId());
        long recipeScrapCnt = recipeService.countRecipeScrapByUserId(user.getUserId());

        List<Recipe> userRecipes = recipeSearchService.findLimitByUserId(user.getUserId(), 0L, 6);

        return UserProfileResponse.from(user, userRecipes, youtubeScrapCnt, blogScrapCnt, recipeScrapCnt);
    }

    @Transactional
    public void deleteUser(User user, HttpServletRequest request) {

        fridgeService.deleteFridgesByUserId(user.getUserId());
        fridgeBasketService.deleteFridgeBasketsByUserId(user.getUserId());
        recipeScrapService.deleteAllByUserId(user.getUserId());
        recipeViewService.deleteAllByUserId(user.getUserId());
        recipeService.deleteRecipesByUserId(user.getUserId());
        ingredientService.deleteIngredientsByUserId(user.getUserId());
        youtubeScrapService.deleteYoutubeScrapsByUserId(user.getUserId());
        youtubeViewService.deleteYoutubeViewsByUserId(user.getUserId());
        blogScrapService.deleteBlogRecipeScrapsByUserId(user.getUserId());
        blogViewService.deleteBlogRecipeViewByUserId(user.getUserId());

        userService.deleteUser(user, request);
    }
}
