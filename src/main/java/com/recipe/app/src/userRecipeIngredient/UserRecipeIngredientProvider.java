package com.recipe.app.src.userRecipeIngredient;

import com.recipe.app.config.BaseException;
import com.recipe.app.src.userRecipe.models.MyRecipeIngredient;
import com.recipe.app.src.userRecipeIngredient.models.UserRecipeIngredient;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.recipe.app.config.BaseResponseStatus.FAILED_TO_GET_MY_RECIPE_INGREDIENTS;

@Service
public class UserRecipeIngredientProvider {
    private final UserRecipeIngredientRepository userRecipeIngredientRepository;
    private final JwtService jwtService;

    @Autowired
    public UserRecipeIngredientProvider(UserRecipeIngredientRepository userRecipeIngredientRepository, JwtService jwtService) {
        this.userRecipeIngredientRepository = userRecipeIngredientRepository;
        this.jwtService = jwtService;
    }


    /**
     * 나만의 레시피 상세조회 API 에서 재료리스트 조회
     * @param myRecipeIdx
     * @return GetMyRecipeRes
     * @throws BaseException
     */
    public List<MyRecipeIngredient> retrieveUserRecipeIngredient(Integer myRecipeIdx) throws BaseException {
        List<UserRecipeIngredient> userRecipeIngredientList;
        try {
            userRecipeIngredientList = userRecipeIngredientRepository.findByUserRecipeIdxAndStatus(myRecipeIdx,"ACTIVE");
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_MY_RECIPE_INGREDIENTS);
        }

        List<MyRecipeIngredient> myRecipeIngredientList = new ArrayList<>();

        for (int i=0;i<userRecipeIngredientList.size();i++){
            String ingredientIcon = userRecipeIngredientList.get(i).getIngredientIcon();
            String ingredientName = userRecipeIngredientList.get(i).getIngredientName();

            MyRecipeIngredient myRecipeIngredient = new MyRecipeIngredient(ingredientName,ingredientIcon);
            myRecipeIngredientList.add(myRecipeIngredient);

        }

        return myRecipeIngredientList;
    }
}