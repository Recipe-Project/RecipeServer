package com.recipe.app.src.recipeInfo;

import com.recipe.app.common.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class RecipeInfoService {
    private final RecipeInfoRepository recipeInfoRepository;
    private final RecipeInfoProvider recipeInfoProvider;
    private final JwtService jwtService;

    @Autowired
    public RecipeInfoService(RecipeInfoRepository recipeInfoRepository, RecipeInfoProvider recipeInfoProvider, JwtService jwtService) {
        this.recipeInfoRepository = recipeInfoRepository;
        this.recipeInfoProvider = recipeInfoProvider;
        this.jwtService = jwtService;
    }



}

