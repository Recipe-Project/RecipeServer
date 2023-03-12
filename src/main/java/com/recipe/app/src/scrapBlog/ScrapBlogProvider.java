package com.recipe.app.src.scrapBlog;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.src.scrapBlog.models.*;
import com.recipe.app.src.user.UserProvider;
import com.recipe.app.src.user.models.User;
import com.recipe.app.common.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.recipe.app.common.response.BaseResponseStatus.*;


@Service
public class ScrapBlogProvider {
    private final UserProvider userProvider;
    private final ScrapBlogRepository scrapBlogRepository;
    private final JwtService jwtService;

    @Autowired
    public ScrapBlogProvider(UserProvider userProvider, ScrapBlogRepository scrapBlogRepository, JwtService jwtService) {
        this.userProvider = userProvider;
        this.scrapBlogRepository = scrapBlogRepository;
        this.jwtService = jwtService;
    }

    /**
     * 블로그 스크랩 조회
     * @param jwtUserIdx, sort, pageable
     * @return GetScrapBlogsRes
     * @throws BaseException
     */
    public List<GetScrapBlogsRes> retrieveScrapBlogs(Integer jwtUserIdx) throws BaseException {
        User user = userProvider.retrieveUserByUserIdx(jwtUserIdx);

        List<ScrapBlog> scrapBlogList = scrapBlogRepository.findByUserAndStatusOrderByCreatedAtDesc(user, "ACTIVE");

        List<GetScrapBlogsRes> getScrapBlogsResList = new ArrayList<>();
        for(int i=0;i<scrapBlogList.size();i++) {
            ScrapBlog scrapBlog = scrapBlogList.get(i);
            Integer scrapBlogIdx = scrapBlog.getIdx();
            String title = scrapBlog.getTitle();
            String blogUrl = scrapBlog.getBlogUrl();
            String description = scrapBlog.getDescription();
            String bloggerName = scrapBlog.getBloggerName();
            Date postDate = scrapBlog.getPostDate();
            SimpleDateFormat datetime = new SimpleDateFormat("yyyy.M.d", Locale.KOREA);
            datetime.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            String date = datetime.format(postDate);
            String thumbnail = scrapBlog.getThumbnail();

            Integer userScrapCnt = scrapBlogRepository.countByBlogUrlAndStatus(blogUrl, "ACTIVE");

            getScrapBlogsResList.add(new GetScrapBlogsRes(scrapBlogIdx, title, blogUrl, description, bloggerName, date, thumbnail,userScrapCnt));
        }

        return getScrapBlogsResList;
    }
}
