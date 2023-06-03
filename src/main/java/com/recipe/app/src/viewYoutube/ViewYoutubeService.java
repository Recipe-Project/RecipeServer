package com.recipe.app.src.viewYoutube;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.viewYoutube.models.PostViewsYoutubeReq;
import com.recipe.app.src.viewYoutube.models.PostViewsYoutubeRes;
import com.recipe.app.src.viewYoutube.models.ViewYoutube;
import com.recipe.app.common.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        viewYoutubeRepository.save(new ViewYoutube(user, youtubeIdx));

        return new PostViewsYoutubeRes(userIdx,youtubeIdx);
    }


}