package com.recipe.app.src.userRecipePhoto;

import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserRecipePhotoProvider {
    private final UserRecipePhotoRepository userRecipePhotoRepository;
    private final JwtService jwtService;

    @Autowired
    public UserRecipePhotoProvider(UserRecipePhotoRepository userRecipePhotoRepository, JwtService jwtService) {
        this.userRecipePhotoRepository = userRecipePhotoRepository;
        this.jwtService = jwtService;
    }

//    public List retrieveUserRecipePhoto(Integer myRecipeIdx) throws BaseException {
//        List<UserRecipePhoto> userRecipePhotoList;
//        try {
//            userRecipePhotoList = userRecipePhotoRepository.findByUserRecipeIdxAndStatus(myRecipeIdx,"ACTIVE");
//        } catch (Exception ignored) {
//            throw new BaseException(FAILED_TO_GET_MY_RECIPE_PHOTOS);
//        }
//
//
//        List list= userRecipePhotoList.stream().map(userRecipePhoto -> {
//            String photoUrl = userRecipePhoto.getPhotoUrl();
//            return photoUrl;
//        }).collect(Collectors.toList());
//
//        return list;
//    }
}