package com.recipe.app.src.recipeKeyword;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.src.recipeKeyword.models.GetRecipesBestKeywordRes;
import com.recipe.app.common.utils.JwtService;
import com.recipe.app.src.user.application.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecipeKeywordProvider {
    private final UserService userService;
    private final RecipeKeywordRepository recipeKeywordRepository;
    private final JwtService jwtService;

    @Autowired
    public RecipeKeywordProvider(UserService userService, RecipeKeywordRepository recipeKeywordRepository, JwtService jwtService) {
        this.userService = userService;
        this.recipeKeywordRepository = recipeKeywordRepository;
        this.jwtService = jwtService;
    }

    /**
     * 인기 검색어 조회 API
     * @return List<GetRecipesBestKeywordRes>
     * @throws BaseException
     */
    public List<GetRecipesBestKeywordRes> retrieveRecipesBestKeyword() throws BaseException {
        List<Object[]> bestKeywordList = recipeKeywordRepository.findByBestKeywordTop10();

        return bestKeywordList.stream().map(keyword -> {
            String bestKeyword = String.valueOf(keyword[0].toString());

            return new GetRecipesBestKeywordRes(bestKeyword);
        }).collect(Collectors.toList());
    }

}