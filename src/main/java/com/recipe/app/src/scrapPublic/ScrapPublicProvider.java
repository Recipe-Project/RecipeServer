package com.recipe.app.src.scrapPublic;

import com.recipe.app.src.scrapYoutube.ScrapYoutubeRepository;
import com.recipe.app.src.user.UserProvider;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;

import com.recipe.app.config.BaseException;
import com.recipe.app.src.scrapYoutube.models.GetScrapYoutubesRes;
import com.recipe.app.src.scrapYoutube.models.ScrapYoutube;
import com.recipe.app.src.user.UserProvider;
import com.recipe.app.src.user.models.User;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.recipe.app.config.BaseResponseStatus.*;


@Service
public class ScrapPublicProvider {
    private final UserProvider userProvider;
    private final ScrapPublicRepository scrapPublicRepository;
    private final JwtService jwtService;

    @Autowired
    public ScrapPublicProvider(UserProvider userProvider, ScrapPublicRepository scrapPublicRepository, JwtService jwtService) {
        this.userProvider = userProvider;
        this.scrapPublicRepository = scrapPublicRepository;
        this.jwtService = jwtService;
    }
}
