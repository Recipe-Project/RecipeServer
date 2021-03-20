package com.recipe.app.src.ingredient;

import com.recipe.app.config.BaseException;
import com.recipe.app.src.ingredientCategory.IngredientCategoryProvider;
import com.recipe.app.src.ingredientCategory.IngredientCategoryRepository;
import com.recipe.app.src.user.UserProvider;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IngredientService {
    private final UserProvider userProvider;
    private final IngredientCategoryProvider ingredientCategoryProvider;
    private final IngredientRepository ingredientRepository;
    private final IngredientCategoryRepository ingredientCategoryRepository;
    private final JwtService jwtService;

    @Autowired
    public IngredientService(UserProvider userProvider, IngredientCategoryProvider ingredientCategoryProvider, IngredientRepository ingredientRepository, IngredientCategoryRepository ingredientCategoryRepository, JwtService jwtService) {
        this.userProvider = userProvider;
        this.ingredientCategoryProvider = ingredientCategoryProvider;
        this.ingredientRepository = ingredientRepository;
        this.ingredientCategoryRepository = ingredientCategoryRepository;
        this.jwtService = jwtService;
    }
//
//    /**
//     * 나만의 레시피 생성
//     * @param postMyRecipeReq,userIdx
//     * @return PostMyRecipeRes
//     * @throws BaseException
//     */
//    public PostMyRecipeRes createMyRecipe(PostMyRecipeReq postMyRecipeReq, int userIdx) throws BaseException {
//
//
//        List<String> photoUrlList = postMyRecipeReq.getPhotoUrlList();
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
//            if (photoUrlList != null) {
//                for (int i = 0; i < photoUrlList.size(); i++) {
//                    UserRecipePhoto userRecipePhoto = new UserRecipePhoto(userRecipeIdx, photoUrlList.get(i));
//                    userRecipePhotoRepository.save(userRecipePhoto);
//                }
//            }
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
//        return new PostMyRecipeRes(userRecipeIdx,photoUrlList,thumbnail,title,content,ingredientList);
//    }

}
