package com.recipe.app.src.userRecipe;

import com.recipe.app.config.BaseException;
import com.recipe.app.src.user.UserProvider;
import com.recipe.app.src.user.UserRepository;
import com.recipe.app.src.userRecipe.models.UserRecipe;
import com.recipe.app.src.userRecipePhoto.UserRecipePhotoRepository;
import com.recipe.app.src.userRecipePhoto.models.UserRecipePhoto;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.recipe.app.config.BaseResponseStatus.*;


@Service
public class UserRecipeService {
    private final UserRecipeRepository userRecipeRepository;
    private final UserRecipePhotoRepository userRecipePhotoRepository;
    private final UserRecipeProvider userRecipeProvider;
    private final JwtService jwtService;

    @Autowired
    public UserRecipeService(UserRecipeRepository userRecipeRepository, UserRecipePhotoRepository userRecipePhotoRepository, UserRecipeProvider userRecipeProvider, JwtService jwtService) {
        this.userRecipeRepository = userRecipeRepository;
        this.userRecipePhotoRepository = userRecipePhotoRepository;
        this.userRecipeProvider = userRecipeProvider;
        this.jwtService = jwtService;
    }

//    /**
//     * 나만의 레시피 삭제
//     * @param myRecipeIdx
//     * @throws BaseException
//     */
//    @Transactional
//    public void deleteUserRecipe(Integer userIdx, Integer myRecipeIdx) throws BaseException {
//        UserRecipe userRecipe;
//        try {
//            userRecipe = userRecipeRepository.findByUserIdxAndUserRecipeIdxAndStatus(userIdx,myRecipeIdx,"ACTIVE");
//        } catch (Exception ignored) {
//            throw new BaseException(FAILED_TO_GET_USER_RECIPE);
//        }
//        userRecipe.setStatus("INACTIVE");
//
//
//        List<UserRecipePhoto> userRecipePhotoList;
//        try {
//            userRecipePhotoList = userRecipePhotoRepository.findByUserIdxAndUserRecipeIdxAndStatus(userIdx,myRecipeIdx,"ACTIVE");
//        } catch (Exception ignored) {
//            throw new BaseException(FAILED_TO_GET_USER_RECIPE_PHOTO);
//        }
//
//        for (int i=0;i<userRecipePhotoList.size();i++){
//            userRecipePhotoList.get(i).setStatus("INACTIVE");
//        }
//        userRecipePhotoRepository.saveAll(userRecipePhotoList);
//
//    }

}