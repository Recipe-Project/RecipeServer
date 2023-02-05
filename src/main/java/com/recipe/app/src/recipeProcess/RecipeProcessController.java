package com.recipe.app.src.recipeProcess;

import com.recipe.app.common.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/scraps/recipe")
public class RecipeProcessController {
    private final RecipeProcessProvider recipeProcessProvider;
    private final RecipeProcessService recipeProcessService;
    private final JwtService jwtService;

    @Autowired
    public RecipeProcessController(RecipeProcessProvider recipeProcessProvider, RecipeProcessService recipeProcessService, JwtService jwtService) {
        this.recipeProcessProvider = recipeProcessProvider;
        this.recipeProcessService = recipeProcessService;
        this.jwtService = jwtService;
    }




}