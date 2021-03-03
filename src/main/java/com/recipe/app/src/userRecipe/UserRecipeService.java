package com.recipe.app.src.userRecipe;

import com.recipe.app.config.BaseException;
import com.recipe.app.src.user.UserProvider;
import com.recipe.app.src.user.UserRepository;
import com.recipe.app.src.userRecipe.models.UserRecipe;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserRecipeService {
    private final UserRecipeRepository userRecipeRepository;
    private final UserRecipeProvider userRecipeProvider;
    private final JwtService jwtService;

    @Autowired
    public UserRecipeService(UserRecipeRepository userRecipeRepository, UserRecipeProvider userRecipeProvider, JwtService jwtService) {
        this.userRecipeRepository = userRecipeRepository;
        this.userRecipeProvider = userRecipeProvider;
        this.jwtService = jwtService;
    }

//    /**
//     * 나만의 레시피 삭제
//     * @param myRecipeIdx
//     * @throws BaseException
//     */
//    public void deleteUserRecipe(Integer myRecipeIdx) throws BaseException {
//        UserRecipe userRecipe = userRecipeProvider.retrieveUserRecipeByMyRecipeIdx(myRecipeIdx);
//
//        userRecipe.setStatus("INACTIVE");
//
//
//        try {
//            userRecipeRepository.save(userRecipe);
//        } catch (Exception ignored) {
//            throw new BaseException(FAILED_TO_DELETE_USER_RECIPE);
//        }
//    }
}