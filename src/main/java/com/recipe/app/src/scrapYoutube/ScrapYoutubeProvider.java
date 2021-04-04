package com.recipe.app.src.scrapYoutube;


import com.recipe.app.config.BaseException;
import com.recipe.app.src.scrapYoutube.models.GetScrapYoutubesRes;
import com.recipe.app.src.scrapYoutube.models.ScrapYoutube;
import com.recipe.app.src.scrapYoutube.models.ScrapYoutubeList;
import com.recipe.app.src.user.UserProvider;
import com.recipe.app.src.user.models.User;
import com.recipe.app.src.viewYoutube.ViewYoutubeRepository;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.recipe.app.config.BaseResponseStatus.*;


@Service
public class ScrapYoutubeProvider {
    private final UserProvider userProvider;
    private final ScrapYoutubeRepository scrapYoutubeRepository;
    private final ViewYoutubeRepository viewYoutubeRepository;
    private final JwtService jwtService;

    @Autowired
    public ScrapYoutubeProvider(UserProvider userProvider, ScrapYoutubeRepository scrapYoutubeRepository, ViewYoutubeRepository viewYoutubeRepository, JwtService jwtService) {
        this.userProvider = userProvider;
        this.scrapYoutubeRepository = scrapYoutubeRepository;
        this.viewYoutubeRepository = viewYoutubeRepository;
        this.jwtService = jwtService;
    }

    /**
     * 유튜브 스크랩 조회
     * @param userIdx,sort
     * @return GetScrapYoutubesRes
     * @throws BaseException
     */
    public GetScrapYoutubesRes retrieveScrapYoutubeList(Integer userIdx) throws BaseException {
        User user = userProvider.retrieveUserByUserIdx(userIdx);
        List<ScrapYoutubeList> scrapYoutubeList = null;

        try {

            scrapYoutubeList= retrieveScrapYoutubesSortedByCreatedDate(userIdx);
            Long scrapYoutubeCount = scrapYoutubeRepository.countByUserAndStatus(user,"ACTIVE");

            return new GetScrapYoutubesRes(scrapYoutubeCount,scrapYoutubeList);
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_SCRAP_YOUTUBE);
        }
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
            String youtubeId = scrapYoutube.getYoutubeId();
            String title = scrapYoutube.getTitle(); //제목 30자
            if (title.length()>30){
                title = title.substring(0,30)+"...";
            }
            String thumbnail = scrapYoutube.getThumbnail();
            String youtubeUrl = scrapYoutube.getYoutubeUrl();
            String postDate = scrapYoutube.getPostDate();
            String channelName = scrapYoutube.getChannelName();
            String playTime = scrapYoutube.getPlayTime();
            Long heartCount = scrapYoutubeRepository.countByYoutubeIdAndStatus(youtubeId,"ACTIVE");

            return new ScrapYoutubeList(userIdx,youtubeId,title,thumbnail,heartCount,youtubeUrl,postDate,channelName,playTime);

        }).collect(Collectors.toList());
    }

    /**
     * 유튜브 스크랩 하기에서 특정게시글 스크랩 조회
     * @param youtubeId,userIdx
     * @return ScrapYoutube
     * @throws BaseException
     */
    public ScrapYoutube retrieveScrapYoutube(String youtubeId, Integer userIdx) throws BaseException {
        ScrapYoutube scrapYoutube;
        User user = userProvider.retrieveUserByUserIdx(userIdx);
        try {
            scrapYoutube = scrapYoutubeRepository.findByYoutubeIdAndUserAndStatus(youtubeId,user,"ACTIVE");
        } catch (Exception ignored) {
            throw new BaseException(FAILED_TO_GET_SCRAP_YOUTUBE);
        }



        return scrapYoutube;
    }

//    /**
//     * 유튜브 스크랩 조회
//     * @param userIdx,sort,pageable
//     * @return GetScrapYoutubesRes
//     * @throws BaseException
//     */
//    public GetScrapYoutubesRes retrieveScrapYoutubeList(Integer userIdx, Integer sort) throws BaseException {
//        User user = userProvider.retrieveUserByUserIdx(userIdx);
//        List<ScrapYoutubeList> scrapYoutubeList = null;
//
//        try {
//            if (sort==null || sort==1){ // 조회수
//                scrapYoutubeList = retrieveScrapYoutubesSortedByViewCount(userIdx);
//            }
//            else if(sort==2){ //최신순
//                scrapYoutubeList= retrieveScrapYoutubesSortedByCreatedDate(userIdx);
//            }
//            else if(sort==3){ //스크랩수
//                scrapYoutubeList= retrieveScrapYoutubesSortedByScrapCount(userIdx);
//            }
//
//
//            Long scrapYoutubeCount = scrapYoutubeRepository.countByUserAndStatus(user,"ACTIVE");
//
//            return new GetScrapYoutubesRes(scrapYoutubeCount,scrapYoutubeList);
//        } catch (Exception ignored) {
//            throw new BaseException(FAILED_TO_GET_SCRAP_YOUTUBE);
//        }
//    }
//
//    /**
//     * 유튜브 스크랩 조회 - 조회수
//     * @param userIdx
//     * @return List<ScrapYoutubeList>
//     * @throws BaseException
//     */
//    public List<ScrapYoutubeList> retrieveScrapYoutubesSortedByViewCount(Integer userIdx) throws BaseException {
//
//
//        User user = userProvider.retrieveUserByUserIdx(userIdx);
//
//
//        // 유저가 스크랩한 리스트 조회
//        List<ScrapYoutube> scrapYoutubeList;
//        try {
//            scrapYoutubeList = scrapYoutubeRepository.findByUserAndStatus(user, "ACTIVE");
//        } catch (Exception ignored) {
//            throw new BaseException(FAILED_TO_GET_SCRAP_YOUTUBE);
//        }
//
//        // 위에서 가져온 리스트에서 유튜브인덱스 뽑아서 ViewYoutube 테이블에서 인덱스에 해당하는 조회수 세기
//        Map<Integer,Long> map = new HashMap<Integer,Long>();
//        Long count;
//        for(ScrapYoutube sc : scrapYoutubeList) {
//            Integer youtubeIdx =  sc.getYoutubeIdx();
//            count = viewYoutubeRepository.countByYoutubeIdxAndStatus(youtubeIdx,"ACTIVE");
//            map.put(youtubeIdx,count);
//        }
//
//
//        List<Integer> keySetList = new ArrayList<>(map.keySet());
//        // 조회순대로 유트브인덱스 내림차순
//        Collections.sort(keySetList, (o1, o2) -> (map.get(o2).compareTo(map.get(o1))));
//        // 결과 keySetList
//        List<ScrapYoutubeList> syList = new ArrayList<>();
//        ScrapYoutube scrapYoutube;
//        for(Integer key : keySetList) {
//            scrapYoutube = scrapYoutubeRepository.findByYoutubeIdxAndUserAndStatus(key,user,"ACTIVE");
//            Integer youtubeIdx = scrapYoutube.getYoutubeIdx();
//            String title = scrapYoutube.getTitle(); // 30자 제한
//            String thumbnail = scrapYoutube.getThumbnail();
//            String youtubeUrl = scrapYoutube.getYoutubeUrl();
//            String postDate = scrapYoutube.getPostDate();
//            String channelName = scrapYoutube.getChannelName();
//            Long heartCount = map.get(key);
//            ScrapYoutubeList sc = new ScrapYoutubeList(userIdx,youtubeIdx,title,thumbnail,heartCount,youtubeUrl,postDate,channelName);
//            syList.add(sc);
//        }
//        return syList;
//    }




//    /**
//     * 유튜브 스크랩 조회 - 좋아요순
//     * @param userIdx
//     * @return List<ScrapYoutubeList>
//     * @throws BaseException
//     */
//    public List<ScrapYoutubeList> retrieveScrapYoutubesSortedByScrapCount(Integer userIdx) throws BaseException {
//
//        User user = userProvider.retrieveUserByUserIdx(userIdx);
//
//
//        List<ScrapYoutube> scrapYoutubeList;
//        try {
//            scrapYoutubeList = scrapYoutubeRepository.findByUserAndStatus(user, "ACTIVE");
//        } catch (Exception ignored) {
//            throw new BaseException(FAILED_TO_GET_SCRAP_YOUTUBE);
//        }
//
//        Map<Integer,Long> map = new HashMap<Integer,Long>();
//        Long count;
//        for(ScrapYoutube sc : scrapYoutubeList) {
//            Integer youtubeIdx =  sc.getYoutubeIdx();
//            count = scrapYoutubeRepository.countByYoutubeIdxAndStatus(youtubeIdx,"ACTIVE");
//            map.put(youtubeIdx,count);
//        }
//
//
//        List<Integer> keySetList = new ArrayList<>(map.keySet());
//        // 내림차순
//        Collections.sort(keySetList, (o1, o2) -> (map.get(o2).compareTo(map.get(o1))));
//        // 결과 keySetList
//        List<ScrapYoutubeList> syList = new ArrayList<>();
//        ScrapYoutube scrapYoutube;
//        for(Integer key : keySetList) {
//            scrapYoutube = scrapYoutubeRepository.findByYoutubeIdxAndUserAndStatus(key,user,"ACTIVE");
//            Integer youtubeIdx = scrapYoutube.getYoutubeIdx();
//            String title = scrapYoutube.getTitle();
//            String thumbnail = scrapYoutube.getThumbnail();
//            String youtubeUrl = scrapYoutube.getYoutubeUrl();
//            String postDate = scrapYoutube.getPostDate();
//            String channelName = scrapYoutube.getChannelName();
//            Long heartCount = map.get(key);
//            ScrapYoutubeList sc = new ScrapYoutubeList(userIdx,youtubeIdx,title,thumbnail,heartCount,youtubeUrl,postDate,channelName);
//            syList.add(sc);
//        }
//        return syList;
//    }





}