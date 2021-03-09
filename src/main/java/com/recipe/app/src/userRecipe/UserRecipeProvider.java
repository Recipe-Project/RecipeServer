package com.recipe.app.src.userRecipe;

import com.recipe.app.config.BaseException;
import com.recipe.app.src.userRecipe.models.*;
import com.recipe.app.src.userRecipeIngredient.UserRecipeIngredientProvider;
import com.recipe.app.src.userRecipePhoto.UserRecipePhotoProvider;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.recipe.app.config.BaseResponseStatus.*;

@Service
public class UserRecipeProvider {
    private final UserRecipeRepository userRecipeRepository;
    private final UserRecipePhotoProvider userRecipePhotoProvider;
    private final UserRecipeIngredientProvider userRecipeIngredientProvider;
    private final JwtService jwtService;

    @Autowired
    public UserRecipeProvider(UserRecipeRepository userRecipeRepository, UserRecipePhotoProvider userRecipePhotoProvider,
                              UserRecipeIngredientProvider userRecipeIngredientProvider, JwtService jwtService) {
        this.userRecipeRepository = userRecipeRepository;
        this.userRecipePhotoProvider = userRecipePhotoProvider;
        this.userRecipeIngredientProvider = userRecipeIngredientProvider;
        this.jwtService = jwtService;
    }

    /**
     * 나만의 레시피 전체조회
     * @param userIdx
     * @return List<GetMyRecipesRes>
     * @throws BaseException
     */
    public List<GetMyRecipesRes> retrieveMyRecipesList(Integer userIdx, Pageable pageable) throws BaseException {

        Page<UserRecipe> userRecipeList;
        try {
            userRecipeList = userRecipeRepository.findByUserIdxAndStatus(userIdx, "ACTIVE",pageable);
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_MY_RECIPES);
        }

        return userRecipeList.stream().map(userRecipe -> {
            int userRecipeIdx = userRecipe.getUserRecipeIdx();
            String thumbnail = userRecipe.getThumbnail();
            String title = userRecipe.getTitle();
            String content = userRecipe.getContent() ;
            if (content.length()>50){
                content = content.substring(0,50);
            }


            return new GetMyRecipesRes(userRecipeIdx, thumbnail,title,content);

        }).collect(Collectors.toList());
    }


    /**
     * 나만의 레시피 상세조회
     * @param myRecipeIdx
     * @return getMyRecipeRes
     * @throws BaseException
     */
    @Transactional
    public GetMyRecipeRes retrieveMyRecipe(Integer userIdx, Integer myRecipeIdx) throws BaseException {
        UserRecipe userRecipe;
        try {
            userRecipe = userRecipeRepository.findByUserIdxAndUserRecipeIdxAndStatus(userIdx, myRecipeIdx,"ACTIVE");
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_MY_RECIPE);
        }

        List photoUrlList = userRecipePhotoProvider.retrieveUserRecipePhoto(myRecipeIdx);
        String title = userRecipe.getTitle();
        String content = userRecipe.getContent();

        List ingredientList = userRecipeIngredientProvider.retrieveUserRecipeIngredient(myRecipeIdx);

        return new GetMyRecipeRes(photoUrlList,title,content,ingredientList);
    }

    /**
     * 나만의 레시피 인덱스 존재여부
     * @param myRecipeIdx
     * @return Boolean
     * @throws BaseException
     */
    public Boolean existMyRecipe(Integer myRecipeIdx) throws BaseException {
        Boolean existMyRecipe;
        try {
            existMyRecipe = userRecipeRepository.existsByUserRecipeIdxAndStatus(myRecipeIdx,"ACTIVE");
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_MY_RECIPE);
        }


        return existMyRecipe;
    }

}