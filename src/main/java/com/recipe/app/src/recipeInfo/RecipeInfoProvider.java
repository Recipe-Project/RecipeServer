package com.recipe.app.src.recipeInfo;

import com.recipe.app.config.BaseException;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.recipe.app.config.BaseResponseStatus.*;

@Service
public class RecipeInfoProvider {
    private final RecipeInfoRepository recipeInfoRepository;
    private final JwtService jwtService;

    @Autowired
    public RecipeInfoProvider(RecipeInfoRepository recipeInfoRepository, JwtService jwtService) {
        this.recipeInfoRepository = recipeInfoRepository;
        this.jwtService = jwtService;
    }


}