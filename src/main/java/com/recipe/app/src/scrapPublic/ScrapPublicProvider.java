package com.recipe.app.src.scrapPublic;

import com.recipe.app.src.recipe.domain.RecipeInfo;
import com.recipe.app.src.scrapPublic.models.GetScrapPublicsRes;
import com.recipe.app.src.scrapPublic.models.ScrapPublic;
import com.recipe.app.src.scrapPublic.models.ScrapPublicList;
import com.recipe.app.src.user.application.UserService;
import com.recipe.app.src.viewPublic.ViewPublicRepository;
import com.recipe.app.common.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.src.user.domain.User;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class ScrapPublicProvider {
    private final UserService userService;
    private final RecipeInfoProvider recipeInfoProvider;
    private final ViewPublicRepository viewPublicRepository;
    private final ScrapPublicRepository scrapPublicRepository;
    private final JwtService jwtService;

    @Autowired
    public ScrapPublicProvider(UserService userService, RecipeInfoProvider recipeInfoProvider, ViewPublicRepository viewPublicRepository, ScrapPublicRepository scrapPublicRepository, JwtService jwtService) {
        this.userService = userService;
        this.recipeInfoProvider = recipeInfoProvider;
        this.viewPublicRepository = viewPublicRepository;
        this.scrapPublicRepository = scrapPublicRepository;
        this.jwtService = jwtService;
    }

    /**
     * idx로 레시피 조회
     * @param recipeId,userIdx
     * @return ScrapPublic
     * @throws BaseException
     */
    public ScrapPublic retrieveScrapRecipe(int recipeId, int userIdx) throws BaseException {
        User user = userService.retrieveUserByUserIdx(userIdx);
        RecipeInfo recipeInfo = recipeInfoProvider.retrieveRecipeByRecipeId(recipeId);

        return scrapPublicRepository.findByUserAndRecipeInfoAndStatus(user,recipeInfo,"ACTIVE");
    }
    /**
     * 스크랩 레시피 조회 API
     * @param userIdx
     * @return GetScrapPublicsRes
     * @throws BaseException
     */
    public GetScrapPublicsRes retrieveScrapRecipes(Integer userIdx) throws BaseException {
        User user = userService.retrieveUserByUserIdx(userIdx);

        List<ScrapPublicList> scrapPublicList= retrieveScrapRecipesSortedByCreatedDate(userIdx);

        Long scrapCount = scrapPublicRepository.countByUserAndStatus(user,"ACTIVE");

        return new GetScrapPublicsRes(scrapCount,scrapPublicList);
    }

    /**
     * 스크랩 레시피 조회 - 최신순
     * @param userIdx
     * @return List<ScrapPublicList>
     * @throws BaseException
     */
    public List<ScrapPublicList> retrieveScrapRecipesSortedByCreatedDate(Integer userIdx) throws BaseException {

        User user = userService.retrieveUserByUserIdx(userIdx);
        List<ScrapPublic> scrapPublicList = scrapPublicRepository.findByUserAndStatus(user, "ACTIVE",Sort.by("createdAt").descending());

        return scrapPublicList.stream().map(scrapPublic -> {
            RecipeInfo recipeInfo = scrapPublic.getRecipeInfo();
            Integer recipeId = scrapPublic.getRecipeInfo().getRecipeId();
            String title = scrapPublic.getRecipeInfo().getRecipeNmKo();
            if (title.length()>30){
                title = title.substring(0,30)+"...";
            }
            String content = scrapPublic.getRecipeInfo().getSumry();
            if (content.length()>50){
                content = content.substring(0,50)+"...";
            }
            String thumbnail = scrapPublic.getRecipeInfo().getImgUrl();
            Long scrapCount = scrapPublicRepository.countByRecipeInfoAndStatus(recipeInfo,"ACTIVE");

            return new ScrapPublicList(recipeId,title,content,thumbnail,scrapCount);

        }).collect(Collectors.toList());
    }


//    /**
//     * 스크랩 레시피 조회 API
//     * @param userIdx,pageable
//     * @return ScrapPublic
//     * @throws BaseException
//     */
//    public GetScrapPublicsRes retrieveScrapRecipes(Integer userIdx, Integer sort) throws BaseException {
//
//        User user = userProvider.retrieveUserByUserIdx(userIdx);
//
//        List<ScrapPublicList> scrapPublicList = null;
//        try {
//
//            if (sort==null || sort==1){
//                scrapPublicList = retrieveScrapRecipesSortedByViewCount(userIdx);
//            }
//            else if(sort==2){
//                scrapPublicList= retrieveScrapRecipesSortedByCreatedDate(userIdx);
//            }
//            else if(sort==3){
//                scrapPublicList= retrieveScrapRecipesSortedByScrapCount(userIdx);
//            }
//
//
//
//            Long scrapCount = scrapPublicRepository.countByUserAndStatus(user,"ACTIVE");
//
//            return new GetScrapPublicsRes(scrapCount,scrapPublicList);
//        } catch (Exception ignored) {
//            throw new BaseException(FAILED_TO_GET_SCRAP_PUBLIC);
//        }
//    }

//    /**
//     * 스크랩 레시피 조회 - 조회순
//     * @param userIdx
//     * @return List<ScrapPublicList>
//     * @throws BaseException
//     */
//    public List<ScrapPublicList> retrieveScrapRecipesSortedByViewCount(Integer userIdx) throws BaseException {
//
//        User user = userProvider.retrieveUserByUserIdx(userIdx);
//
//
//        // 유저가 스크랩한 레시피 리스트 조회
//        List<ScrapPublic> scrapPublicList;
//        try {
//            scrapPublicList = scrapPublicRepository.findByUserAndStatus(user, "ACTIVE");
//        } catch (Exception ignored) {
//            throw new BaseException(FAILED_TO_GET_SCRAP_PUBLIC);
//        }
//
//        // 레시피 스크랩개수와 레시피 아이디와 매핑
//        Map<RecipeInfo,Long> map = new HashMap<RecipeInfo,Long>();
//        Long count;
//        for(ScrapPublic sc : scrapPublicList) {
//            RecipeInfo recipeInfo =  sc.getRecipeInfo();
//            count = viewPublicRepository.countByRecipeInfoAndStatus(recipeInfo,"ACTIVE");
//            Integer recipeId = sc.getRecipeInfo().getRecipeId();
//            map.put(recipeInfo,count);
//        }
//
//        // 매핑->리스트로
//        List<RecipeInfo> keySetList = new ArrayList<>(map.keySet());
//        // 내림차순
//        Collections.sort(keySetList, (o1, o2) -> (map.get(o2).compareTo(map.get(o1))));
//        // 결과 keySetList
//        List<ScrapPublicList> spList = new ArrayList<>();
//        ScrapPublic scrapPublic;
//        for(RecipeInfo recipeInfo : keySetList) {
//            scrapPublic = scrapPublicRepository.findByUserAndRecipeInfoAndStatus(user,recipeInfo,"ACTIVE");
//            Integer recipeId = scrapPublic.getRecipeInfo().getRecipeId();
//            String title = scrapPublic.getRecipeInfo().getRecipeNmKo();
//            String content = scrapPublic.getRecipeInfo().getSumry();
//            String thumbnail = scrapPublic.getRecipeInfo().getImgUrl();
//            Long scrapCount = map.get(recipeInfo);
//
//            ScrapPublicList sp = new ScrapPublicList(recipeId,title,content,thumbnail,scrapCount);
//            spList.add(sp);
//        }
//        return spList;
//    }
//



//
//    /**
//     * 스크랩 레시피 조회 - 좋아요순
//     * @param userIdx
//     * @return List<scrapPublicList>
//     * @throws BaseException
//     */
//    public List<ScrapPublicList> retrieveScrapRecipesSortedByScrapCount(Integer userIdx) throws BaseException {
//
//        User user = userProvider.retrieveUserByUserIdx(userIdx);
//
//
//        List<ScrapPublic> scrapPublicList;
//        try {
//            scrapPublicList = scrapPublicRepository.findByUserAndStatus(user, "ACTIVE");
//        } catch (Exception ignored) {
//            throw new BaseException(FAILED_TO_GET_SCRAP_PUBLIC);
//        }
//
//        // 레시피 스크랩개수 매핑
//        Map<RecipeInfo,Long> map = new HashMap<RecipeInfo,Long>();
//        Long count;
//        for(ScrapPublic sc : scrapPublicList) {
//            RecipeInfo recipeInfo =  sc.getRecipeInfo();
//            count = scrapPublicRepository.countByRecipeInfoAndStatus(recipeInfo,"ACTIVE");
//            Integer recipeId = sc.getRecipeInfo().getRecipeId();
//            map.put(recipeInfo,count);
//        }
//
//        // 매핑->리스트로
//        List<RecipeInfo> keySetList = new ArrayList<>(map.keySet());
//        // 내림차순
//        Collections.sort(keySetList, (o1, o2) -> (map.get(o2).compareTo(map.get(o1))));
//        // 결과 keySetList
//        List<ScrapPublicList> spList = new ArrayList<>();
//        ScrapPublic scrapPublic;
//        for(RecipeInfo recipeInfo : keySetList) {
//            scrapPublic = scrapPublicRepository.findByUserAndRecipeInfoAndStatus(user,recipeInfo,"ACTIVE");
//            Integer recipeId = scrapPublic.getRecipeInfo().getRecipeId();
//            String title = scrapPublic.getRecipeInfo().getRecipeNmKo();
//            String content = scrapPublic.getRecipeInfo().getSumry();
//            String thumbnail = scrapPublic.getRecipeInfo().getImgUrl();
//            Long scrapCount = map.get(recipeInfo);
//
//            ScrapPublicList sp = new ScrapPublicList(recipeId,title,content,thumbnail,scrapCount);
//            spList.add(sp);
//        }
//        return spList;
//    }



}
