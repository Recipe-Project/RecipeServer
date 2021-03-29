package com.recipe.app.src.scrapYoutube;

import com.recipe.app.config.BaseException;
import com.recipe.app.src.scrapYoutube.models.*;
import com.recipe.app.src.user.UserProvider;
import com.recipe.app.src.user.models.User;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



import static com.recipe.app.config.BaseResponseStatus.*;


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
        Integer youtubeIdx = postScrapYoutubeReq.getYoutubeIdx();
        String title = postScrapYoutubeReq.getTitle();
        String thumbnail = postScrapYoutubeReq.getThumbnail();
        String youtubeUrl = postScrapYoutubeReq.getYoutubeUrl();
        String postDate = postScrapYoutubeReq.getPostDate();
        String channelName = postScrapYoutubeReq.getChannelName();
        String playTime = postScrapYoutubeReq.getPlayTime();
        User user = userProvider.retrieveUserByUserIdx(userIdx);


        try {
            ScrapYoutube scrapYoutube = new ScrapYoutube(user, youtubeIdx, title, thumbnail, youtubeUrl, postDate, channelName,playTime);
            scrapYoutube = scrapYoutubeRepository.save(scrapYoutube);


        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_POST_SCRAP_YOUTUBE);
        }

        return new PostScrapYoutubeRes(userIdx,youtubeIdx, title,thumbnail,youtubeUrl,postDate,channelName,playTime);
    }



    /**
     * 유튜브 스크랩 취소
     * @param youtubeIdx,userIdx
     * @return PostScrapYoutubeRes
     * @throws BaseException
     */
    public PostScrapYoutubeRes deleteScrapYoutube(Integer youtubeIdx, Integer userIdx) throws BaseException {

        ScrapYoutube scrapYoutube = scrapYoutubeProvider.retrieveScrapYoutube(youtubeIdx, userIdx);
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
        return new PostScrapYoutubeRes(userIdx,youtubeIdx, title,thumbnail,youtubeUrl,postDate,channelName,playTime);
    }

}