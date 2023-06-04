package com.recipe.app.src.viewYoutube;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.src.user.application.UserService;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.viewYoutube.models.PostViewsYoutubeReq;
import com.recipe.app.src.viewYoutube.models.PostViewsYoutubeRes;
import com.recipe.app.src.viewYoutube.models.ViewYoutube;
import com.recipe.app.common.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ViewYoutubeService {
    private final UserService userService;
    private final ViewYoutubeRepository viewYoutubeRepository;
    private final JwtService jwtService;

    @Autowired
    public ViewYoutubeService(UserService userService, ViewYoutubeRepository viewYoutubeRepository, JwtService jwtService) {
        this.userService = userService;
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
        User user = userService.retrieveUserByUserIdx(userIdx);

        viewYoutubeRepository.save(new ViewYoutube(user, youtubeIdx));

        return new PostViewsYoutubeRes(userIdx,youtubeIdx);
    }


}