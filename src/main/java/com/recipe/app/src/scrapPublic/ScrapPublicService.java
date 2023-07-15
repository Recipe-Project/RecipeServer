package com.recipe.app.src.scrapPublic;

import com.recipe.app.src.recipe.domain.RecipeInfo;
import com.recipe.app.src.scrapPublic.models.PostScrapPublicRes;
import com.recipe.app.src.scrapPublic.models.ScrapPublic;
import com.recipe.app.common.utils.JwtService;
import com.recipe.app.src.user.application.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.src.user.domain.User;
import org.springframework.stereotype.Service;


@Service
public class ScrapPublicService {
    private final UserService userService;
    private final RecipeInfoProvider recipeInfoProvider;
    private final ScrapPublicRepository scrapPublicRepository;
    private final ScrapPublicProvider scrapPublicProvider;
    private final JwtService jwtService;

    @Autowired
    public ScrapPublicService(UserService userService, RecipeInfoProvider recipeInfoProvider, ScrapPublicRepository scrapPublicRepository, ScrapPublicProvider scrapPublicProvider, JwtService jwtService) {
        this.userService = userService;
        this.recipeInfoProvider = recipeInfoProvider;
        this.scrapPublicRepository = scrapPublicRepository;
        this.scrapPublicProvider = scrapPublicProvider;
        this.jwtService = jwtService;
    }

    /**
     * 레시피 스크랩 생성
     * @param recipeId,userIdx
     * @return PostScrapPublicRes
     * @throws BaseException
     */
    public PostScrapPublicRes createScrapRecipe(int recipeId, int userIdx) throws BaseException {
        User user = userService.retrieveUserByUserIdx(userIdx);
        RecipeInfo recipeInfo = recipeInfoProvider.retrieveRecipeByRecipeId(recipeId);
        scrapPublicRepository.save(new ScrapPublic(user, recipeInfo));

        return new PostScrapPublicRes(recipeId, userIdx);

    }

    /**
     * 레시피 스크랩 취소
     * @param recipeId,userIdx
     * @return PostScrapPublicRes
     * @throws BaseException
     */
    public PostScrapPublicRes deleteScrapRecipe(int recipeId, int userIdx) throws BaseException {
        ScrapPublic scrapPublic = scrapPublicProvider.retrieveScrapRecipe(recipeId, userIdx);
        scrapPublic.setStatus("INACTIVE");
        scrapPublicRepository.save(scrapPublic);
        return new PostScrapPublicRes(recipeId, userIdx);
    }
}
