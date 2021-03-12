package com.recipe.app.src.scrapYoutube;



import com.recipe.app.config.BaseException;
import com.recipe.app.config.BaseResponse;
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
@RequestMapping("/scraps/youtube")
public class ScrapYoutubeController {
    private final ScrapYoutubeProvider scrapYoutubeProvider;
    private final ScrapYoutubeService scrapYoutubeService;
    private final JwtService jwtService;

    @Autowired
    public ScrapYoutubeController(ScrapYoutubeProvider scrapYoutubeProvider, ScrapYoutubeService scrapYoutubeService , JwtService jwtService) {
        this.scrapYoutubeProvider = scrapYoutubeProvider;
        this.scrapYoutubeService = scrapYoutubeService;
        this.jwtService = jwtService;
    }

    /**
     * 유튜브 스크랩하기 API
     * [POST] /scraps/youtube
     * @return BaseResponse<PostScrapYoutubeRes>
     * @RequestBody parameters
     */
    @PostMapping("")
    public BaseResponse<PostScrapYoutubeRes> postScrapYoutube(@RequestBody PostScrapYoutubeReq parameters) {

        try {
            Integer userIdx = jwtService.getUserId();
            if (parameters.getYoutubeIdx() == null && parameters.getThumbnail() !=null) {
                return new BaseResponse<>(EMPTY_YOUTUBEIDX);
            }
            if (parameters.getTitle() != null && parameters.getThumbnail() ==null) {
                return new BaseResponse<>(EMPTY_TITLE);
            }
            if (parameters.getThumbnail() == null && parameters.getThumbnail() !=null) {
                return new BaseResponse<>(EMPTY_THUMBNAIL);
            }
            if (parameters.getYoutubeUrl() != null && parameters.getThumbnail() ==null) {
                return new BaseResponse<>(EMPTY_YOUTUBEURL);
            }

            PostScrapYoutubeRes postScrapYoutubeRes = scrapYoutubeService.createScrapYoutube(parameters,userIdx);
            return new BaseResponse<>(postScrapYoutubeRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

    }


    /**
     * 유튜브 스크랩 조회 API
     * [GET] /scraps/youtube
     * @return BaseResponse<List<GetScrapYoutubesRes>>
     */
    @GetMapping("")
    public BaseResponse<List<GetScrapYoutubesRes>> getScrapYoutube(@PageableDefault(size=10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        try {
            Integer userIdx = jwtService.getUserId();
            List<GetScrapYoutubesRes> getScrapYoutubesResList = scrapYoutubeProvider.retrieveScrapYoutubeList(userIdx,pageable);

            return new BaseResponse<>(getScrapYoutubesResList);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


}