package com.recipe.app.src.ingredient.application;

import com.recipe.app.src.fridge.application.FridgeService;
import com.recipe.app.src.fridgeBasket.application.FridgeBasketService;
import com.recipe.app.src.ingredient.application.dto.IngredientsResponse;
import com.recipe.app.src.ingredient.domain.Ingredient;
import com.recipe.app.src.ingredient.domain.IngredientCategory;
import com.recipe.app.src.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IngredientFacadeService {

    private final IngredientService ingredientService;
    private final IngredientCategoryService ingredientCategoryService;
    private final FridgeBasketService fridgeBasketService;
    private final FridgeService fridgeService;

    public IngredientFacadeService(IngredientService ingredientService, IngredientCategoryService ingredientCategoryService,
                                   FridgeBasketService fridgeBasketService, FridgeService fridgeService) {

        this.ingredientService = ingredientService;
        this.ingredientCategoryService = ingredientCategoryService;
        this.fridgeBasketService = fridgeBasketService;
        this.fridgeService = fridgeService;
    }

    @Transactional(readOnly = true)
    public IngredientsResponse findIngredientsByKeyword(User user, String keyword) {

        long fridgeBasketCount = fridgeBasketService.countByUserId(user.getUserId());
        List<IngredientCategory> categories = ingredientCategoryService.findAll();
        List<Ingredient> ingredients = ingredientService.findByKeyword(user.getUserId(), keyword);

        return IngredientsResponse.from(fridgeBasketCount, categories, ingredients);
    }

    @Transactional(readOnly = true)
    public IngredientsResponse findMyIngredients(User user) {

        long fridgeBasketCount = fridgeBasketService.countByUserId(user.getUserId());
        List<IngredientCategory> categories = ingredientCategoryService.findAll();
        List<Ingredient> ingredients = ingredientService.findByUserId(user.getUserId());

        return IngredientsResponse.from(fridgeBasketCount, categories, ingredients);
    }

    @Transactional
    public void deleteMyIngredient(User user, Long ingredientId) {

        fridgeService.deleteByUserIdAndIngredientId(user.getUserId(), ingredientId);
        fridgeBasketService.deleteByUserIdAndIngredientId(user.getUserId(), ingredientId);
        ingredientService.deleteIngredient(user, ingredientId);
    }
}
