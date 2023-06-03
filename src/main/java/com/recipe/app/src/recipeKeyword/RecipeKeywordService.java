package com.recipe.app.src.recipeKeyword;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.src.recipeKeyword.models.RecipeKeyword;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.common.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        recipeKeywordRepository.save(new RecipeKeyword(keyword));
    }


}