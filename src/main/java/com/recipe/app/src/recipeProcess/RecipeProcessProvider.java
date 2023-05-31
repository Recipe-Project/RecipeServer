package com.recipe.app.src.recipeProcess;

import com.recipe.app.common.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class RecipeProcessProvider {
    private final RecipeProcessRepository recipeProcessRepository;
    private final JwtService jwtService;

    @Autowired
    public RecipeProcessProvider(RecipeProcessRepository recipeProcessRepository, JwtService jwtService) {
        this.recipeProcessRepository = recipeProcessRepository;
        this.jwtService = jwtService;
    }


}