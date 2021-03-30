package com.recipe.app.src.fridge;

import com.recipe.app.config.BaseException;
import com.recipe.app.src.fridge.models.*;
import com.recipe.app.src.fridgeBasket.FridgeBasketRepository;
import com.recipe.app.src.fridgeBasket.models.FridgeBasket;
import com.recipe.app.src.ingredientCategory.IngredientCategoryProvider;
import com.recipe.app.src.ingredientCategory.models.IngredientCategory;
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
    private final FridgeProvider fridgeProvider;
    private final FridgeRepository fridgeRepository;
    private final FridgeBasketRepository fridgeBasketRepository;
    private final IngredientCategoryProvider ingredientCategoryProvider;
    private final JwtService jwtService;

    @Autowired
    public FridgeService(UserProvider userProvider, FridgeProvider fridgeProvider, FridgeRepository fridgeRepository, FridgeBasketRepository fridgeBasketRepository, IngredientCategoryProvider ingredientCategoryProvider, JwtService jwtService) {
        this.userProvider = userProvider;
        this.fridgeProvider = fridgeProvider;
        this.fridgeRepository = fridgeRepository;
        this.fridgeBasketRepository = fridgeBasketRepository;
        this.ingredientCategoryProvider = ingredientCategoryProvider;
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

                Integer ingredientCategoryIdx = fridgeBasketList.get(i).getIngredientCategoryIdx();

                IngredientCategory ingredientCategory = ingredientCategoryProvider.retrieveIngredientCategoryByIngredientCategoryIdx(ingredientCategoryIdx);

                String expiredAtTmp = fridgeBasketList.get(i).getExpiredAt();

                DateFormat sdFormat = new SimpleDateFormat("yyyy.MM.dd");
                Date expiredAt = sdFormat.parse(expiredAtTmp);

                String storageMethod = fridgeBasketList.get(i).getStorageMethod();
                Integer count = fridgeBasketList.get(i).getCount();

                Fridge fridge = new Fridge(user,ingredientName,ingredientIcon,ingredientCategory,storageMethod,expiredAt,count);
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
            Integer ingredientCategoryIdx = fridgeBasketList.get(i).getIngredientCategoryIdx();
            String expiredAt = fridgeBasketList.get(i).getExpiredAt();
            String storageMethod = fridgeBasketList.get(i).getStorageMethod();
            Integer count = fridgeBasketList.get(i).getCount();


            PostFridgesRes postFridgesRes = new PostFridgesRes(ingredientName,ingredientIcon,ingredientCategoryIdx,expiredAt,storageMethod,count);
            postFridgesResList.add(postFridgesRes);

        }
        return postFridgesResList;

    }


    /**
     * 냉장고 재료 삭제 API
     * @param userIdx,parameters
     * @throws BaseException
     */
    @Transactional
    public void deleteFridgeIngredient(Integer userIdx, DeleteFridgesIngredientReq parameters) throws BaseException {
        User user = userProvider.retrieveUserByUserIdx(userIdx);

        for (int i=0;i<parameters.getIngredientList().size();i++){
            String ingredientName = parameters.getIngredientList().get(i);
            Fridge fridge;
            try {
                fridge = fridgeRepository.findByIngredientNameAndStatus(ingredientName,"ACTIVE");
            } catch (Exception ignored) {
                throw new BaseException(FAILED_TO_GET_FRIDGE);
            }

            try {
                fridge.setStatus("INACTIVE");
                fridgeRepository.save(fridge);


            } catch (Exception exception) {
                throw new BaseException(FAILED_TO_DELETE_FRIDGE);
            }

        }




    }

//    /**
//     * 냉장고 재료 수정 API
//     * @param patchFridgesIngredientReq,userIdx
//     * @return PatchMyRecipeRes
//     * @throws BaseException
//     */
//    @Transactional
//    public PatchFridgesIngredientRes updateFridgeIngredient(PatchFridgesIngredientReq patchFridgesIngredientReq, Integer userIdx) throws BaseException {
//        User user = userProvider.retrieveUserByUserIdx(userIdx);
//
//        UserRecipe userRecipe;
//        try {
//            userRecipe = userRecipeRepository.findByUserAndUserRecipeIdxAndStatus(user,userRecipeIdx,"ACTIVE");
//        } catch (Exception ignored) {
//            throw new BaseException(FAILED_TO_GET_MY_RECIPE);
//        }
//
//        List<UserRecipeIngredient> userRecipeIngredientList;
//        try {
//            userRecipeIngredientList = userRecipeIngredientRepository.findByUserRecipeIdxAndStatus(userRecipeIdx,"ACTIVE");
//        } catch (Exception ignored) {
//            throw new BaseException(FAILED_TO_GET_MY_RECIPE_INGREDIENTS);
//        }
//
//        String thumbnail = patchMyRecipeReq.getThumbnail();
//        String title = patchMyRecipeReq.getTitle();
//        String content = patchMyRecipeReq.getContent();
//        List<Integer> ingredientList = patchMyRecipeReq.getIngredientList();
//        try {
//            userRecipe.setThumbnail(thumbnail);
//            userRecipe.setTitle(title);
//            userRecipe.setContent(content);
//            userRecipeRepository.save(userRecipe);
//
//
//            // 재료 삭제
//            for (int i=0;i<userRecipeIngredientList.size();i++){
//                userRecipeIngredientList.get(i).setStatus("INACTIVE");
//            }
//            userRecipeIngredientRepository.saveAll(userRecipeIngredientList);
//
//
//            if (ingredientList != null) {
//                for (int i = 0; i < ingredientList.size(); i++) {
//                    UserRecipeIngredient userRecipeIngredient = new UserRecipeIngredient(userRecipeIdx, ingredientList.get(i));
//                    userRecipeIngredientRepository.save(userRecipeIngredient);
//                }
//            }
//
//
//        } catch (Exception ignored) {
//            throw new BaseException(FAILED_TO_PATCH_MY_RECIPE);
//        }
//        return new PatchMyRecipeRes(thumbnail,title,content,ingredientList);
//
//        // 냉장고 바구니 조회
//
//        // for 문 돌려
//        // 입력한 순서대로 수정해
//        //
//
//
//
//    }

}
