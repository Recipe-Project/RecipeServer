package com.recipe.app.src.scrapYoutube;



import com.recipe.app.common.exception.BaseException;
import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.src.scrapYoutube.models.GetScrapYoutubesRes;
import com.recipe.app.src.scrapYoutube.models.PostScrapYoutubeReq;
import com.recipe.app.src.scrapYoutube.models.PostScrapYoutubeRes;
import com.recipe.app.src.scrapYoutube.models.ScrapYoutube;
import com.recipe.app.common.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.recipe.app.common.response.BaseResponse.success;
import static com.recipe.app.common.response.BaseResponseStatus.*;


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
            Integer userIdx = jwtService.getUserId();

            if (parameters.getTitle() == null || parameters.getTitle().length()==0) {
                throw new BaseException(EMPTY_TITLE);
            }
            if (parameters.getThumbnail() == null || parameters.getThumbnail().length()==0) {
                throw new BaseException(EMPTY_THUMBNAIL);
            }
            if (parameters.getYoutubeUrl() == null || parameters.getYoutubeUrl().length()==0) {
                throw new BaseException(EMPTY_YOUTUBEURL);
            }
            if (parameters.getPostDate() == null || parameters.getPostDate().length()==0) {
                throw new BaseException(EMPTY_POST_DATE);
            }
            if (parameters.getChannelName() == null || parameters.getChannelName().length()==0) {
                throw new BaseException(EMPTY_CHANNEL_NAME);
            }
            if (parameters.getYoutubeId() == null || parameters.getYoutubeId().length()==0) {
                throw new BaseException(EMPTY_YOUTUBEIDX);
            }
            if (parameters.getPlayTime() == null || parameters.getPlayTime().length()==0) {
                throw new BaseException(EMPTY_PLAY_TIME);
            }
            if(!parameters.getThumbnail().matches("([^\\s]+(\\.(?i)(jpg|png|gif|pdf))$)")){
                throw new BaseException(INVALID_THUMBNAIL);
            }
            if (!parameters.getYoutubeUrl().matches("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")) {
                throw new BaseException(INVALID_YOUTUBE_URL);
            }
            if (!parameters.getPostDate().matches("^\\d{4}\\.(0[1-9]|1[012])\\.(0[1-9]|[12][0-9]|3[01])$")) {
                throw new BaseException(INVALID_DATE);
            }
//            if (!parameters.getPlayTime().matches("^([0-5]?[0-9]:[0-5][0-9]):[0-5][0-9]$")|!parameters.getPlayTime().matches("^([1-2]?[0-9]:[0-5][0-9]):[0-5][0-9]:[0-5][0-9]$")) { // 1~23
//                return new BaseResponse<>(INVALID_PLAY_TIME);
//            }


            // 이미 발급 받은건지
            ScrapYoutube scrapYoutube = null;
            scrapYoutube = scrapYoutubeProvider.retrieveScrapYoutube(parameters.getYoutubeId(), userIdx);


            PostScrapYoutubeRes postScrapYoutubeRes;
            if (scrapYoutube != null) {
                postScrapYoutubeRes = scrapYoutubeService.deleteScrapYoutube(parameters.getYoutubeId(),userIdx);
                return success(postScrapYoutubeRes);
            }
            else{
                // 새로 만들지
                postScrapYoutubeRes = scrapYoutubeService.createScrapYoutube(parameters,userIdx);
                return success(postScrapYoutubeRes);
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
        Integer userIdx = jwtService.getUserId();

        GetScrapYoutubesRes getScrapYoutubesRes = scrapYoutubeProvider.retrieveScrapYoutubeList(userIdx);

        return success(getScrapYoutubesRes);
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