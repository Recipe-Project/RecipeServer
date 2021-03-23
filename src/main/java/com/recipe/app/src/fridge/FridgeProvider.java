package com.recipe.app.src.fridge;


import com.recipe.app.config.BaseException;
import com.recipe.app.src.fridge.models.Fridge;
import com.recipe.app.src.fridge.models.GetFridgesRes;
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
    private final JwtService jwtService;

    @Autowired
    public FridgeProvider(UserProvider userProvider, FridgeRepository fridgeRepository, JwtService jwtService) {
        this.userProvider = userProvider;
        this.fridgeRepository = fridgeRepository;
        this.jwtService = jwtService;
    }

    /**
     * 냉장고 조회 API
     * @param userIdx
     * @return List<GetFridgesRes>
     * @throws BaseException
     */
    public List<GetFridgesRes> retreiveFridges(int userIdx) throws BaseException {
        User user = userProvider.retrieveUserByUserIdx(userIdx);
        List<Fridge> fridgeList;
        try {
            fridgeList = fridgeRepository.findByUserAndStatus(user,"ACTIVE");
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_RETREIVE_FRIDGE_BY_USER);
        }

        return fridgeList.stream().map(fridge -> {

            String ingredientName = fridge.getIngredientName();
            String ingredientIcon = fridge.getIngredientIcon();
            Date tmpDate = fridge.getExpiredAt();

            DateFormat sdFormat = new SimpleDateFormat("yy.MM.dd");
            String expiredAt = sdFormat.format(tmpDate);

            String storageMethod = fridge.getStorageMethod();
            Integer count = fridge.getCount();

            Integer freshness;

            Date tempDate = new Date();
            String nowDate = sdFormat.format(tempDate);

            SimpleDateFormat sdf = new SimpleDateFormat("yy.MM.dd");

            long diffDay = 0;
            try{
                Date startDate = sdf.parse(nowDate);
                Date endDate = sdf.parse(expiredAt);
                //두날짜 사이의 시간 차이(ms)를 하루 동안의 ms(24시*60분*60초*1000밀리초) 로 나눈다.
                diffDay = (endDate.getTime() - startDate.getTime()) / (24*60*60*1000);
                //System.out.println(diffDay+"일");
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



            return new GetFridgesRes(ingredientName,ingredientIcon,expiredAt+"까지",storageMethod,count,freshness);

        }).collect(Collectors.toList());
    }

}
