package com.recipe.app.src.recipeIngredient;

import com.recipe.app.common.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class RecipeIngredientProvider {
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final JwtService jwtService;

    @Autowired
    public RecipeIngredientProvider(RecipeIngredientRepository recipeIngredientRepository, JwtService jwtService) {
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.jwtService = jwtService;
    }


}