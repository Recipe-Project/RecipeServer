package com.recipe.app.src.recipeKeyword;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.src.recipeKeyword.models.RecipeKeyword;
import com.recipe.app.src.user.application.UserService;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.common.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RecipeKeywordService {
    private final UserService userService;
    private final RecipeKeywordRepository recipeKeywordRepository;
    private final JwtService jwtService;

    @Autowired
    public RecipeKeywordService(UserService userService, RecipeKeywordRepository recipeKeywordRepository, JwtService jwtService) {
        this.userService = userService;
        this.recipeKeywordRepository = recipeKeywordRepository;
        this.jwtService = jwtService;
    }


    /**
     * 레시피 검색어 저장 API
     * @param userIdx,keyword
     * @return void
     * @throws BaseException
     */
    public void createRecipeKeyword(String keyword) throws BaseException {
        recipeKeywordRepository.save(new RecipeKeyword(keyword));
    }


}