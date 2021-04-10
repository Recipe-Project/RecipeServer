package com.recipe.app.src.keywordYoutube;

import com.recipe.app.config.BaseException;
import com.recipe.app.config.BaseResponse;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.recipe.app.config.BaseResponseStatus.SUCCESS;


@RestController
@RequestMapping("/recipes")
public class KeywordYoutubeController {
    private final KeywordYoutubeService keywordYoutubeService;
    private final JwtService jwtService;

    @Autowired
    public KeywordYoutubeController(KeywordYoutubeService keywordYoutubeService, JwtService jwtService) {
        this.keywordYoutubeService = keywordYoutubeService;
        this.jwtService = jwtService;
    }


    /**
     * 유튜브 검색어 저장 API
     * [POST] /recipes/youtube?keyword=
     * @return BaseResponse<Void>
     */
    @PostMapping("/youtube")
    public BaseResponse<Void> postRecipeYoutubeKeyword(@RequestParam(value="keyword") String keyword) {

        try {
            int jwtUserIdx = jwtService.getUserId();

            keywordYoutubeService.createRecipeYoutubeKeyword(jwtUserIdx, keyword);

            return new BaseResponse<>(SUCCESS);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}