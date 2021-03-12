package com.recipe.app.src.scrapYoutube;


import com.recipe.app.config.BaseException;
import com.recipe.app.src.scrapYoutube.models.GetScrapYoutubesRes;
import com.recipe.app.src.scrapYoutube.models.ScrapYoutube;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.recipe.app.config.BaseResponseStatus.*;


@Service
public class ScrapYoutubeProvider {
    private final ScrapYoutubeRepository scrapYoutubeRepository;
    private final JwtService jwtService;

    @Autowired
    public ScrapYoutubeProvider( ScrapYoutubeRepository scrapYoutubeRepository, JwtService jwtService) {
        this.scrapYoutubeRepository = scrapYoutubeRepository;
        this.jwtService = jwtService;
    }

    /**
     * 유튜브 스크랩 조회
     * @param userIdx
     * @return List<GetScrapYoutubesRes>
     * @throws BaseException
     */
    public List<GetScrapYoutubesRes> retrieveScrapYoutubeList(Integer userIdx, Pageable pageable) throws BaseException {

        Page<ScrapYoutube> scrapYoutubeList;
        try {
            scrapYoutubeList = scrapYoutubeRepository.findByUserIdxAndStatus(userIdx, "ACTIVE", pageable);
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_SCRAP_YOUTUBE);
        }

        return scrapYoutubeList.stream().map(scrapYoutube -> {
            Integer youtubeIdx= scrapYoutube.getYoutubeIdx();
            String title = scrapYoutube.getTitle();
            String thumbnail = scrapYoutube.getThumbnail();
            String youtubeUrl = scrapYoutube.getYoutubeUrl();


            return new GetScrapYoutubesRes(youtubeIdx, title, thumbnail, youtubeUrl);

        }).collect(Collectors.toList());
    }


}