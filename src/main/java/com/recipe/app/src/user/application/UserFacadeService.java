package com.recipe.app.src.user.application;

import com.recipe.app.src.fridge.application.FridgeService;
import com.recipe.app.src.fridgeBasket.application.FridgeBasketService;
import com.recipe.app.src.ingredient.application.IngredientService;
import com.recipe.app.src.recipe.application.BlogRecipeService;
import com.recipe.app.src.recipe.application.RecipeService;
import com.recipe.app.src.recipe.application.YoutubeRecipeService;
import com.recipe.app.src.recipe.domain.Recipe;
import com.recipe.app.src.user.application.dto.UserDto;
import com.recipe.app.src.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class UserFacadeService {

    private final UserService userService;
    private final RecipeService recipeService;
    private final YoutubeRecipeService youtubeRecipeService;
    private final BlogRecipeService blogRecipeService;
    private final FridgeService fridgeService;
    private final FridgeBasketService fridgeBasketService;
    private final IngredientService ingredientService;

    public UserFacadeService(UserService userService, RecipeService recipeService, YoutubeRecipeService youtubeRecipeService, BlogRecipeService blogRecipeService,
                             FridgeService fridgeService, FridgeBasketService fridgeBasketService, IngredientService ingredientService) {

        this.userService = userService;
        this.recipeService = recipeService;
        this.youtubeRecipeService = youtubeRecipeService;
        this.blogRecipeService = blogRecipeService;
        this.fridgeService = fridgeService;
        this.fridgeBasketService = fridgeBasketService;
        this.ingredientService = ingredientService;
    }

    @Transactional(readOnly = true)
    public UserDto.UserProfileResponse findUserProfile(User user) {

        long youtubeScrapCnt = youtubeRecipeService.countYoutubeScrapByUser(user);
        long blogScrapCnt = blogRecipeService.countBlogScrapByUser(user);
        long recipeScrapCnt = recipeService.countRecipeScrapByUser(user);
        List<Recipe> userRecipes = recipeService.getRecipesByUser(user, 0, 6).toList();

        return UserDto.UserProfileResponse.from(user, userRecipes, youtubeScrapCnt, blogScrapCnt, recipeScrapCnt);
    }

    @Transactional
    public void deleteUser(User user, HttpServletRequest request) {

        fridgeService.deleteFridgesByUser(user);
        fridgeBasketService.deleteFridgeBasketsByUser(user);
        recipeService.deleteRecipesByUser(user);
        ingredientService.deleteIngredientsByUser(user);
        youtubeRecipeService.deleteYoutubeScrapsByUser(user);
        youtubeRecipeService.deteteYoutubeViewsByUser(user);
        blogRecipeService.deleteBlogRecipeScrapsByUser(user);
        blogRecipeService.deleteBlogRecipeViewByUser(user);

        userService.deleteUser(user, request);
    }
}
