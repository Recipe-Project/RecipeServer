package com.recipe.app.src.fridge;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.src.fridge.models.*;
import com.recipe.app.src.fridgeBasket.FridgeBasketRepository;
import com.recipe.app.src.ingredientCategory.IngredientCategoryRepository;
import com.recipe.app.src.ingredientCategory.models.IngredientCategory;
import com.recipe.app.src.recipeInfo.RecipeInfoRepository;
import com.recipe.app.src.recipeInfo.models.RecipeInfo;
import com.recipe.app.src.scrapPublic.ScrapPublicRepository;
import com.recipe.app.src.scrapPublic.models.ScrapPublicInfo;
import com.recipe.app.src.user.mapper.UserRepository;
import com.recipe.app.src.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FridgeProvider {
    private final UserProvider userProvider;
    private final FridgeRepository fridgeRepository;
    private final IngredientCategoryRepository ingredientCategoryRepository;
    private final RecipeInfoRepository recipeInfoRepository;
    private final ScrapPublicRepository scrapPublicRepository;
    private final FridgeBasketRepository fridgeBasketRepository;
    private final UserRepository userRepository;

    @Autowired
    public FridgeProvider(UserProvider userProvider, FridgeRepository fridgeRepository, IngredientCategoryRepository ingredientCategoryRepository, RecipeInfoRepository recipeInfoRepository, ScrapPublicRepository scrapPublicRepository, FridgeBasketRepository fridgeBasketRepository, UserRepository userRepository) {
        this.userProvider = userProvider;
        this.fridgeRepository = fridgeRepository;
        this.ingredientCategoryRepository = ingredientCategoryRepository;
        this.recipeInfoRepository = recipeInfoRepository;
        this.scrapPublicRepository = scrapPublicRepository;
        this.fridgeBasketRepository = fridgeBasketRepository;
        this.userRepository = userRepository;
    }

    /**
     * 냉장고 조회 API
     * @param userIdx
     * @return GetFridgesRes
     * @throws BaseException
     */
    public GetFridgesRes retreiveFridges(int userIdx) throws BaseException {
        User user = userProvider.retrieveUserByUserIdx(userIdx);
        long fridgeBasketCount = fridgeBasketRepository.countByUserAndStatus(user,"ACTIVE");
        List<IngredientCategory> ingredientCategories = ingredientCategoryRepository.findByStatus("ACTIVE");

        List<Fridges> fridges = new ArrayList<>();
        for (IngredientCategory ingredientCategory : ingredientCategories){
            int ingredientCategoryIdx = ingredientCategory.getIngredientCategoryIdx();
            String ingredientCategoryName = ingredientCategory.getName();

            List<IngredientList> ingredientList = retreiveFridgeList(ingredientCategory, user);

            Fridges fridge = new Fridges(ingredientCategoryIdx, ingredientCategoryName, ingredientList);
            fridges.add(fridge);
        }
        return new GetFridgesRes(fridgeBasketCount,fridges);
    }


    /**
     * 카테고리에 해당하는 냉장고 재료 리스트 추출
     *
     * @param ingredientCategory,userIdx
     * @return List<IngredientList>
     * @throws BaseException
     */
    public List<IngredientList> retreiveFridgeList(IngredientCategory ingredientCategory, User user) throws BaseException {
        List<Fridge> fridgeList = fridgeRepository.findByUserAndIngredientCategoryAndStatus(user,ingredientCategory,"ACTIVE");

        return fridgeList.stream().map(fl -> {
            String ingredientName = fl.getIngredientName();
            String ingredientIcon = fl.getIngredientIcon();
            String storageMethod = fl.getStorageMethod();
            Integer count =fl.getCount();

            Date expiredDate = fl.getExpiredAt();
            String expiredAt = null;
            Integer freshness = null;
            if(expiredDate == null) {
                freshness = 555;
            }
            else {
                SimpleDateFormat sdf = new SimpleDateFormat("yy.MM.dd");
                expiredAt = sdf.format(expiredDate) + "까지";

                long diffDay = 0;
                try{
                    Date nowDate = sdf.parse(sdf.format(new Date()));
                    //두날짜 사이의 시간 차이(ms)를 하루 동안의 ms(24시*60분*60초*1000밀리초) 로 나눈다.
                    diffDay = (expiredDate.getTime() - nowDate.getTime()) / (24*60*60*1000);
                }catch(ParseException e){
                    e.printStackTrace();
                }

                if (diffDay<0){
                    freshness=444;
                }
                else if(diffDay<=3){
                    freshness=1;
                }
                else if( diffDay <=7){
                    freshness=2;
                }
                else{
                    freshness=3;
                }
            }
            return new IngredientList(ingredientName, ingredientIcon, expiredAt, storageMethod, count, freshness);
        }).collect(Collectors.toList());

    }


    /**
     * 재료명 존재여부
     * @param ingredientNameList,userIdx
     * @return List<Fridge>
     * @throws BaseException
     */
    public List<Fridge> getExistIngredients(List<String> ingredientNameList, User user) throws BaseException {
        return fridgeRepository.findAllByUserAndStatusAndIngredientNameIn(user,"ACTIVE", ingredientNameList);
    }


    /**
     * 냉장고 파먹기 조회 API
     * @param userIdx
     * @return GetFridgesRecipeRes
     * @throws BaseException
     */
    public GetFridgesRecipeRes retreiveFridgesRecipe(int userIdx, Integer start, Integer display) throws BaseException {
        User user = userProvider.retrieveUserByUserIdx(userIdx);

        List<RecipeInfo> recipeInfo = recipeInfoRepository.searchRecipeListOrderByIngredientCntWhichUserHasDesc(user, "ACTIVE");

        List<Integer> recipeIdList = recipeInfo.stream().map(RecipeInfo::getRecipeId).collect(Collectors.toList());
        Map<Integer, ScrapPublicInfo> scrapCountMap = scrapPublicRepository.findScrapCountStatusAndRecipeInfoIn("ACTIVE", recipeIdList)
                .stream().collect(Collectors.toMap(ScrapPublicInfo::getRecipeId, v -> v));;

        List<RecipeList> recipeList = new ArrayList<>();
        for(int i = start; i < start + display && i < recipeInfo.size(); i++) {
            Integer recipeId = recipeInfo.get(i).getRecipeId();
            String title = recipeInfo.get(i).getRecipeNmKo();
            String content = recipeInfo.get(i).getSumry();
            String thumbnail = recipeInfo.get(i).getImgUrl();
            String cookingTime = recipeInfo.get(i).getCookingTime();
            ScrapPublicInfo scrapPublicInfo = scrapCountMap.get(recipeId);
            long scrapCount = scrapPublicInfo == null ? 0 : scrapPublicInfo.getScrapCount();
            recipeList.add(new RecipeList(recipeId, title, content, thumbnail, cookingTime, scrapCount));
        }
        return new GetFridgesRecipeRes(recipeInfo.size(), recipeList);
    }


    /**
     * 유통기한 3일 남은 재료를 소유한 유저들 리스트 조회
     * @return shelfLifeUsers
     * @throws BaseException
     */
    public List<ShelfLifeUser> retreiveShelfLifeUserList() throws BaseException {
        SimpleDateFormat sdFormat = new SimpleDateFormat("yy.MM.dd");
        String today = sdFormat.format(new Date());

        List<Fridge> fridgeList = fridgeRepository.findAllByStatusAnd3DaysBeforeExpiredAt("ACTIVE", today);

        List<ShelfLifeUser> shelfLifeUsers = new ArrayList<>();
        for(Fridge fridge : fridgeList) {
            String deviceToken = fridge.getUser().getDeviceToken();
            String ingredientName = fridge.getIngredientName();
            ShelfLifeUser shelfLifeUser = new ShelfLifeUser(deviceToken, ingredientName);
            shelfLifeUsers.add(shelfLifeUser);
        }
        return shelfLifeUsers;
    }
}
