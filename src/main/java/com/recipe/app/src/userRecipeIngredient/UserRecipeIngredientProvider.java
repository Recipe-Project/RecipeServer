package com.recipe.app.src.userRecipeIngredient;

import com.recipe.app.config.BaseException;
import com.recipe.app.src.userRecipeIngredient.UserRecipeIngredientRepository;
import com.recipe.app.src.userRecipeIngredient.models.UserRecipeIngredient;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.recipe.app.config.BaseResponseStatus.*;

@Service
public class UserRecipeIngredientProvider {
    private final UserRecipeIngredientRepository userRecipeIngredientRepository;
    private final JwtService jwtService;

    @Autowired
    public UserRecipeIngredientProvider(UserRecipeIngredientRepository userRecipeIngredientRepository, JwtService jwtService) {
        this.userRecipeIngredientRepository = userRecipeIngredientRepository;
        this.jwtService = jwtService;
    }


    public List retrieveUserRecipeIngredient(Integer myRecipeIdx) throws BaseException {
        List<UserRecipeIngredient> userRecipeIngredientList;
        try {
            userRecipeIngredientList = userRecipeIngredientRepository.findByUserRecipeIdxAndStatus(myRecipeIdx,"ACTIVE");
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_MY_RECIPE_INGREDIENTS);
        }


        List list= userRecipeIngredientList.stream().map(userRecipeIngredient -> {
            Integer ingredientIdx = userRecipeIngredient.getIngredientIdx();
            return ingredientIdx;
        }).collect(Collectors.toList());

        return list;
    }
}