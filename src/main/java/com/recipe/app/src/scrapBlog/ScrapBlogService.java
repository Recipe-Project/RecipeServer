package com.recipe.app.src.scrapBlog;

import com.recipe.app.config.BaseException;
import com.recipe.app.src.scrapYoutube.ScrapYoutubeProvider;
import com.recipe.app.src.scrapYoutube.ScrapYoutubeRepository;
import com.recipe.app.src.scrapYoutube.models.*;
import com.recipe.app.src.user.UserProvider;
import com.recipe.app.src.user.models.User;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static com.recipe.app.config.BaseResponseStatus.*;


@Service
public class ScrapBlogService {
    private final UserProvider userProvider;
    private final ScrapBlogRepository scrapBlogRepository;
    private final ScrapBlogProvider scrapBlogProvider;
    private final JwtService jwtService;

    @Autowired
    public ScrapBlogService(UserProvider userProvider, ScrapBlogRepository scrapBlogRepository, ScrapBlogProvider scrapBlogProvider, JwtService jwtService) {
        this.userProvider = userProvider;
        this.scrapBlogRepository = scrapBlogRepository;
        this.scrapBlogProvider = scrapBlogProvider;
        this.jwtService = jwtService;
    }
}
