package com.recipe.app.src.userRecipe;

import com.recipe.app.config.BaseException;
import com.recipe.app.src.ingredient.IngredientProvider;
import com.recipe.app.src.ingredient.models.Ingredient;
import com.recipe.app.src.user.UserProvider;
import com.recipe.app.src.user.models.User;
import com.recipe.app.src.userRecipe.models.*;
import com.recipe.app.src.userRecipeIngredient.UserRecipeIngredientRepository;
import com.recipe.app.src.userRecipeIngredient.models.UserRecipeIngredient;
import com.recipe.app.src.userRecipePhoto.UserRecipePhotoRepository;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.recipe.app.config.BaseResponseStatus.*;


@Service
public class UserRecipeService {
    private final UserProvider userProvider;
    private final UserRecipeRepository userRecipeRepository;
    private final UserRecipePhotoRepository userRecipePhotoRepository;
    private final UserRecipeIngredientRepository userRecipeIngredientRepository;
    private final UserRecipeProvider userRecipeProvider;
    private final IngredientProvider ingredientProvider;
    private final JwtService jwtService;

    @Autowired
    public UserRecipeService(UserProvider userProvider, UserRecipeRepository userRecipeRepository, UserRecipePhotoRepository userRecipePhotoRepository, UserRecipeIngredientRepository userRecipeIngredientRepository, UserRecipeProvider userRecipeProvider, IngredientProvider ingredientProvider, JwtService jwtService) {
        this.userProvider = userProvider;
        this.userRecipeRepository = userRecipeRepository;
        this.userRecipePhotoRepository = userRecipePhotoRepository;
        this.userRecipeIngredientRepository = userRecipeIngredientRepository;
        this.userRecipeProvider = userRecipeProvider;
        this.ingredientProvider = ingredientProvider;
        this.jwtService = jwtService;
    }

//    /**
//     * 나만의 레시피 삭제 API
//     * @param userIdx,myRecipeIdx
//     * @throws BaseException
//     */
//    @Transactional
//    public void deleteUserRecipe(Integer userIdx, Integer myRecipeIdx) throws BaseException {
//        User user = userProvider.retrieveUserByUserIdx(userIdx);
//        UserRecipe userRecipe;
//        try {
//            userRecipe = userRecipeRepository.findByUserAndUserRecipeIdxAndStatus(user,myRecipeIdx,"ACTIVE");
//        } catch (Exception ignored) {
//            throw new BaseException(FAILED_TO_GET_MY_RECIPE);
//        }
//
//
//
//        //재료 삭제도 해야해
//        try {
//            userRecipe.setStatus("INACTIVE");
//            userRecipeRepository.save(userRecipe);
//
//
//        } catch (Exception exception) {
//            throw new BaseException(FAILED_TO_DELETE_MY_RECIPE);
//        }
//
//
//
//
//    }


    // origin
//    /**
//     * 나만의 레시피 생성
//     * @param postMyRecipeReq,userIdx
//     * @return PostMyRecipeRes
//     * @throws BaseException
//     */
//    @Transactional
//    public PostMyRecipeRes createMyRecipe(PostMyRecipeReq postMyRecipeReq, int userIdx) throws BaseException {
//
//
//        String thumbnail = postMyRecipeReq.getThumbnail();
//        String title = postMyRecipeReq.getTitle();
//        String content = postMyRecipeReq.getContent();
//        List<Integer> ingredientList = postMyRecipeReq.getIngredientList();
//        Integer userRecipeIdx;
//        User user = userProvider.retrieveUserByUserIdx(userIdx);
//
//        try {
//            UserRecipe userRecipe = new UserRecipe(user, thumbnail, title, content);
//            userRecipe = userRecipeRepository.save(userRecipe);
//            userRecipeIdx = userRecipe.getUserRecipeIdx();
//
//
//
//            if (ingredientList != null) {
//                for (int i = 0; i < ingredientList.size(); i++) {
//                    UserRecipeIngredient userRecipeIngredient = new UserRecipeIngredient(userRecipeIdx, ingredientList.get(i));
//                    userRecipeIngredientRepository.save(userRecipeIngredient);
//                }
//            }
//
//        } catch (Exception exception) {
//            throw new BaseException(FAILED_TO_POST_MY_RECIPE);
//        }
//
//        return new PostMyRecipeRes(userRecipeIdx,thumbnail,title,content,ingredientList);
//    }



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
        List<Integer> ingredientList = postMyRecipeReq.getIngredientList();
        List<MyRecipeIngredient> direcIngredientList = postMyRecipeReq.getDirectIngredientList();



        Integer userRecipeIdx;
        User user = userProvider.retrieveUserByUserIdx(userIdx);

        try {
            UserRecipe userRecipe = new UserRecipe(user, thumbnail, title, content);
            userRecipe = userRecipeRepository.save(userRecipe);
            userRecipeIdx = userRecipe.getUserRecipeIdx();
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_SAVE_MY_RECIPE);
        }


        try {
            if (ingredientList!=null) {
                for (int i = 0; i < ingredientList.size(); i++) {
                    Ingredient ingredient = ingredientProvider.retrieveIngredientByIngredientIdx(ingredientList.get(i));
                    String ingredientIcon = ingredient.getIcon();
                    String ingredientName = ingredient.getName();

                    UserRecipeIngredient userRecipeIngredient = new UserRecipeIngredient(userRecipeIdx,ingredient,ingredientIcon,ingredientName);
                    userRecipeIngredientRepository.save(userRecipeIngredient);
                }
            }
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_SAVE_MY_RECIPE_INGREDIENT);
        }

        try {
            if (direcIngredientList !=null) {
                for (int i = 0; i < direcIngredientList.size(); i++) {
                    String ingredientIcon = direcIngredientList.get(i).getIngredientIcon();
                    String ingredientName = direcIngredientList.get(i).getIngredientName();
                    UserRecipeIngredient userRecipeIngredient = new UserRecipeIngredient(userRecipeIdx,null,ingredientIcon,ingredientName);
                    userRecipeIngredientRepository.save(userRecipeIngredient);
                }
            }
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_SAVE_MY_RECIPE_DIRECT_INGREDIENT);
        }

        return new PostMyRecipeRes(userRecipeIdx,thumbnail,title,content,ingredientList,direcIngredientList);
    }

    /**
     * 나만의 레시피 수정 API
     * @param patchMyRecipeReq,userIdx,userRecipeIdx
     * @return PatchMyRecipeRes
     * @throws BaseException
     */
    @Transactional
    public PatchMyRecipeRes updateMyRecipe(PatchMyRecipeReq patchMyRecipeReq, Integer userIdx, Integer userRecipeIdx) throws BaseException {
        User user = userProvider.retrieveUserByUserIdx(userIdx);
        UserRecipe userRecipe;
        try {
            userRecipe = userRecipeRepository.findByUserAndUserRecipeIdxAndStatus(user,userRecipeIdx,"ACTIVE");
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_MY_RECIPE);
        }

        List<UserRecipeIngredient> userRecipeIngredientList;
        try {
            userRecipeIngredientList = userRecipeIngredientRepository.findByUserRecipeIdxAndStatus(userRecipeIdx,"ACTIVE");
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_MY_RECIPE_INGREDIENTS);
        }

        String thumbnail = patchMyRecipeReq.getThumbnail();
        String title = patchMyRecipeReq.getTitle();
        String content = patchMyRecipeReq.getContent();
        List<Integer> ingredientList = patchMyRecipeReq.getIngredientList();
        List<MyRecipeIngredient> direcIngredientList = patchMyRecipeReq.getDirectIngredientList();
        try {
            userRecipe.setThumbnail(thumbnail);
            userRecipe.setTitle(title);
            userRecipe.setContent(content);
            userRecipeRepository.save(userRecipe);


            // 재료 삭제
            for (int i=0;i<userRecipeIngredientList.size();i++){
                userRecipeIngredientList.get(i).setStatus("INACTIVE");
            }
            userRecipeIngredientRepository.saveAll(userRecipeIngredientList);

            if (ingredientList!=null) {
                for (int i = 0; i < ingredientList.size(); i++) {
                    Ingredient ingredient = ingredientProvider.retrieveIngredientByIngredientIdx(ingredientList.get(i));
                    String ingredientIcon = ingredient.getIcon();
                    String ingredientName = ingredient.getName();

                    UserRecipeIngredient userRecipeIngredient = new UserRecipeIngredient(userRecipeIdx,ingredient,ingredientIcon,ingredientName);
                    userRecipeIngredientRepository.save(userRecipeIngredient);
                }
            }

            if (direcIngredientList !=null) {
                for (int i = 0; i < direcIngredientList.size(); i++) {
                    String ingredientIcon = direcIngredientList.get(i).getIngredientIcon();
                    String ingredientName = direcIngredientList.get(i).getIngredientName();
                    UserRecipeIngredient userRecipeIngredient = new UserRecipeIngredient(userRecipeIdx,null,ingredientIcon,ingredientName);
                    userRecipeIngredientRepository.save(userRecipeIngredient);
                }
            }



        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_PATCH_MY_RECIPE);
        }
        return new PatchMyRecipeRes(thumbnail,title,content,ingredientList,direcIngredientList);


    }

    /**
     * 나만의 레시피 삭제 API
     * @param userIdx,myRecipeIdx
     * @throws BaseException
     */
    @Transactional
    public void deleteUserRecipe(Integer userIdx, Integer myRecipeIdx) throws BaseException {
        User user = userProvider.retrieveUserByUserIdx(userIdx);
        UserRecipe userRecipe;
        try {
            userRecipe = userRecipeRepository.findByUserAndUserRecipeIdxAndStatus(user,myRecipeIdx,"ACTIVE");
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_MY_RECIPE);
        }


        try {
            userRecipe.setStatus("INACTIVE");
            userRecipeRepository.save(userRecipe);


        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_DELETE_MY_RECIPE);
        }


        List<UserRecipeIngredient> userRecipeIngredientList;
        try {
            userRecipeIngredientList = userRecipeIngredientRepository.findByUserRecipeIdxAndStatus(myRecipeIdx,"ACTIVE");
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_MY_RECIPE_INGREDIENTS);
        }

        try{
            for (int i=0;i<userRecipeIngredientList.size();i++){
                userRecipeIngredientList.get(i).setStatus("INACTIVE");
            }
            userRecipeIngredientRepository.saveAll(userRecipeIngredientList);
        }catch (Exception ignored) {
            throw new BaseException(FAILED_TO_DELETE_MY_RECIPE_INGREDIENT);
        }


    }



}