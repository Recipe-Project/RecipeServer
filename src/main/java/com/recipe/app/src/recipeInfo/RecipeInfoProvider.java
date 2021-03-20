package com.recipe.app.src.recipeInfo;

import com.recipe.app.config.BaseException;
import com.recipe.app.src.recipeInfo.models.*;
import com.recipe.app.src.scrapPublic.ScrapPublicRepository;
import com.recipe.app.src.user.UserProvider;
import com.recipe.app.src.user.models.User;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.recipe.app.config.BaseResponseStatus.*;

@Service
public class RecipeInfoProvider {
    private final ScrapPublicRepository scrapPublicRepository;
    private final UserProvider userProvider;
    private final RecipeInfoRepository recipeInfoRepository;
    private final JwtService jwtService;

    @Autowired
    public RecipeInfoProvider(ScrapPublicRepository scrapPublicRepository, UserProvider userProvider, RecipeInfoRepository recipeInfoRepository, JwtService jwtService) {
        this.scrapPublicRepository = scrapPublicRepository;
        this.userProvider = userProvider;
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

    /**
     * 레시피 검색
     * @param jwtUserIdx, keyword ,pageable
     * @return List<GetRecipeInfosRes>
     * @throws BaseException
     */

    public List<GetRecipeInfosRes> retrieveRecipeInfos(Integer jwtUserIdx, String keyword, Pageable pageable) throws BaseException {
        User user = userProvider.retrieveUserByUserIdx(jwtUserIdx);
        List<RecipeInfo> recipeInfoList;
        try {
            recipeInfoList= recipeInfoRepository.searchRecipeInfos(keyword, "ACTIVE", pageable);
        } catch (Exception ignored) {
            throw new BaseException(DATABASE_ERROR);
        }

        List<GetRecipeInfosRes> getRecipeInfosResList = new ArrayList<>();
        for(int i=0;i<recipeInfoList.size();i++){
            RecipeInfo recipeInfo = recipeInfoList.get(i);
            Integer recipeId = recipeInfo.getRecipeId();
            String title = recipeInfo.getRecipeNmKo();
            String description = recipeInfo.getSumry();
            String thumbnail = recipeInfo.getImgUrl();
            String userScrapYN = "N";
            for(int j=0;j<user.getScrapPublics().size();j++){
                if(user.getScrapPublics().get(j).getRecipeInfo().getRecipeId()==recipeId && user.getScrapPublics().get(j).getStatus().equals("ACTIVE")){
                    userScrapYN = "Y";
                }
            }

            Integer userScrapCnt = 0;
            try{
                userScrapCnt = Math.toIntExact(scrapPublicRepository.countByRecipeInfoAndStatus(recipeInfo, "ACTIVE"));
            }catch (Exception e){
                throw new BaseException(DATABASE_ERROR);
            }

            getRecipeInfosResList.add(new GetRecipeInfosRes(recipeId, title, description, thumbnail, userScrapYN, userScrapCnt));

        }

        return getRecipeInfosResList;

    }


}