package com.recipe.app.src.recipeIngredient;

import com.recipe.app.common.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/scraps/recipe")
public class RecipeIngredientController {
    private final RecipeIngredientProvider recipeIngredientProvider;
    private final RecipeIngredientService recipeIngredientService;
    private final JwtService jwtService;

    @Autowired
    public RecipeIngredientController(RecipeIngredientProvider recipeIngredientProvider, RecipeIngredientService recipeIngredientService, JwtService jwtService) {
        this.recipeIngredientProvider = recipeIngredientProvider;
        this.recipeIngredientService = recipeIngredientService;
        this.jwtService = jwtService;
    }




}