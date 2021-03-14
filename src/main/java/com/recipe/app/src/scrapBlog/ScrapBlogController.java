package com.recipe.app.src.scrapBlog;

import com.recipe.app.config.BaseException;
import com.recipe.app.config.BaseResponse;
import com.recipe.app.src.scrapYoutube.ScrapYoutubeProvider;
import com.recipe.app.src.scrapYoutube.ScrapYoutubeService;
import com.recipe.app.src.scrapYoutube.models.*;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.recipe.app.config.BaseResponseStatus.*;


@RestController
@RequestMapping("/scraps/blog")
public class ScrapBlogController {
    private final ScrapBlogProvider scrapBlogProvider;
    private final ScrapBlogService scrapBlogService;
    private final JwtService jwtService;

    @Autowired
    public ScrapBlogController(ScrapBlogProvider scrapBlogProvider, ScrapBlogService scrapBlogService , JwtService jwtService) {
        this.scrapBlogProvider = scrapBlogProvider;
        this.scrapBlogService = scrapBlogService;
        this.jwtService = jwtService;
    }


}
