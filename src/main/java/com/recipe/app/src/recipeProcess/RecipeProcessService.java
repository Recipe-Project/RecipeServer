package com.recipe.app.src.recipeProcess;

import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class RecipeProcessService {
    private final RecipeProcessRepository recipeProcessRepository;
    private final RecipeProcessProvider recipeProcessProvider;
    private final JwtService jwtService;

    @Autowired
    public RecipeProcessService(RecipeProcessRepository recipeProcessRepository, RecipeProcessProvider recipeProcessProvider, JwtService jwtService) {
        this.recipeProcessRepository = recipeProcessRepository;
        this.recipeProcessProvider = recipeProcessProvider;
        this.jwtService = jwtService;
    }

}