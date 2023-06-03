package com.recipe.app.src.userRecipe;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.userRecipe.models.*;
import com.recipe.app.src.userRecipeIngredient.UserRecipeIngredientProvider;
import com.recipe.app.src.userRecipePhoto.UserRecipePhotoProvider;
import com.recipe.app.common.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserRecipeProvider {
    private final UserProvider userProvider;
    private final UserRecipeRepository userRecipeRepository;
    private final UserRecipePhotoProvider userRecipePhotoProvider;
    private final UserRecipeIngredientProvider userRecipeIngredientProvider;
    private final JwtService jwtService;

    @Autowired
    public UserRecipeProvider(UserProvider userProvider, UserRecipeRepository userRecipeRepository, UserRecipePhotoProvider userRecipePhotoProvider,
                              UserRecipeIngredientProvider userRecipeIngredientProvider, JwtService jwtService) {
        this.userProvider = userProvider;
        this.userRecipeRepository = userRecipeRepository;
        this.userRecipePhotoProvider = userRecipePhotoProvider;
        this.userRecipeIngredientProvider = userRecipeIngredientProvider;
        this.jwtService = jwtService;
    }

    /**
     * 나만의 레시피 전체조회 API
     * @param userIdx
     * @return List<GetMyRecipesRes>
     * @throws BaseException
     */
    public List<GetMyRecipesRes> retrieveMyRecipesList(Integer userIdx) throws BaseException {

        User user = userProvider.retrieveUserByUserIdx(userIdx);
        List<UserRecipe> userRecipeList = userRecipeRepository.findByUserAndStatus(user, "ACTIVE", Sort.by("createdAt").descending());

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
     * 나만의 레시피 상세조회 API
     * @param userIdx,myRecipeIdx
     * @return GetMyRecipeRes
     * @throws BaseException
     */
    @Transactional
    public GetMyRecipeRes retrieveMyRecipe(Integer userIdx, Integer myRecipeIdx) throws BaseException {
        User user = userProvider.retrieveUserByUserIdx(userIdx);
        UserRecipe userRecipe = userRecipeRepository.findByUserAndUserRecipeIdxAndStatus(user, myRecipeIdx,"ACTIVE");

        String thumbnail = userRecipe.getThumbnail();
        String title = userRecipe.getTitle();
        String content = userRecipe.getContent();

        List<MyRecipeIngredient> myRecipeIngredientList = userRecipeIngredientProvider.retrieveUserRecipeIngredient(myRecipeIdx);

        return new GetMyRecipeRes(thumbnail,title,content, myRecipeIngredientList);
    }

    /**
     * 나만의 레시피 인덱스 존재여부
     * @param myRecipeIdx
     * @return Boolean
     * @throws BaseException
     */
    public Boolean existMyRecipe(Integer myRecipeIdx) throws BaseException {
        return userRecipeRepository.existsByUserRecipeIdxAndStatus(myRecipeIdx,"ACTIVE");
    }

}