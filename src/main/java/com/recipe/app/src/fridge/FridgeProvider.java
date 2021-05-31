package com.recipe.app.src.fridge;


import com.recipe.app.config.BaseException;
import com.recipe.app.src.fridge.models.*;
import com.recipe.app.src.fridgeBasket.FridgeBasketRepository;
import com.recipe.app.src.ingredientCategory.IngredientCategoryProvider;
import com.recipe.app.src.ingredientCategory.IngredientCategoryRepository;
import com.recipe.app.src.ingredientCategory.models.IngredientCategory;
import com.recipe.app.src.recipeInfo.RecipeInfoProvider;
import com.recipe.app.src.recipeInfo.RecipeInfoRepository;
import com.recipe.app.src.recipeInfo.models.RecipeInfo;
import com.recipe.app.src.recipeIngredient.RecipeIngredientRepository;
import com.recipe.app.src.recipeIngredient.models.RecipeIngredient;
import com.recipe.app.src.scrapPublic.ScrapPublicRepository;
import com.recipe.app.src.user.UserProvider;
import com.recipe.app.src.user.UserRepository;
import com.recipe.app.src.user.models.User;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.recipe.app.config.BaseResponseStatus.*;

@Service
public class FridgeProvider {
    private final UserProvider userProvider;
    private final FridgeRepository fridgeRepository;
    private final IngredientCategoryProvider ingredientCategoryProvider;
    private final IngredientCategoryRepository ingredientCategoryRepository;
    private final RecipeInfoRepository recipeInfoRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final ScrapPublicRepository scrapPublicRepository;
    private final RecipeInfoProvider recipeInfoProvider;
    private final FridgeBasketRepository fridgeBasketRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Autowired
    public FridgeProvider(UserProvider userProvider, FridgeRepository fridgeRepository, IngredientCategoryProvider ingredientCategoryProvider, IngredientCategoryRepository ingredientCategoryRepository, RecipeInfoRepository recipeInfoRepository, RecipeIngredientRepository recipeIngredientRepository, ScrapPublicRepository scrapPublicRepository, RecipeInfoProvider recipeInfoProvider, FridgeBasketRepository fridgeBasketRepository, UserRepository userRepository, JwtService jwtService) {
        this.userProvider = userProvider;
        this.fridgeRepository = fridgeRepository;
        this.ingredientCategoryProvider = ingredientCategoryProvider;
        this.ingredientCategoryRepository = ingredientCategoryRepository;
        this.recipeInfoRepository = recipeInfoRepository;
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.scrapPublicRepository = scrapPublicRepository;
        this.recipeInfoProvider = recipeInfoProvider;
        this.fridgeBasketRepository = fridgeBasketRepository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    /**
     * 냉장고 조회 API
     * @param userIdx
     * @return GetFridgesRes
     * @throws BaseException
     */
    public GetFridgesRes retreiveFridges(int userIdx) throws BaseException {
        User user = userProvider.retrieveUserByUserIdx(userIdx);
        long fridgeBasketCount;
        try {
            fridgeBasketCount = fridgeBasketRepository.countByUserAndStatus(user,"ACTIVE");
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_FRIDGE_BASKET_COUNT);
        }



        List<IngredientCategory> ingredientCategories;
        try {
            ingredientCategories = ingredientCategoryRepository.findByStatus("ACTIVE");
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_INGREDIENT_CATEGORY);
        }

        List<Fridges> fridges = new ArrayList<>();
        for (int i=0;i<ingredientCategories.size()-1;i++){
            int ingredientCategoryIdx = ingredientCategories.get(i).getIngredientCategoryIdx();
            String ingredientCategoryName = ingredientCategories.get(i).getName();

            List<IngredientList> ingredientList = retreiveFridgeList(ingredientCategoryIdx,userIdx);

            Fridges fridge = new Fridges(ingredientCategoryIdx, ingredientCategoryName, ingredientList);
            fridges.add(fridge);

        }



        return new GetFridgesRes(fridgeBasketCount,fridges);

    }


    /**
     * 카테고리에 해당하는 냉장고 재료 리스트 추출
     *
     * @param ingredientCategoryIdx,userIdx
     * @return List<IngredientList>
     * @throws BaseException
     */
    public List<IngredientList> retreiveFridgeList(Integer ingredientCategoryIdx, int userIdx) throws BaseException {
        User user = userProvider.retrieveUserByUserIdx(userIdx);
        IngredientCategory ingredientCategory = ingredientCategoryProvider.retrieveIngredientCategoryByIngredientCategoryIdx(ingredientCategoryIdx);

        List<Fridge> fridgeList;
        try {
            fridgeList = fridgeRepository.findByUserAndIngredientCategoryAndStatus(user,ingredientCategory,"ACTIVE");
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_INGREDIENT_LIST);
        }

        return fridgeList.stream().map(fl -> {
            String ingredientName = fl.getIngredientName();
            String ingredientIcon = fl.getIngredientIcon();
            String storageMethod = fl.getStorageMethod();
            Integer count =fl.getCount();

            Date tmpDate = fl.getExpiredAt();
            String expiredAt;
            String expiredAtResult;
            Integer freshness;
            if (tmpDate == null || tmpDate.equals("")){
                expiredAt=null;
                expiredAtResult=null;
                freshness=555;
            }
            else{
                DateFormat sdFormat = new SimpleDateFormat("yy.MM.dd");
                expiredAt = sdFormat.format(tmpDate);
                expiredAtResult = sdFormat.format(tmpDate)+"까지";
                Date tempDate = new Date();
                String nowDate = sdFormat.format(tempDate);
                SimpleDateFormat sdf = new SimpleDateFormat("yy.MM.dd");
                long diffDay = 0;
                try{
                    Date startDate = sdf.parse(nowDate);
                    Date endDate = sdf.parse(expiredAt);
                    //두날짜 사이의 시간 차이(ms)를 하루 동안의 ms(24시*60분*60초*1000밀리초) 로 나눈다.
                    diffDay = (endDate.getTime() - startDate.getTime()) / (24*60*60*1000);
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

            return new IngredientList(ingredientName,ingredientIcon,expiredAtResult,storageMethod,count,freshness);
        }).collect(Collectors.toList());

    }


    /**
     * 재료명 존재여부
     * @param ingredientName,userIdx
     * @return Boolean
     * @throws BaseException
     */
    public Boolean existIngredient(String ingredientName,int userIdx) throws BaseException {
        User user = userProvider.retrieveUserByUserIdx(userIdx);
        Boolean existIngredient;
        try {
            existIngredient = fridgeRepository.existsByUserAndIngredientNameAndStatus(user,ingredientName,"ACTIVE");
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_INGREDIENT_NAME);
        }

        return existIngredient;
    }


    /**
     * 냉장고 파먹기 조회 API
     * @param userIdx
     * @return GetFridgesRecipeRes
     * @throws BaseException
     */
    public GetFridgesRecipeRes retreiveFridgesRecipe(int userIdx, Integer start, Integer display) throws BaseException {
        User user = userProvider.retrieveUserByUserIdx(userIdx);

        // 냉장고 재료랑 레시피 재료랑 동일한 재료 개수 많은 순
        // 1.유저인덱스로 냉장고 재료를 조회한다.
        List<Fridge> fridgeList;
        try {
            fridgeList = fridgeRepository.findByUserAndStatus(user, "ACTIVE");

        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_FRIDGE_LIST);
        }

        // 2.모든 레시피를 조회한다.
        List<RecipeInfo> recipeInfoList;
        try {
            recipeInfoList = recipeInfoRepository.findByStatus("ACTIVE");

        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_RECIPE_INFO_LIST);
        }

        // 각각의 레시피 재료와 일치하는 냉장고 재료의 개수를 저장하기 위해 hashmap을 선언한다.
        HashMap<Integer, Integer> map = new HashMap<>();
        try {

            // 3. 전체 레시피 목록으로 반복문을 실행한다.
            for (RecipeInfo recipeInfo : recipeInfoList) {
                Integer recipeId = recipeInfo.getRecipeId();

                // 3-1. 레시피 하나에 해당하는 레시피 재료들을 가져온다.
                List<RecipeIngredient> recipeIngredientList;
                try {
                    recipeIngredientList = recipeIngredientRepository.findByRecipeInfoAndStatus(recipeInfo, "ACTIVE");

                } catch (Exception ignored) {
                    throw new BaseException(FAILED_TO_GET_RECIPE_INGREDIENTS_LIST);
                }


                //재료들을 arrayList에 저장
                List<String> recipeIngredients = new ArrayList<>();
                for (RecipeIngredient recipeIngredient : recipeIngredientList){
                    recipeIngredients.add(recipeIngredient.getIrdntNm());

                }

                // 3-2. 냉장고 재료 목록으로 반복문을 실행한다.
                int count = 0;
                for (Fridge fridge : fridgeList) {
                    String fridgeIngredientName = fridge.getIngredientName();

                    // 레시피 재료리스트에 냉장고 재료가 포함된다면 count +1
                    if (recipeIngredients.contains(fridgeIngredientName)) {
                        count++;
                    }
                }

                // 3-3. count가 0이 아닐때 (레시피인덱스, count) (1,5),(2,3)를 map에 추가
                if(count!=0){
                    map.put(recipeId, count);
                }

            }

        } catch (Exception ignored) {
            throw new BaseException(NO_INGREDIENT_THAT_MATCH_THE_RECIPE);
        }


        // 4.count 많은 수 대로 정렬한다.
        List<Integer> keySetList = new ArrayList<>(map.keySet());
        //내림차순
        Collections.sort(keySetList, (o1, o2) -> (map.get(o2).compareTo(map.get(o1))));
        List<RecipeList> recipeList = new ArrayList<>();
        for(int i=start;i<start+display&&i<keySetList.size();i++){
        //for (Integer recipeId : keySetList) {
            Integer recipeId = keySetList.get(i);
            System.out.println(String.format("Key : %s, Value : %s", recipeId, map.get(recipeId)));
            RecipeInfo recipeInfo = recipeInfoRepository.findByRecipeIdAndStatus(recipeId, "ACTIVE");
            RecipeInfo ri = recipeInfoProvider.retrieveRecipeByRecipeId(recipeId);
            String title = recipeInfo.getRecipeNmKo();
            String content = recipeInfo.getSumry();
            String thumbnail = recipeInfo.getImgUrl();
            String cookingTime = recipeInfo.getCookingTime();
            long scrapCount = scrapPublicRepository.countByRecipeInfoAndStatus(ri, "ACTIVE");

            recipeList.add(new RecipeList(recipeId, title, content, thumbnail, cookingTime, scrapCount));

        }

        return new GetFridgesRecipeRes(keySetList.size(), recipeList);
    }


    /**
     * 유통기한 3일 남은 재료를 소유한 유저들 리스트 조회
     * @return shelfLifeUsers
     * @throws BaseException
     */
    public List<ShelfLifeUser> retreiveShelfLifeUserList() throws BaseException {
        List<Fridge> fridgeList;
        List<ShelfLifeUser> shelfLifeUsers = new ArrayList<>();
        try {
            fridgeList = fridgeRepository.findByStatus("ACTIVE");

            for (int i=0;i<fridgeList.size();i++) {
                Integer userIdx = fridgeList.get(i).getUser().getUserIdx();
                Boolean existUserIdx = userRepository.existsByUserIdxAndStatus(userIdx,"ACTIVE");
                if (existUserIdx){

                    Date tmpDate = fridgeList.get(i).getExpiredAt();

                    if (tmpDate== null){
                        continue;
                    }
                    DateFormat sdFormat = new SimpleDateFormat("yy.MM.dd");
                    String expiredAt = sdFormat.format(tmpDate);
                    Date tempDate = new Date();
                    String nowDate = sdFormat.format(tempDate);
                    SimpleDateFormat sdf = new SimpleDateFormat("yy.MM.dd");
                    long diffDay = 0;
                    try{
                        Date startDate = sdf.parse(nowDate);
                        Date endDate = sdf.parse(expiredAt);
                        diffDay = (endDate.getTime() - startDate.getTime()) / (24*60*60*1000);
                    }catch(ParseException e){
                        e.printStackTrace();
                    }


                    if(diffDay>2 && diffDay<=3){

                        String ingredientName = fridgeList.get(i).getIngredientName();
                        User user = userProvider.retrieveUserByUserIdx(userIdx);
                        String deviceToken = user.getDeviceToken();
                        ShelfLifeUser shelfLifeUser = new ShelfLifeUser(deviceToken,ingredientName);
                        shelfLifeUsers.add(shelfLifeUser);
                    }
                }

            }

        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_SHELF_LIFE_USER_LIST);
        }


        return shelfLifeUsers;
    }



}
