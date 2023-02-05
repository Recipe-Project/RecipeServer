package com.recipe.app.src.scrapYoutube;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.src.scrapYoutube.models.*;
import com.recipe.app.src.user.UserProvider;
import com.recipe.app.src.user.models.User;
import com.recipe.app.common.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



import static com.recipe.app.common.response.BaseResponseStatus.*;


@Service
public class ScrapYoutubeService {
    private final UserProvider userProvider;
    private final ScrapYoutubeRepository scrapYoutubeRepository;
    private final ScrapYoutubeProvider scrapYoutubeProvider;
    private final JwtService jwtService;

    @Autowired
    public ScrapYoutubeService(UserProvider userProvider, ScrapYoutubeRepository scrapYoutubeRepository, ScrapYoutubeProvider scrapYoutubeProvider, JwtService jwtService) {
        this.userProvider = userProvider;
        this.scrapYoutubeRepository = scrapYoutubeRepository;
        this.scrapYoutubeProvider = scrapYoutubeProvider;
        this.jwtService = jwtService;
    }

    /**
     * 유튜브 스크랩하기 생성
     * @param postScrapYoutubeReq,userIdx
     * @return PostMyRecipeRes
     * @throws BaseException
     */
    public PostScrapYoutubeRes createScrapYoutube(PostScrapYoutubeReq postScrapYoutubeReq, int userIdx) throws BaseException {
        String youtubeId = postScrapYoutubeReq.getYoutubeId();
        String title = postScrapYoutubeReq.getTitle();
        String thumbnail = postScrapYoutubeReq.getThumbnail();
        String youtubeUrl = postScrapYoutubeReq.getYoutubeUrl();
        String postDate = postScrapYoutubeReq.getPostDate();
        String channelName = postScrapYoutubeReq.getChannelName();
        String playTime = postScrapYoutubeReq.getPlayTime();
        User user = userProvider.retrieveUserByUserIdx(userIdx);


        try {
            ScrapYoutube scrapYoutube = new ScrapYoutube(user, youtubeId, title, thumbnail, youtubeUrl, postDate, channelName,playTime);
            scrapYoutube = scrapYoutubeRepository.save(scrapYoutube);


        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_POST_SCRAP_YOUTUBE);
        }

        return new PostScrapYoutubeRes(userIdx,youtubeId, title,thumbnail,youtubeUrl,postDate,channelName,playTime);
    }



    /**
     * 유튜브 스크랩 취소
     * @param youtubeId,userIdx
     * @return PostScrapYoutubeRes
     * @throws BaseException
     */
    public PostScrapYoutubeRes deleteScrapYoutube(String youtubeId, Integer userIdx) throws BaseException {

        ScrapYoutube scrapYoutube = scrapYoutubeProvider.retrieveScrapYoutube(youtubeId, userIdx);
        scrapYoutube.setStatus("INACTIVE");
        String title = scrapYoutube.getTitle();
        String thumbnail = scrapYoutube.getThumbnail();
        String youtubeUrl = scrapYoutube.getYoutubeUrl();
        String postDate = scrapYoutube.getPostDate();
        String channelName = scrapYoutube.getChannelName();
        String playTime = scrapYoutube.getPlayTime();
        User user = userProvider.retrieveUserByUserIdx(userIdx);
        try {
            scrapYoutubeRepository.save(scrapYoutube);
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_POST_DELETE_SCRAP_YOUTUBE);
        }
        return new PostScrapYoutubeRes(userIdx,youtubeId, title,thumbnail,youtubeUrl,postDate,channelName,playTime);
    }

}