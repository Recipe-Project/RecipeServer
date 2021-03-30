package com.recipe.app.src.fridge;


import com.recipe.app.config.BaseException;
import com.recipe.app.src.fridge.models.Fridge;
import com.recipe.app.src.fridge.models.FridgeList;
import com.recipe.app.src.fridge.models.GetFridgesRes;
import com.recipe.app.src.ingredientCategory.IngredientCategoryProvider;
import com.recipe.app.src.ingredientCategory.IngredientCategoryRepository;
import com.recipe.app.src.ingredientCategory.models.IngredientCategory;
import com.recipe.app.src.user.UserProvider;
import com.recipe.app.src.user.models.User;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.recipe.app.config.BaseResponseStatus.*;

@Service
public class FridgeProvider {
    private final UserProvider userProvider;
    private final FridgeRepository fridgeRepository;
    private final IngredientCategoryProvider ingredientCategoryProvider;
    private final IngredientCategoryRepository ingredientCategoryRepository;
    private final JwtService jwtService;

    @Autowired
    public FridgeProvider(UserProvider userProvider, FridgeRepository fridgeRepository, IngredientCategoryProvider ingredientCategoryProvider, IngredientCategoryRepository ingredientCategoryRepository, JwtService jwtService) {
        this.userProvider = userProvider;
        this.fridgeRepository = fridgeRepository;
        this.ingredientCategoryProvider = ingredientCategoryProvider;
        this.ingredientCategoryRepository = ingredientCategoryRepository;
        this.jwtService = jwtService;
    }

    /**
     * 냉장고 조회 API
     * @param userIdx
     * @return List<GetFridgesRes>
     * @throws BaseException
     */
    public List<GetFridgesRes> retreiveFridges(int userIdx) throws BaseException {


        List<IngredientCategory> ingredientCategoryList;
        try {
            ingredientCategoryList = ingredientCategoryRepository.findByStatus("ACTIVE");
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_INGREDIENT_CATEGORY);
        }



        return ingredientCategoryList.stream().map(ic -> {

            Integer ingredientCategoryIdx = ic.getIngredientCategoryIdx();
            String ingredientCategoryName = ic.getName();

            List<FridgeList> fridgeList = null;
            try {
                fridgeList = retreiveFridgeList(ingredientCategoryIdx,userIdx);
            } catch (BaseException e) {
                e.printStackTrace();
            }


            return new GetFridgesRes(ingredientCategoryIdx, ingredientCategoryName, fridgeList);


        }).collect(Collectors.toList());

    }


    /**
     * 카테고리에 해당하는 냉장고 재료 리스트 추출
     *
     * @param ingredientCategoryIdx
     * @return List<FridgeList>
     * @throws BaseException
     */
    public List<FridgeList> retreiveFridgeList(Integer ingredientCategoryIdx,int userIdx) throws BaseException {
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

//
//                DateFormat sdFormat = new SimpleDateFormat("yyyy.MM.dd");
//                expiredAt = sdFormat.parse(tmpDate);
            }

//            DateFormat sdFormat = new SimpleDateFormat("yy.MM.dd");
//            String expiredAt = sdFormat.format(tmpDate);



//
//            Date tempDate = new Date();
//            String nowDate = sdFormat.format(tempDate);
//            SimpleDateFormat sdf = new SimpleDateFormat("yy.MM.dd");
//            long diffDay = 0;
//            try{
//                Date startDate = sdf.parse(nowDate);
//                Date endDate = sdf.parse(expiredAt);
//                //두날짜 사이의 시간 차이(ms)를 하루 동안의 ms(24시*60분*60초*1000밀리초) 로 나눈다.
//                diffDay = (endDate.getTime() - startDate.getTime()) / (24*60*60*1000);
//            }catch(ParseException e){
//                e.printStackTrace();
//            }
//
//            if (diffDay<0){
//                freshness=444;
//            }
//            else if(diffDay<=3){
//                freshness=1;
//            }
//            else if( diffDay <=7){
//                freshness=2;
//            }
//            else{
//                freshness=3;
//            }

            return new FridgeList(ingredientName,ingredientIcon,expiredAtResult,storageMethod,count,freshness);
        }).collect(Collectors.toList());

//        return fridgeList.stream().map(fl -> {
//            String ingredientName = fl.getIngredientName();
//            String ingredientIcon = fl.getIngredientIcon();
//
//            Date tmpDate = fl.getExpiredAt();
//
//
//
//            DateFormat sdFormat = new SimpleDateFormat("yy.MM.dd");
//            String expiredAt = sdFormat.format(tmpDate);
//
//            String storageMethod = fl.getStorageMethod();
//            Integer count =fl.getCount();
//            Integer freshness;
//
//            Date tempDate = new Date();
//            String nowDate = sdFormat.format(tempDate);
//            SimpleDateFormat sdf = new SimpleDateFormat("yy.MM.dd");
//            long diffDay = 0;
//            try{
//                Date startDate = sdf.parse(nowDate);
//                Date endDate = sdf.parse(expiredAt);
//                //두날짜 사이의 시간 차이(ms)를 하루 동안의 ms(24시*60분*60초*1000밀리초) 로 나눈다.
//                diffDay = (endDate.getTime() - startDate.getTime()) / (24*60*60*1000);
//            }catch(ParseException e){
//                e.printStackTrace();
//            }
//
//            if (diffDay<0){
//                freshness=444;
//            }
//            else if(diffDay<=3){
//                freshness=1;
//            }
//            else if( diffDay <=7){
//                freshness=2;
//            }
//            else{
//                freshness=3;
//            }
//
//            return new FridgeList(ingredientName,ingredientIcon,expiredAt+"까지",storageMethod,count,freshness);
//        }).collect(Collectors.toList());
    }


    /**
     * 재료명 존재여부
     * @param ingredientName
     * @return Boolean
     * @throws BaseException
     */
    public Boolean existIngredient(String ingredientName) throws BaseException {
        Boolean existIngredient;
        try {
            existIngredient = fridgeRepository.existsByIngredientNameAndStatus(ingredientName,"ACTIVE");
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_INGREDIENT_NAME);
        }


        return existIngredient;
    }

}
