package com.recipe.app.src.scrapBlog;

import com.recipe.app.config.BaseException;
import com.recipe.app.src.scrapYoutube.ScrapYoutubeRepository;
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
public class ScrapBlogProvider {
    private final UserProvider userProvider;
    private final ScrapBlogRepository scrapBlogRepository;
    private final JwtService jwtService;

    @Autowired
    public ScrapBlogProvider(UserProvider userProvider, ScrapBlogRepository scrapBlogRepository, JwtService jwtService) {
        this.userProvider = userProvider;
        this.scrapBlogRepository = scrapBlogRepository;
        this.jwtService = jwtService;
    }
}
