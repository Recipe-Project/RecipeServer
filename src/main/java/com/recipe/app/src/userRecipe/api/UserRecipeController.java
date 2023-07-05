package com.recipe.app.src.userRecipe.api;

import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.common.utils.JwtService;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.userRecipe.application.UserRecipeService;
import com.recipe.app.src.userRecipe.application.dto.UserRecipeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.recipe.app.common.response.BaseResponse.success;


@RestController
@RequiredArgsConstructor
@RequestMapping("/my-recipes")
public class UserRecipeController {
    private final UserRecipeService userRecipeService;
    private final JwtService jwtService;

    @GetMapping("")
    public BaseResponse<List<UserRecipeDto.UserRecipesResponse>> getUserRecipes(final Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        List<UserRecipeDto.UserRecipesResponse> data = userRecipeService.retrieveUserRecipes(user).stream()
                .map(UserRecipeDto.UserRecipesResponse::new)
                .collect(Collectors.toList());

        return success(data);
    }

    @GetMapping("/{userRecipeIdx}")
    public BaseResponse<UserRecipeDto.UserRecipeResponse> getUserRecipe(final Authentication authentication, @PathVariable int userRecipeIdx) {
        User user = ((User) authentication.getPrincipal());
        UserRecipeDto.UserRecipeResponse data = new UserRecipeDto.UserRecipeResponse(userRecipeService.retrieveUserRecipe(user, userRecipeIdx));

        return success(data);
    }

    @ResponseBody
    @PostMapping("")
    public BaseResponse<UserRecipeDto.UserRecipeResponse> postUserRecipe(final Authentication authentication, @RequestBody UserRecipeDto.UserRecipeRequest request) {
        User user = ((User) authentication.getPrincipal());
        UserRecipeDto.UserRecipeResponse data = new UserRecipeDto.UserRecipeResponse(userRecipeService.createUserRecipe(user, request));

        return success(data);
    }

    @ResponseBody
    @PatchMapping("/{userRecipeIdx}")
    public BaseResponse<UserRecipeDto.UserRecipeResponse> patchUserRecipe(final Authentication authentication, @PathVariable int userRecipeIdx, @RequestBody UserRecipeDto.UserRecipeRequest request) {
        User user = ((User) authentication.getPrincipal());
        UserRecipeDto.UserRecipeResponse data = new UserRecipeDto.UserRecipeResponse(userRecipeService.updateUserRecipe(user, userRecipeIdx, request));

        return success(data);
    }

    @DeleteMapping("/{userRecipeIdx}")
    public BaseResponse<Void> deleteUserRecipe(final Authentication authentication, @PathVariable int userRecipeIdx) {
        User user = ((User) authentication.getPrincipal());
        userRecipeService.deleteUserRecipe(user, userRecipeIdx);

        return success();
    }

}