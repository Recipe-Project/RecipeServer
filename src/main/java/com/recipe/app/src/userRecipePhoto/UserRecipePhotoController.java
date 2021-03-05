package com.recipe.app.src.userRecipePhoto;

import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserRecipePhotoController {
    private final UserRecipePhotoProvider userRecipePhotoProvider;
    private final UserRecipePhotoService userRecipePhotoService;
    private final JwtService jwtService;

    @Autowired
    public UserRecipePhotoController(UserRecipePhotoProvider userRecipePhotoProvider, UserRecipePhotoService userRecipePhotoService, JwtService jwtService) {
        this.userRecipePhotoProvider = userRecipePhotoProvider;
        this.userRecipePhotoService = userRecipePhotoService;
        this.jwtService = jwtService;
    }

}