package com.recipe.app.src.scrapYoutube;


import com.recipe.app.config.BaseException;
import com.recipe.app.src.scrapYoutube.models.GetScrapYoutubesRes;
import com.recipe.app.src.scrapYoutube.models.ScrapYoutube;
import com.recipe.app.src.scrapYoutube.models.ScrapYoutubeList;
import com.recipe.app.src.user.UserProvider;
import com.recipe.app.src.user.models.User;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.recipe.app.config.BaseResponseStatus.*;


@Service
public class ScrapYoutubeProvider {
    private final UserProvider userProvider;
    private final ScrapYoutubeRepository scrapYoutubeRepository;
    private final JwtService jwtService;

    @Autowired
    public ScrapYoutubeProvider(UserProvider userProvider, ScrapYoutubeRepository scrapYoutubeRepository, JwtService jwtService) {
        this.userProvider = userProvider;
        this.scrapYoutubeRepository = scrapYoutubeRepository;
        this.jwtService = jwtService;
    }

    /**
     * 유튜브 스크랩 조회
     * @param userIdx,sort,pageable
     * @return GetScrapYoutubesRes
     * @throws BaseException
     */
    public GetScrapYoutubesRes retrieveScrapYoutubes(Integer userIdx, Integer sort) throws BaseException {
        User user = userProvider.retrieveUserByUserIdx(userIdx);
        List<ScrapYoutubeList> scrapYoutubeList = null;

        try {
            if (sort==null || sort==1){
                scrapYoutubeList = retrieveScrapYoutubesSortedByViewCount(userIdx);
            }
            else if(sort==2){
                scrapYoutubeList= retrieveScrapYoutubesSortedByCreatedDate(userIdx);
            }
            else if(sort==3){
                scrapYoutubeList= retrieveScrapYoutubesSortedByScrapCount(userIdx);
            }


            Long scrapYoutubeCount = scrapYoutubeRepository.countByUserAndStatus(user,"ACTIVE");

            return new GetScrapYoutubesRes(scrapYoutubeCount,scrapYoutubeList);
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_SCRAP_YOUTUBE);
        }
    }

    /**
     * 유튜브 스크랩 조회 - 조회수
     * @param userIdx
     * @return List<ScrapYoutubeList>
     * @throws BaseException
     */
    public List<ScrapYoutubeList> retrieveScrapYoutubesSortedByViewCount(Integer userIdx) throws BaseException {


        User user = userProvider.retrieveUserByUserIdx(userIdx);
        List<ScrapYoutube> scrapYoutubeList;

        try {
            scrapYoutubeList = scrapYoutubeRepository.findByUserAndStatus(user, "ACTIVE",Sort.by("createdAt").descending());
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_SCRAP_YOUTUBE);
        }

        return scrapYoutubeList.stream().map(scrapYoutube -> {
            Integer youtubeIdx = scrapYoutube.getYoutubeIdx();
            String title = scrapYoutube.getTitle();
            String thumbnail = scrapYoutube.getThumbnail();
            String youtubeUrl = scrapYoutube.getYoutubeUrl();
            String postDate = scrapYoutube.getPostDate();
            String channelName = scrapYoutube.getChannelName();
            Long heartCount = scrapYoutubeRepository.countByYoutubeIdxAndStatus(youtubeIdx,"ACTIVE");

            return new ScrapYoutubeList(userIdx,youtubeIdx,title,thumbnail,heartCount,youtubeUrl,postDate,channelName);

        }).collect(Collectors.toList());
    }

    /**
     * 유튜브 스크랩 조회 - 최신날짜순
     * @param userIdx
     * @return List<ScrapYoutubeList>
     * @throws BaseException
     */
    public List<ScrapYoutubeList> retrieveScrapYoutubesSortedByCreatedDate(Integer userIdx) throws BaseException {

        User user = userProvider.retrieveUserByUserIdx(userIdx);
        List<ScrapYoutube> scrapYoutubeList;

        try {
            scrapYoutubeList = scrapYoutubeRepository.findByUserAndStatus(user, "ACTIVE",Sort.by("createdAt").descending());
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_SCRAP_YOUTUBE);
        }

        return scrapYoutubeList.stream().map(scrapYoutube -> {
            Integer youtubeIdx = scrapYoutube.getYoutubeIdx();
            String title = scrapYoutube.getTitle();
            String thumbnail = scrapYoutube.getThumbnail();
            String youtubeUrl = scrapYoutube.getYoutubeUrl();
            String postDate = scrapYoutube.getPostDate();
            String channelName = scrapYoutube.getChannelName();
            Long heartCount = scrapYoutubeRepository.countByYoutubeIdxAndStatus(youtubeIdx,"ACTIVE");

            return new ScrapYoutubeList(userIdx,youtubeIdx,title,thumbnail,heartCount,youtubeUrl,postDate,channelName);

        }).collect(Collectors.toList());
    }



    /**
     * 유튜브 스크랩 조회 - 좋아요순
     * @param userIdx
     * @return List<ScrapYoutubeList>
     * @throws BaseException
     */
    public List<ScrapYoutubeList> retrieveScrapYoutubesSortedByScrapCount(Integer userIdx) throws BaseException {

        User user = userProvider.retrieveUserByUserIdx(userIdx);


        List<ScrapYoutube> scrapYoutubeList;
        try {
            scrapYoutubeList = scrapYoutubeRepository.findByUserAndStatus(user, "ACTIVE");
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_SCRAP_YOUTUBE);
        }

        Map<Integer,Long> map = new HashMap<Integer,Long>();
        Long count;
        for(ScrapYoutube sc : scrapYoutubeList) {
            Integer youtubeIdx =  sc.getYoutubeIdx();
            count = scrapYoutubeRepository.countByYoutubeIdxAndStatus(youtubeIdx,"ACTIVE");
            map.put(youtubeIdx,count);
        }


        List<Integer> keySetList = new ArrayList<>(map.keySet());
        // 내림차순
        Collections.sort(keySetList, (o1, o2) -> (map.get(o2).compareTo(map.get(o1))));
        // 결과 keySetList
        List<ScrapYoutubeList> syList = new ArrayList<>();
        ScrapYoutube scrapYoutube;
        for(Integer key : keySetList) {
            scrapYoutube = scrapYoutubeRepository.findByYoutubeIdxAndUserAndStatus(key,user,"ACTIVE");
            Integer youtubeIdx = scrapYoutube.getYoutubeIdx();
            String title = scrapYoutube.getTitle();
            String thumbnail = scrapYoutube.getThumbnail();
            String youtubeUrl = scrapYoutube.getYoutubeUrl();
            String postDate = scrapYoutube.getPostDate();
            String channelName = scrapYoutube.getChannelName();
            Long heartCount = map.get(key);
            ScrapYoutubeList sc = new ScrapYoutubeList(userIdx,youtubeIdx,title,thumbnail,heartCount,youtubeUrl,postDate,channelName);
            syList.add(sc);
        }
        return syList;
    }



    /**
     * 유튜브 스크랩 하기에서 특정게시글 스크랩 조회
     * @param userIdx
     * @return ScrapYoutube
     * @throws BaseException
     */
    public ScrapYoutube retrieveScrapYoutube(Integer youtubeIdx, Integer userIdx) throws BaseException {
        ScrapYoutube scrapYoutube;
        User user = userProvider.retrieveUserByUserIdx(userIdx);
        try {
            scrapYoutube = scrapYoutubeRepository.findByYoutubeIdxAndUserAndStatus(youtubeIdx,user,"ACTIVE");
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_SCRAP_YOUTUBE);
        }


        return scrapYoutube;
    }


}