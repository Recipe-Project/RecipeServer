package com.recipe.app.src.userRecipePhoto;

import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserRecipePhotoService {
    private final UserRecipePhotoRepository userRecipePhotoRepository;
    private final UserRecipePhotoProvider userRecipePhotoProvider;
    private final JwtService jwtService;

    @Autowired
    public UserRecipePhotoService(UserRecipePhotoRepository userRecipePhotoRepository, UserRecipePhotoProvider userRecipePhotoProvider, JwtService jwtService) {
        this.userRecipePhotoRepository = userRecipePhotoRepository;
        this.userRecipePhotoProvider = userRecipePhotoProvider;
        this.jwtService = jwtService;
    }
}