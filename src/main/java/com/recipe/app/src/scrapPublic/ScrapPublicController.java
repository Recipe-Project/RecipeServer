package com.recipe.app.src.scrapPublic;

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
@RequestMapping("/scraps/recipe")
public class ScrapPublicController {
    private final ScrapPublicProvider scrapPublicProvider;
    private final ScrapPublicService scrapPublicService;
    private final JwtService jwtService;

    @Autowired
    public ScrapPublicController(ScrapPublicProvider scrapPublicProvider, ScrapPublicService scrapPublicService , JwtService jwtService) {
        this.scrapPublicProvider = scrapPublicProvider;
        this.scrapPublicService = scrapPublicService;
        this.jwtService = jwtService;
    }

}
