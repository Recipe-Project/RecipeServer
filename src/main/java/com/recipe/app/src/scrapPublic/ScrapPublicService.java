package com.recipe.app.src.scrapPublic;

import com.recipe.app.src.scrapYoutube.ScrapYoutubeProvider;
import com.recipe.app.src.scrapYoutube.ScrapYoutubeRepository;
import com.recipe.app.src.user.UserProvider;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;

import com.recipe.app.config.BaseException;
import com.recipe.app.src.scrapYoutube.models.*;
import com.recipe.app.src.user.UserProvider;
import com.recipe.app.src.user.models.User;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static com.recipe.app.config.BaseResponseStatus.*;


@Service
public class ScrapPublicService {
    private final UserProvider userProvider;
    private final ScrapPublicRepository scrapPublicRepository;
    private final ScrapPublicProvider scrapPublicProvider;
    private final JwtService jwtService;

    @Autowired
    public ScrapPublicService(UserProvider userProvider, ScrapPublicRepository scrapPublicRepository, ScrapPublicProvider scrapPublicProvider, JwtService jwtService) {
        this.userProvider = userProvider;
        this.scrapPublicRepository = scrapPublicRepository;
        this.scrapPublicProvider = scrapPublicProvider;
        this.jwtService = jwtService;
    }
}
