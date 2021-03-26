package com.recipe.app.src.viewBlog;

import com.recipe.app.config.BaseException;
import com.recipe.app.config.BaseResponse;
import com.recipe.app.src.viewBlog.models.*;
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
public class ViewBlogService {
    private final UserProvider userProvider;
    private final ViewBlogRepository viewBlogRepository;

    @Autowired
    public ViewBlogService(UserProvider userProvider, ViewBlogRepository viewBlogRepository){
        this.userProvider = userProvider;
        this.viewBlogRepository = viewBlogRepository;
    }

    /**
     * 블로그 조회로그 저장
     * @param jwtUserIdx, postViewBlogReq
     * @return void
     * @throws BaseException
     */
    public void createViewBlog(Integer jwtUserIdx, PostViewBlogReq postViewBlogReq) throws BaseException {
        String blogUrl = postViewBlogReq.getBlogUrl();

        User user = userProvider.retrieveUserByUserIdx(jwtUserIdx);

        ViewBlog viewBlog = new ViewBlog(user, blogUrl);

        try {
            viewBlog = viewBlogRepository.save(viewBlog);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
