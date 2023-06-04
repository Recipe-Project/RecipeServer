package com.recipe.app.src.userRecipe;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.src.ingredient.IngredientProvider;
import com.recipe.app.src.user.application.UserService;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.userRecipe.models.*;
import com.recipe.app.src.userRecipeIngredient.UserRecipeIngredientRepository;
import com.recipe.app.src.userRecipeIngredient.models.UserRecipeIngredient;
import com.recipe.app.src.userRecipePhoto.UserRecipePhotoRepository;
import com.recipe.app.common.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class UserRecipeService {
    private final UserService userService;
    private final UserRecipeRepository userRecipeRepository;
    private final UserRecipePhotoRepository userRecipePhotoRepository;
    private final UserRecipeIngredientRepository userRecipeIngredientRepository;
    private final UserRecipeProvider userRecipeProvider;
    private final IngredientProvider ingredientProvider;
    private final JwtService jwtService;

    @Autowired
    public UserRecipeService(UserService userService, UserRecipeRepository userRecipeRepository, UserRecipePhotoRepository userRecipePhotoRepository, UserRecipeIngredientRepository userRecipeIngredientRepository, UserRecipeProvider userRecipeProvider, IngredientProvider ingredientProvider, JwtService jwtService) {
        this.userService = userService;
        this.userRecipeRepository = userRecipeRepository;
        this.userRecipePhotoRepository = userRecipePhotoRepository;
        this.userRecipeIngredientRepository = userRecipeIngredientRepository;
        this.userRecipeProvider = userRecipeProvider;
        this.ingredientProvider = ingredientProvider;
        this.jwtService = jwtService;
    }



    /**
     * 나만의 레시피 생성 API
     * @param postMyRecipeReq,userIdx
     * @return PostMyRecipeRes
     * @throws BaseException
     */
    @Transactional
    public PostMyRecipeRes createMyRecipe(PostMyRecipeReq postMyRecipeReq, int userIdx) throws BaseException {
        String thumbnail = postMyRecipeReq.getThumbnail();
        String title = postMyRecipeReq.getTitle();
        String content = postMyRecipeReq.getContent();
        List<MyRecipeIngredient> ingredientList = postMyRecipeReq.getIngredientList();

        Integer userRecipeIdx;
        User user = userService.retrieveUserByUserIdx(userIdx);

        UserRecipe userRecipe = new UserRecipe(user, thumbnail, title, content);
        userRecipe = userRecipeRepository.save(userRecipe);
        userRecipeIdx = userRecipe.getUserRecipeIdx();

        if (ingredientList !=null) {
            for (int i = 0; i < ingredientList.size(); i++) {
                String ingredientIcon = ingredientList.get(i).getIngredientIcon();
                String ingredientName = ingredientList.get(i).getIngredientName();
                UserRecipeIngredient userRecipeIngredient = new UserRecipeIngredient(userRecipeIdx,null,ingredientIcon,ingredientName);
                userRecipeIngredientRepository.save(userRecipeIngredient);
            }
        }

        return new PostMyRecipeRes(userRecipeIdx,thumbnail,title,content,ingredientList);
    }
    /**
     * 나만의 레시피 수정 API
     * @param patchMyRecipeReq,userIdx,userRecipeIdx
     * @return PatchMyRecipeRes
     * @throws BaseException
     */
    @Transactional
    public PatchMyRecipeRes updateMyRecipe(PatchMyRecipeReq patchMyRecipeReq, Integer userIdx, Integer userRecipeIdx) throws BaseException {
        User user = userService.retrieveUserByUserIdx(userIdx);

        String thumbnail = patchMyRecipeReq.getThumbnail();
        String title = patchMyRecipeReq.getTitle();
        String content = patchMyRecipeReq.getContent();
        List<MyRecipeIngredient> ingredientList = patchMyRecipeReq.getIngredientList();

        UserRecipe userRecipe = userRecipeRepository.findByUserAndUserRecipeIdxAndStatus(user,userRecipeIdx,"ACTIVE");

        List<UserRecipeIngredient> userRecipeIngredientList = userRecipeIngredientRepository.findByUserRecipeIdxAndStatus(userRecipeIdx,"ACTIVE");

        userRecipe.setThumbnail(thumbnail);
        userRecipe.setTitle(title);
        userRecipe.setContent(content);
        userRecipeRepository.save(userRecipe);

        // 재료 삭제
        for (int i=0;i<userRecipeIngredientList.size();i++){
            userRecipeIngredientList.get(i).setStatus("INACTIVE");
        }
        userRecipeIngredientRepository.saveAll(userRecipeIngredientList);

        if (ingredientList !=null) {
            for (int i = 0; i < ingredientList.size(); i++) {
                String ingredientIcon = ingredientList.get(i).getIngredientIcon();
                String ingredientName = ingredientList.get(i).getIngredientName();
                UserRecipeIngredient userRecipeIngredient = new UserRecipeIngredient(userRecipeIdx,null,ingredientIcon,ingredientName);
                userRecipeIngredientRepository.save(userRecipeIngredient);
            }
        }

        return new PatchMyRecipeRes(thumbnail,title,content,ingredientList);

    }


    /**
     * 나만의 레시피 삭제 API
     * @param userIdx,myRecipeIdx
     * @throws BaseException
     */
    @Transactional
    public void deleteUserRecipe(Integer userIdx, Integer myRecipeIdx) throws BaseException {
        User user = userService.retrieveUserByUserIdx(userIdx);
        UserRecipe userRecipe = userRecipeRepository.findByUserAndUserRecipeIdxAndStatus(user,myRecipeIdx,"ACTIVE");

        userRecipe.setStatus("INACTIVE");
        userRecipeRepository.save(userRecipe);

        List<UserRecipeIngredient> userRecipeIngredientList = userRecipeIngredientRepository.findByUserRecipeIdxAndStatus(myRecipeIdx,"ACTIVE");

        for (int i=0;i<userRecipeIngredientList.size();i++){
            userRecipeIngredientList.get(i).setStatus("INACTIVE");
        }
        userRecipeIngredientRepository.saveAll(userRecipeIngredientList);
    }
}