package com.recipe.app.src.viewYoutube;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.src.viewYoutube.models.PostViewsYoutubeReq;
import com.recipe.app.src.viewYoutube.models.PostViewsYoutubeRes;
import com.recipe.app.common.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.recipe.app.common.response.BaseResponse.success;
import static com.recipe.app.common.response.BaseResponseStatus.EMPTY_YOUTUBEIDX;


@RestController
@RequestMapping("/views/youtube")
public class ViewYoutubeController {
    private final ViewYoutubeService viewYoutubeService;
    private final JwtService jwtService;

    @Autowired
    public ViewYoutubeController(ViewYoutubeService viewYoutubeService, JwtService jwtService) {
        this.viewYoutubeService = viewYoutubeService;
        this.jwtService = jwtService;
    }

    /**
     * 유튜브 조회로그 저장 API
     * [POST] /views/youtube
     * @return BaseResponse<PostViewsYoutubeRes>
     * @RequestBody parameters
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostViewsYoutubeRes> postViewsYoutube(@RequestBody PostViewsYoutubeReq parameters) {
        if (parameters.getYoutubeIdx() == null && parameters.getYoutubeIdx() !=null) {
            throw new BaseException(EMPTY_YOUTUBEIDX);
        }

        Integer userIdx = jwtService.getUserId();
        PostViewsYoutubeRes postViewsYoutubeRes = viewYoutubeService.createViewYoutube(parameters,userIdx);
        return success(postViewsYoutubeRes);
    }

}
