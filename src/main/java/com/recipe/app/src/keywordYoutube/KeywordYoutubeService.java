package com.recipe.app.src.keywordYoutube;

import com.recipe.app.config.BaseException;
import com.recipe.app.src.keywordYoutube.models.KeywordYoutube;
import com.recipe.app.src.user.UserProvider;
import com.recipe.app.src.user.models.User;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.recipe.app.config.BaseResponseStatus.FAILED_TO_POST_YOUTUBE_KEYWORD;
@Service
public class KeywordYoutubeService {
    private final UserProvider userProvider;
    private final KeywordYoutubeRepository keywordYoutubeRepository;
    private final JwtService jwtService;

    @Autowired
    public KeywordYoutubeService(UserProvider userProvider, KeywordYoutubeRepository keywordYoutubeRepository, JwtService jwtService) {
        this.userProvider = userProvider;
        this.keywordYoutubeRepository = keywordYoutubeRepository;
        this.jwtService = jwtService;
    }


    /**
     * 유튜브 검색어 저장 API
     * @param userIdx,keyword
     * @return PostMyRecipeRes
     * @throws BaseException
     */
    public void createRecipeYoutubeKeyword(int userIdx, String keyword) throws BaseException {
        User user = userProvider.retrieveUserByUserIdx(userIdx);


        try {
            KeywordYoutube keywordYoutube = new KeywordYoutube(user, keyword);
            keywordYoutube = keywordYoutubeRepository.save(keywordYoutube);


        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_POST_YOUTUBE_KEYWORD);
        }

    }


}