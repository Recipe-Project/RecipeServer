package com.recipe.app.src.user.application;

import com.recipe.app.src.fridge.application.FridgeService;
import com.recipe.app.src.fridgeBasket.application.FridgeBasketService;
import com.recipe.app.src.ingredient.application.IngredientService;
import com.recipe.app.src.recipe.application.RecipeService;
import com.recipe.app.src.recipe.application.blog.BlogScrapService;
import com.recipe.app.src.recipe.application.blog.BlogViewService;
import com.recipe.app.src.recipe.application.youtube.YoutubeScrapService;
import com.recipe.app.src.recipe.application.youtube.YoutubeViewService;
import com.recipe.app.src.recipe.domain.Recipe;
import com.recipe.app.src.user.application.dto.UserProfileResponse;
import com.recipe.app.src.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class UserFacadeService {

    private final UserService userService;
    private final RecipeService recipeService;
    private final YoutubeScrapService youtubeScrapService;
    private final YoutubeViewService youtubeViewService;
    private final BlogScrapService blogScrapService;
    private final BlogViewService blogViewService;
    private final FridgeService fridgeService;
    private final FridgeBasketService fridgeBasketService;
    private final IngredientService ingredientService;

    public UserFacadeService(UserService userService, RecipeService recipeService, YoutubeScrapService youtubeScrapService, YoutubeViewService youtubeViewService,
                             BlogScrapService blogScrapService, BlogViewService blogViewService, FridgeService fridgeService, FridgeBasketService fridgeBasketService, IngredientService ingredientService) {

        this.userService = userService;
        this.recipeService = recipeService;
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

        long youtubeScrapCnt = youtubeScrapService.countYoutubeScrapByUser(user);
        long blogScrapCnt = blogScrapService.countBlogScrapByUser(user);
        long recipeScrapCnt = recipeService.countRecipeScrapByUser(user);
        List<Recipe> userRecipes = recipeService.getRecipesByUser(user, 0, 6).toList();

        return UserProfileResponse.from(user, userRecipes, youtubeScrapCnt, blogScrapCnt, recipeScrapCnt);
    }

    @Transactional
    public void deleteUser(User user, HttpServletRequest request) {

        fridgeService.deleteFridgesByUser(user);
        fridgeBasketService.deleteFridgeBasketsByUser(user);
        recipeService.deleteRecipesByUser(user);
        ingredientService.deleteIngredientsByUser(user);
        youtubeScrapService.deleteYoutubeScrapsByUser(user);
        youtubeViewService.deleteYoutubeViewsByUser(user);
        blogScrapService.deleteBlogRecipeScrapsByUser(user);
        blogViewService.deleteBlogRecipeViewByUser(user);

        userService.deleteUser(user, request);
    }
}
