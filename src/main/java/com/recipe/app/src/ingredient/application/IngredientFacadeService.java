package com.recipe.app.src.ingredient.application;

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

    public IngredientFacadeService(IngredientService ingredientService, IngredientCategoryService ingredientCategoryService, FridgeBasketService fridgeBasketService) {

        this.ingredientService = ingredientService;
        this.ingredientCategoryService = ingredientCategoryService;
        this.fridgeBasketService = fridgeBasketService;
    }

    @Transactional(readOnly = true)
    public IngredientsResponse findIngredientsByKeyword(User user, String keyword) {

        long fridgeBasketCount = fridgeBasketService.countByUserId(user.getUserId());
        List<IngredientCategory> categories = ingredientCategoryService.findAll();
        List<Ingredient> ingredients = ingredientService.findByKeyword(user.getUserId(), keyword);

        return IngredientsResponse.from(fridgeBasketCount, categories, ingredients);
    }
}
