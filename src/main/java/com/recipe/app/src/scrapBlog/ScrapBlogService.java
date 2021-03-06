package com.recipe.app.src.scrapBlog;

import com.recipe.app.config.BaseException;
import com.recipe.app.config.BaseResponse;
import com.recipe.app.src.scrapBlog.models.*;
import com.recipe.app.src.user.UserProvider;
import com.recipe.app.src.user.models.User;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static com.recipe.app.config.BaseResponseStatus.*;


@Service
public class ScrapBlogService {
    private final UserProvider userProvider;
    private final ScrapBlogRepository scrapBlogRepository;
    private final ScrapBlogProvider scrapBlogProvider;
    private final JwtService jwtService;

    @Autowired
    public ScrapBlogService(UserProvider userProvider, ScrapBlogRepository scrapBlogRepository, ScrapBlogProvider scrapBlogProvider, JwtService jwtService) {
        this.userProvider = userProvider;
        this.scrapBlogRepository = scrapBlogRepository;
        this.scrapBlogProvider = scrapBlogProvider;
        this.jwtService = jwtService;
    }

    /**
     * 블로그 스크랩하기 생성/삭제
     * @param jwtUserIdx, postScrapBlogReq
     * @return void
     * @throws BaseException
     */
    public void createOrDeleteScrapBlog(Integer jwtUserIdx, PostScrapBlogReq postScrapBlogReq) throws BaseException {
        String title = postScrapBlogReq.getTitle();
        String thumbnail = postScrapBlogReq.getThumbnail();
        String blogUrl = postScrapBlogReq.getBlogUrl();
        String description = postScrapBlogReq.getDescription();
        String bloggerName = postScrapBlogReq.getBloggerName();
        String postDate = postScrapBlogReq.getPostDate();
        SimpleDateFormat datetime = new SimpleDateFormat("yyyy.M.d", Locale.KOREA);
        datetime.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        Date date;
        try {
            date = datetime.parse(postDate);
        }catch (Exception e){
            throw new BaseException(DATE_PARSE_ERROR);
        }

        User user = userProvider.retrieveUserByUserIdx(jwtUserIdx);

        ScrapBlog scrapBlog = null;
        try {
            scrapBlog = scrapBlogRepository.findByUserAndBlogUrlAndStatus(user, blogUrl, "ACTIVE");
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }

        if (scrapBlog == null) {
            scrapBlog = new ScrapBlog(user, title, thumbnail, blogUrl, description, bloggerName, date);
        }
        else{
            if(scrapBlog.getStatus().equals("ACTIVE")){
                scrapBlog.setStatus("INACTIVE");
            }
            else if(scrapBlog.getStatus().equals("INACTIVE")){
                scrapBlog.setStatus("ACTIVE");
            }
        }

        try {
            scrapBlog = scrapBlogRepository.save(scrapBlog);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
