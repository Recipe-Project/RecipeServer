package com.recipe.app.src.recipeInfo;



import com.recipe.app.config.BaseException;
import com.recipe.app.config.BaseResponse;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.recipe.app.config.BaseResponseStatus.*;


@RestController
@RequestMapping("/scraps/recipe")
public class RecipeInfoController {
    private final RecipeInfoProvider recipeInfoProvider;
    private final RecipeInfoService recipeInfoService;
    private final JwtService jwtService;

    @Autowired
    public RecipeInfoController(RecipeInfoProvider recipeInfoProvider,RecipeInfoService recipeInfoService, JwtService jwtService) {
        this.recipeInfoProvider = recipeInfoProvider;
        this.recipeInfoService = recipeInfoService;
        this.jwtService = jwtService;
    }



}