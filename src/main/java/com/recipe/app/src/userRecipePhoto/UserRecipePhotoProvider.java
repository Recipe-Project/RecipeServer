package com.recipe.app.src.userRecipePhoto;

import com.recipe.app.config.BaseException;
import com.recipe.app.src.userRecipe.UserRecipeRepository;
import com.recipe.app.src.userRecipePhoto.models.*;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.recipe.app.config.BaseResponseStatus.*;

@Service
public class UserRecipePhotoProvider {
    private final UserRecipePhotoRepository userRecipePhotoRepository;
    private final JwtService jwtService;

    @Autowired
    public UserRecipePhotoProvider(UserRecipePhotoRepository userRecipePhotoRepository, JwtService jwtService) {
        this.userRecipePhotoRepository = userRecipePhotoRepository;
        this.jwtService = jwtService;
    }

    public List retrieveUserRecipePhoto(Integer userRecipeIdx) throws BaseException {
        List<UserRecipePhoto> userRecipePhotoList;
        try {
            userRecipePhotoList = userRecipePhotoRepository.findByUserRecipeIdxAndStatus(userRecipeIdx,"ACTIVE");
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_USER_RECIPE_PHOTO);
        }


        List list= userRecipePhotoList.stream().map(userRecipePhoto -> {
            String photoUrl = userRecipePhoto.getPhotoUrl();
            return photoUrl;
        }).collect(Collectors.toList());

        return list;
    }
}