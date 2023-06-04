package com.recipe.app.src.viewBlog;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.src.user.application.UserService;
import com.recipe.app.src.viewBlog.models.*;
import com.recipe.app.src.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ViewBlogService {
    private final UserService userService;
    private final ViewBlogRepository viewBlogRepository;

    @Autowired
    public ViewBlogService(UserService userService, ViewBlogRepository viewBlogRepository){
        this.userService = userService;
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

        User user = userService.retrieveUserByUserIdx(jwtUserIdx);

        viewBlogRepository.save(new ViewBlog(user, blogUrl));
    }
}
