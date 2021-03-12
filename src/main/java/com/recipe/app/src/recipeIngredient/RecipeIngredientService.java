package com.recipe.app.src.recipeIngredient;

import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class RecipeIngredientService {
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final RecipeIngredientProvider recipeIngredientProvider;
    private final JwtService jwtService;

    @Autowired
    public RecipeIngredientService(RecipeIngredientRepository recipeIngredientRepository, RecipeIngredientProvider recipeIngredientProvider, JwtService jwtService) {
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.recipeIngredientProvider = recipeIngredientProvider;
        this.jwtService = jwtService;
    }

}