package com.recipe.app.src.ingredient;

import com.recipe.app.src.fridgeBasket.mapper.FridgeBasketRepository;
import com.recipe.app.src.ingredientCategory.IngredientCategoryProvider;
import com.recipe.app.src.ingredientCategory.IngredientCategoryRepository;
import com.recipe.app.common.utils.JwtService;
import com.recipe.app.src.user.application.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IngredientService {
    private final UserService userService;
    private final IngredientCategoryProvider ingredientCategoryProvider;
    private final IngredientRepository ingredientRepository;
    private final IngredientCategoryRepository ingredientCategoryRepository;
    private final FridgeBasketRepository fridgeBasketRepository;
    private final JwtService jwtService;

    @Autowired
    public IngredientService(UserService userService, IngredientCategoryProvider ingredientCategoryProvider, IngredientRepository ingredientRepository, IngredientCategoryRepository ingredientCategoryRepository, FridgeBasketRepository fridgeBasketRepository, JwtService jwtService) {
        this.userService = userService;
        this.ingredientCategoryProvider = ingredientCategoryProvider;
        this.ingredientRepository = ingredientRepository;
        this.ingredientCategoryRepository = ingredientCategoryRepository;
        this.fridgeBasketRepository = fridgeBasketRepository;
        this.jwtService = jwtService;
    }


}
