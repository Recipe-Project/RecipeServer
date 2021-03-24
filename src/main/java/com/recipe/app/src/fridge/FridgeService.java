package com.recipe.app.src.fridge;

import com.recipe.app.config.BaseException;
import com.recipe.app.src.fridge.models.*;
import com.recipe.app.src.fridgeBasket.FridgeBasketRepository;
import com.recipe.app.src.fridgeBasket.models.FridgeBasket;
import com.recipe.app.src.user.UserProvider;
import com.recipe.app.src.user.models.User;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.recipe.app.config.BaseResponseStatus.*;

@Service
public class FridgeService {
    private final UserProvider userProvider;
    private final FridgeRepository fridgeRepository;
    private final FridgeBasketRepository fridgeBasketRepository;
    private final JwtService jwtService;

    @Autowired
    public FridgeService(UserProvider userProvider, FridgeRepository fridgeRepository, FridgeBasketRepository fridgeBasketRepository, JwtService jwtService) {
        this.userProvider = userProvider;
        this.fridgeRepository = fridgeRepository;
        this.fridgeBasketRepository = fridgeBasketRepository;
        this.jwtService = jwtService;
    }

    /**
     * 냉장고 채우기  API
     * @param postFridgesReq,userIdx
     * @return List<PostFridgesRes>
     * @throws BaseException
     */
    @Transactional
    public List<PostFridgesRes> createFridges(PostFridgesReq postFridgesReq, int userIdx) throws BaseException {
        User user = userProvider.retrieveUserByUserIdx(userIdx);
        List<FridgeBasketList> fridgeBasketList = postFridgesReq.getFridgeBasketList();


        try {
            for (int i = 0; i < fridgeBasketList.size(); i++) {


                String ingredientName = fridgeBasketList.get(i).getIngredientName();


                String ingredientIcon = fridgeBasketList.get(i).getIngredientIcon();
                String expiredAtTmp = fridgeBasketList.get(i).getExpiredAt();

                DateFormat sdFormat = new SimpleDateFormat("yyyy.MM.dd");
                Date expiredAt = sdFormat.parse(expiredAtTmp);

                String storageMethod = fridgeBasketList.get(i).getStorageMethod();
                Integer count = fridgeBasketList.get(i).getCount();

                Fridge fridge = new Fridge(user,ingredientName,ingredientIcon,storageMethod,expiredAt,count);
                fridge = fridgeRepository.save(fridge);

            }

            List<FridgeBasket> fbList = fridgeBasketRepository.findByUserAndStatus(user, "ACTIVE");


            // 재료 삭제
            for (int i=0;i<fbList.size();i++){
                fbList.get(i).setStatus("INACTIVE");
            }
            fridgeBasketRepository.saveAll(fbList);

        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_POST_FRIDGES);
        }

        List<PostFridgesRes> postFridgesResList = new ArrayList<>();

        for (int i = 0; i < fridgeBasketList.size(); i++) {
            String ingredientName = fridgeBasketList.get(i).getIngredientName();
            String ingredientIcon = fridgeBasketList.get(i).getIngredientIcon();
            String expiredAt = fridgeBasketList.get(i).getExpiredAt();
            String storageMethod = fridgeBasketList.get(i).getStorageMethod();
            Integer count = fridgeBasketList.get(i).getCount();


            PostFridgesRes postFridgesRes = new PostFridgesRes(ingredientName,ingredientIcon,expiredAt,storageMethod,count);
            postFridgesResList.add(postFridgesRes);

        }
        return postFridgesResList;

    }

}
