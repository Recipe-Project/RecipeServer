package com.recipe.app.src.recipeKeyword;

import com.recipe.app.config.BaseException;
import com.recipe.app.src.recipeKeyword.models.GetRecipesBestKeywordRes;
import com.recipe.app.src.user.UserProvider;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.recipe.app.config.BaseResponseStatus.FAILE_TO_GET_BEST_KEYWORD;


@Service
public class RecipeKeywordProvider {
    private final UserProvider userProvider;
    private final RecipeKeywordRepository recipeKeywordRepository;
    private final JwtService jwtService;

    @Autowired
    public RecipeKeywordProvider(UserProvider userProvider, RecipeKeywordRepository recipeKeywordRepository, JwtService jwtService) {
        this.userProvider = userProvider;
        this.recipeKeywordRepository = recipeKeywordRepository;
        this.jwtService = jwtService;
    }

    /**
     * 인기 검색어 조회 API
     * @return List<GetRecipesBestKeywordRes>
     * @throws BaseException
     */
    public List<GetRecipesBestKeywordRes> retrieveRecipesBestKeyword() throws BaseException {
        GetRecipesBestKeywordRes getRecipesBestKeywordRes;

        List<Object[]> bestKeywordList;
        try {
            bestKeywordList = recipeKeywordRepository.findByBestKeywordTop10();
        } catch (Exception ignored) {
            throw new BaseException(FAILE_TO_GET_BEST_KEYWORD);
        }

        return bestKeywordList.stream().map(keyword -> {
            String bestKeyword = String.valueOf(keyword[0].toString());

            return new GetRecipesBestKeywordRes(bestKeyword);
        }).collect(Collectors.toList());


    }

}