package com.recipe.app.src.viewBlog;

import com.recipe.app.config.BaseException;
import com.recipe.app.config.BaseResponse;
import com.recipe.app.src.viewBlog.models.*;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.recipe.app.config.BaseResponseStatus.*;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.*;


@RestController
@RequestMapping("/views/blog")
public class ViewBlogController {
    private final ViewBlogService viewBlogService;
    private final JwtService jwtService;

    @Autowired
    public ViewBlogController(ViewBlogService viewBlogService, JwtService jwtService){
        this.viewBlogService = viewBlogService;
        this.jwtService = jwtService;
    }

    /**
     * 블로그 조회로그 저장 API
     * [POST] /views/blog
     * @return BaseResponse<Void>
     * @RequestBody parameters
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<Void> postViewBlog(@RequestBody PostViewBlogReq parameters) {
        if (parameters.getBlogUrl() == null || parameters.getBlogUrl().length()==0) {
            return new BaseResponse<>(POST_VIEW_BLOG_EMPTY_BLOGURL);
        }

        try {
            Integer jwtUserIdx = jwtService.getUserId();
            viewBlogService.createViewBlog(jwtUserIdx, parameters);
            return new BaseResponse<>(SUCCESS);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

    }
}
