package com.recipe.app.src.scrapYoutube;



import com.recipe.app.config.BaseException;
import com.recipe.app.config.BaseResponse;
import com.recipe.app.src.scrapYoutube.models.*;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


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
    public BaseResponse<PostScrapYoutubeRes> postScrapsYoutube(@RequestBody PostScrapYoutubeReq parameters) {

        try {
            Integer userIdx = jwtService.getUserId();

            if (parameters.getTitle() != null && parameters.getTitle() ==null) {
                return new BaseResponse<>(EMPTY_TITLE);
            }
            if (parameters.getThumbnail() == null && parameters.getThumbnail() !=null) {
                return new BaseResponse<>(EMPTY_THUMBNAIL);
            }
            if (parameters.getYoutubeUrl() != null && parameters.getYoutubeUrl() ==null) {
                return new BaseResponse<>(EMPTY_YOUTUBEURL);
            }
            if (parameters.getPostDate() != null && parameters.getPostDate() ==null) {
                return new BaseResponse<>(EMPTY_POST_DATE);
            }
            if (parameters.getChannelName() != null && parameters.getChannelName() ==null) {
                return new BaseResponse<>(EMPTY_CHANNEL_NAME);
            }
            if (parameters.getYoutubeId() == null && parameters.getYoutubeId() !=null) {
                return new BaseResponse<>(EMPTY_YOUTUBEIDX);
            }
            if (parameters.getPlayTime() == null && parameters.getPlayTime() !=null) {
                return new BaseResponse<>(EMPTY_PLAY_TIME);
            }

            // 이미 발급 받은건지
            ScrapYoutube scrapYoutube = null;
            scrapYoutube = scrapYoutubeProvider.retrieveScrapYoutube(parameters.getYoutubeId(), userIdx);


            PostScrapYoutubeRes postScrapYoutubeRes;
            if (scrapYoutube != null) {
                postScrapYoutubeRes = scrapYoutubeService.deleteScrapYoutube(parameters.getYoutubeId(),userIdx);
                return new BaseResponse<>(postScrapYoutubeRes);
            }
            else{
                postScrapYoutubeRes = scrapYoutubeService.createScrapYoutube(parameters,userIdx);
                return new BaseResponse<>(postScrapYoutubeRes);
            }

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

    }


    /**
     * 유튜브 스크랩 조회 API
     * [GET] /scraps/youtube
     * @return BaseResponse<List<GetScrapYoutubesRes>>
     * @PageableDefault pageable
     */
    @GetMapping("")
    public BaseResponse<GetScrapYoutubesRes> getScrapsYoutube() {

        GetScrapYoutubesRes getScrapYoutubesRes = null;
        try {
            Integer userIdx = jwtService.getUserId();

            getScrapYoutubesRes = scrapYoutubeProvider.retrieveScrapYoutubeList(userIdx);

            return new BaseResponse<>(getScrapYoutubesRes);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
//
//    /**
//     * 유튜브 스크랩 조회 API
//     * [GET] /scraps/youtube
//     * @return BaseResponse<List<GetScrapYoutubesRes>>
//     * @PageableDefault pageable
//     */
//    @GetMapping("")
//    public BaseResponse<GetScrapYoutubesRes> getScrapsYoutube(@RequestParam(value = "sort") @Nullable Integer sort) {
//
//        GetScrapYoutubesRes getScrapYoutubesRes = null;
//        try {
//            Integer userIdx = jwtService.getUserId();
//
//            getScrapYoutubesRes = scrapYoutubeProvider.retrieveScrapYoutubeList(userIdx,sort);
//
//            return new BaseResponse<>(getScrapYoutubesRes);
//
//        } catch (BaseException exception) {
//            return new BaseResponse<>(exception.getStatus());
//        }
//    }


}