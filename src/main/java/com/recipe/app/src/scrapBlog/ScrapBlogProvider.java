package com.recipe.app.src.scrapBlog;

import com.recipe.app.config.BaseException;
import com.recipe.app.src.scrapBlog.models.*;
import com.recipe.app.src.scrapYoutube.models.ScrapYoutubeList;
import com.recipe.app.src.user.UserProvider;
import com.recipe.app.src.user.models.User;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.recipe.app.config.BaseResponseStatus.*;


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

        List<ScrapBlog> scrapBlogList;
        try{
            scrapBlogList = scrapBlogRepository.findByUserAndStatusOrderByCreatedAtDesc(user, "ACTIVE");
        }catch(Exception e){
            throw new BaseException(DATABASE_ERROR);
        }

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

            Integer userScrapCnt = 0;
            try{
                userScrapCnt = scrapBlogRepository.countByBlogUrlAndStatus(blogUrl, "ACTIVE");
            }catch(Exception e){
                throw new BaseException(DATABASE_ERROR);
            }

            getScrapBlogsResList.add(new GetScrapBlogsRes(scrapBlogIdx, title, blogUrl, description, bloggerName, date, thumbnail,userScrapCnt));
        }

        return getScrapBlogsResList;
    }
}
