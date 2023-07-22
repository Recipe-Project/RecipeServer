package com.recipe.app.src.userRecipe.application;

import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.userRecipe.application.dto.UserRecipeDto;
import com.recipe.app.src.userRecipe.domain.UserRecipe;
import com.recipe.app.src.userRecipe.domain.UserRecipeIngredient;
import com.recipe.app.src.userRecipe.exception.NotFoundUserRecipeException;
import com.recipe.app.src.userRecipe.mapper.UserRecipeIngredientRepository;
import com.recipe.app.src.userRecipe.mapper.UserRecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserRecipeService {
    private final UserRecipeRepository userRecipeRepository;
    private final UserRecipeIngredientRepository userRecipeIngredientRepository;

    public List<UserRecipe> retrieveUserRecipes(User user) {
        return userRecipeRepository.findByUserAndStatusOrderByCreatedAtDesc(user, "ACTIVE");
    }

    public UserRecipe retrieveUserRecipe(User user, int myRecipeIdx) {
        return userRecipeRepository.findByUserAndUserRecipeIdxAndStatus(user, myRecipeIdx, "ACTIVE")
                .orElseThrow(NotFoundUserRecipeException::new);
    }

    @Transactional
    public UserRecipe createUserRecipe(User user, UserRecipeDto.UserRecipeRequest request) {

        UserRecipe userRecipe = userRecipeRepository.save(new UserRecipe(user, request.getThumbnail(), request.getTitle(), request.getContent()));

        List<UserRecipeIngredient> ingredients = request.getIngredientList().stream()
                .map((ingredient) -> new UserRecipeIngredient(userRecipe, null, ingredient.getIngredientIcon(), ingredient.getIngredientName()))
                .collect(Collectors.toList());
        userRecipeIngredientRepository.saveAll(ingredients);

        return userRecipe;
    }

    @Transactional
    public UserRecipe updateUserRecipe(User user, int userRecipeIdx, UserRecipeDto.UserRecipeRequest request) {

        UserRecipe userRecipe = retrieveUserRecipe(user, userRecipeIdx);
        userRecipe.changeUserRecipe(request.getThumbnail(), request.getTitle(), request.getContent());
        List<UserRecipeIngredient> ingredients = request.getIngredientList().stream()
                .map((ingredient) -> new UserRecipeIngredient(userRecipe, null, ingredient.getIngredientIcon(), ingredient.getIngredientName()))
                .collect(Collectors.toList());

        userRecipeRepository.save(userRecipe);
        userRecipeIngredientRepository.deleteAll(userRecipe.getUserRecipeIngredients());
        userRecipeIngredientRepository.saveAll(ingredients);

        return retrieveUserRecipe(user, userRecipeIdx);
    }

    @Transactional
    public void deleteUserRecipe(User user, int userRecipeIdx) {

        UserRecipe userRecipe = retrieveUserRecipe(user, userRecipeIdx);
        userRecipe.setStatus("INACTIVE");
        userRecipeRepository.save(userRecipe);
        userRecipeIngredientRepository.deleteAll(userRecipe.getUserRecipeIngredients());
    }
}