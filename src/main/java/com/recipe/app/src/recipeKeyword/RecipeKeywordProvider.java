package com.recipe.app.src.recipeKeyword;

import com.recipe.app.config.BaseException;
import com.recipe.app.src.recipeKeyword.models.GetRecipesBestKeywordRes;
import com.recipe.app.src.recipeKeyword.models.RecipeKeywordTmp;
import com.recipe.app.src.user.UserProvider;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


@Service
public class RecipeKeywordProvider {
    private final UserProvider userProvider;
    private final RecipeKeywordRepository recipeKeywordRepository;
    private final RecipeKeywordTmpRepository recipeKeywordTmpRepository;
    private final JwtService jwtService;

    @Autowired
    public RecipeKeywordProvider(UserProvider userProvider, RecipeKeywordRepository recipeKeywordRepository, RecipeKeywordTmpRepository recipeKeywordTmpRepository, JwtService jwtService) {
        this.userProvider = userProvider;
        this.recipeKeywordRepository = recipeKeywordRepository;
        this.recipeKeywordTmpRepository = recipeKeywordTmpRepository;
        this.jwtService = jwtService;
    }

    /**
     * 인기 검색어 조회 API
     *
     * @return List<GetRecipesBestKeywordRes>
     * @throws BaseException
     */
    public List<GetRecipesBestKeywordRes> retrieveRecipesBestKeyword() throws BaseException {

        Random random = new Random();
        List<RecipeKeywordTmp> recipeKeywords = new ArrayList<>();
        recipeKeywordTmpRepository.findAll().forEach(recipeKeywords::add);

        List<String> keywords = new ArrayList<>();

        int cnt = 0;
        while (keywords.size() < 10) {
            long seed = LocalDateTime.now().withMinute(cnt).withSecond(0).withNano(0).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            random.setSeed(seed);

            int idx = random.nextInt(recipeKeywords.size());
            String keyword = recipeKeywords.get(idx).getKeyword();
            if (!keywords.contains(keyword)) {
                keywords.add(keyword);
            }
            cnt++;
        }

        return keywords.stream()
                .map(GetRecipesBestKeywordRes::new)
                .collect(Collectors.toList());

//        List<Object[]> bestKeywordList;
//        try {
//            bestKeywordList = recipeKeywordRepository.findByBestKeywordTop10();
//        } catch (Exception ignored) {
//            throw new BaseException(FAILE_TO_GET_BEST_KEYWORD);
//        }
//
//        return bestKeywordList.stream().map(keyword -> {
//            String bestKeyword = String.valueOf(keyword[0].toString());
//
//            return new GetRecipesBestKeywordRes(bestKeyword);
//        }).collect(Collectors.toList());
    }

}