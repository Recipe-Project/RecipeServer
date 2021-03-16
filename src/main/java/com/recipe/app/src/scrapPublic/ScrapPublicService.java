package com.recipe.app.src.scrapPublic;

import com.recipe.app.src.recipeInfo.RecipeInfoProvider;
import com.recipe.app.src.recipeInfo.models.RecipeInfo;
import com.recipe.app.src.scrapPublic.models.PostScrapPublicRes;
import com.recipe.app.src.scrapPublic.models.ScrapPublic;
import com.recipe.app.src.user.UserProvider;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;

import com.recipe.app.config.BaseException;
import com.recipe.app.src.user.models.User;
import org.springframework.stereotype.Service;


import static com.recipe.app.config.BaseResponseStatus.*;


@Service
public class ScrapPublicService {
    private final UserProvider userProvider;
    private final RecipeInfoProvider recipeInfoProvider;
    private final ScrapPublicRepository scrapPublicRepository;
    private final ScrapPublicProvider scrapPublicProvider;
    private final JwtService jwtService;

    @Autowired
    public ScrapPublicService(UserProvider userProvider, RecipeInfoProvider recipeInfoProvider, ScrapPublicRepository scrapPublicRepository, ScrapPublicProvider scrapPublicProvider, JwtService jwtService) {
        this.userProvider = userProvider;
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
        User user = userProvider.retrieveUserByUserIdx(userIdx);
        RecipeInfo recipeInfo = recipeInfoProvider.retrieveRecipeByRecipeId(recipeId);

        ScrapPublic scrapPublic = new ScrapPublic(user, recipeInfo);
        try {
            scrapPublic = scrapPublicRepository.save(scrapPublic);
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_POST_CREATE_SCRAP_PUBLIC);
        }
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
        try {
            scrapPublicRepository.save(scrapPublic);
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_POST_DELETE_SCRAP_PUBLIC);
        }
        return new PostScrapPublicRes(recipeId, userIdx);
    }
}
