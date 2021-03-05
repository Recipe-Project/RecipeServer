package com.recipe.app.src.userRecipe;

import com.recipe.app.config.BaseException;
import com.recipe.app.src.user.models.PatchUserRes;
import com.recipe.app.src.userRecipe.models.*;
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

    /**
     * 나만의 레시피 삭제
     * @param myRecipeIdx
     * @throws BaseException
     */
    @Transactional
    public void deleteUserRecipe(Integer userIdx, Integer myRecipeIdx) throws BaseException {
        UserRecipe userRecipe;
        try {
            userRecipe = userRecipeRepository.findByUserIdxAndUserRecipeIdxAndStatus(userIdx,myRecipeIdx,"ACTIVE");
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_USER_RECIPE);
        }
        userRecipe.setStatus("INACTIVE");


        List<UserRecipePhoto> userRecipePhotoList;
        try {
            userRecipePhotoList = userRecipePhotoRepository.findByUserRecipeIdxAndStatus(myRecipeIdx,"ACTIVE");
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_USER_RECIPE_PHOTO);
        }

        for (int i=0;i<userRecipePhotoList.size();i++){
            userRecipePhotoList.get(i).setStatus("INACTIVE");
        }
        userRecipePhotoRepository.saveAll(userRecipePhotoList);

    }

    /**
     * 나만의 레시피 생성
     * @param postMyRecipeReq,userIdx
     * @return PostMyRecipeRes
     * @throws BaseException
     */
    @Transactional
    public PostMyRecipeRes createMyRecipe(PostMyRecipeReq postMyRecipeReq, int userIdx) throws BaseException {


        List<String> photoUrlList = postMyRecipeReq.getPhotoUrlList();
        String thumbnail = postMyRecipeReq.getThumbnail();
        String title = postMyRecipeReq.getTitle();
        String content = postMyRecipeReq.getContent();


        try {
            UserRecipe userRecipe = new UserRecipe(userIdx, thumbnail, title, content);
            userRecipe = userRecipeRepository.save(userRecipe);
            Integer userRecipeIdx = userRecipe.getUserRecipeIdx();


            if (photoUrlList != null) {
                for (int i = 0; i < photoUrlList.size(); i++) {
                    UserRecipePhoto userRecipePhoto = new UserRecipePhoto(userRecipeIdx, photoUrlList.get(i));
                    userRecipePhotoRepository.save(userRecipePhoto);
                }
            }

        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_POST_MY_RECIPE);
        }

        return new PostMyRecipeRes(photoUrlList,thumbnail,title,content);
    }

    /**
     * 나만의 레시피 수정
     * @param patchMyRecipeReq,userIdx
     * @return PatchMyRecipeRes
     * @throws BaseException
     */
    @Transactional
    public PatchMyRecipeRes updateMyRecipe(PatchMyRecipeReq patchMyRecipeReq, Integer userIdx, Integer userRecipeIdx) throws BaseException {
        UserRecipe userRecipe;
        try {
            userRecipe = userRecipeRepository.findByUserIdxAndUserRecipeIdxAndStatus(userIdx,userRecipeIdx,"ACTIVE");
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_USER_RECIPE);
        }

        List<UserRecipePhoto> userRecipePhotoList;
        try {
            userRecipePhotoList = userRecipePhotoRepository.findByUserRecipeIdxAndStatus(userRecipeIdx,"ACTIVE");
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_USER_RECIPE_PHOTO);
        }

        List<String> photoUrlList = patchMyRecipeReq.getPhotoUrlList();
        String thumbnail = patchMyRecipeReq.getThumbnail();
        String title = patchMyRecipeReq.getTitle();
        String content = patchMyRecipeReq.getContent();

        try {
            userRecipe.setThumbnail(thumbnail);
            userRecipe.setTitle(title);
            userRecipe.setContent(content);
            userRecipeRepository.save(userRecipe);

            // 삭제
            for (int i=0;i<userRecipePhotoList.size();i++){
                userRecipePhotoList.get(i).setStatus("INACTIVE");
            }
            userRecipePhotoRepository.saveAll(userRecipePhotoList);

            // 삭제
            if (photoUrlList != null) {
                for (int i = 0; i < photoUrlList.size(); i++) {
                    UserRecipePhoto userRecipePhoto = new UserRecipePhoto(userRecipeIdx, photoUrlList.get(i));
                    userRecipePhotoRepository.save(userRecipePhoto);
                }
            }
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_PATCH_MY_RECIPE);
        }
        return new PatchMyRecipeRes(photoUrlList,thumbnail,title,content);


    }


}