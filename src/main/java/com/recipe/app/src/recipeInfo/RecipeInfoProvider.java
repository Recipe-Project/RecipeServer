package com.recipe.app.src.recipeInfo;

import com.recipe.app.config.BaseException;
import com.recipe.app.src.recipeInfo.models.RecipeInfo;
import com.recipe.app.src.user.models.User;
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

    /**
     * Idx로 레시피 조회
     *
     * @param recipeId
     * @return User
     * @throws BaseException
     */
    public RecipeInfo retrieveRecipeByRecipeId(Integer recipeId) throws BaseException {
        RecipeInfo recipeInfo;
        try {
            recipeInfo = recipeInfoRepository.findById(recipeId).orElse(null);
        } catch (Exception ignored) {
            throw new BaseException(FAILE_TO_GET_RECIPE_INFO);
        }

        if (recipeInfo == null || !recipeInfo.getStatus().equals("ACTIVE")) {
            throw new BaseException(NOT_FOUND_RECIPE_INFO);
        }

        return recipeInfo;
    }
}