package com.recipe.app.src.recipeKeyword;

import com.recipe.app.config.BaseException;
import com.recipe.app.src.recipeKeyword.models.RecipeKeyword;
import com.recipe.app.src.user.UserProvider;
import com.recipe.app.src.user.models.User;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.recipe.app.config.BaseResponseStatus.FAILED_TO_POST_RECIPE_KEYWORD;
@Service
public class RecipeKeywordService {
    private final UserProvider userProvider;
    private final RecipeKeywordRepository recipeKeywordRepository;
    private final JwtService jwtService;

    @Autowired
    public RecipeKeywordService(UserProvider userProvider, RecipeKeywordRepository recipeKeywordRepository, JwtService jwtService) {
        this.userProvider = userProvider;
        this.recipeKeywordRepository = recipeKeywordRepository;
        this.jwtService = jwtService;
    }


    /**
     * 레시피 검색어 저장 API
     * @param userIdx,keyword
     * @return void
     * @throws BaseException
     */
    public void createRecipeKeyword(int userIdx, String keyword) throws BaseException {
        User user = userProvider.retrieveUserByUserIdx(userIdx);

        try {
            RecipeKeyword keywordYoutube = new RecipeKeyword(keyword, userIdx);
            keywordYoutube = recipeKeywordRepository.save(keywordYoutube);

        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_POST_RECIPE_KEYWORD);
        }

    }


}