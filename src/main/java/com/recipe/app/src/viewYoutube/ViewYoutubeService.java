package com.recipe.app.src.viewYoutube;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.src.user.UserProvider;
import com.recipe.app.src.user.models.User;
import com.recipe.app.src.viewYoutube.models.PostViewsYoutubeReq;
import com.recipe.app.src.viewYoutube.models.PostViewsYoutubeRes;
import com.recipe.app.src.viewYoutube.models.ViewYoutube;
import com.recipe.app.common.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.recipe.app.common.response.BaseResponseStatus.FAILED_TO_POST_VIEWS_YOUTUBE;


@Service
public class ViewYoutubeService {
    private final UserProvider userProvider;
    private final ViewYoutubeRepository viewYoutubeRepository;
    private final JwtService jwtService;

    @Autowired
    public ViewYoutubeService(UserProvider userProvider, ViewYoutubeRepository viewYoutubeRepository, JwtService jwtService) {
        this.userProvider = userProvider;
        this.viewYoutubeRepository = viewYoutubeRepository;
        this.jwtService = jwtService;
    }

    /**
     * 유튜브 조회로그 저장 API
     * @param postViewsYoutubeReq,userIdx
     * @return PostMyRecipeRes
     * @throws BaseException
     */
    public PostViewsYoutubeRes createViewYoutube(PostViewsYoutubeReq postViewsYoutubeReq, int userIdx) throws BaseException {
        Integer youtubeIdx = postViewsYoutubeReq.getYoutubeIdx();
        User user = userProvider.retrieveUserByUserIdx(userIdx);


        try {
            ViewYoutube viewYoutube = new ViewYoutube(user, youtubeIdx);
            viewYoutube = viewYoutubeRepository.save(viewYoutube);


        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_POST_VIEWS_YOUTUBE);
        }

        return new PostViewsYoutubeRes(userIdx,youtubeIdx);
    }


}