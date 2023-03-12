package com.recipe.app.src.viewBlog;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.src.viewBlog.models.*;
import com.recipe.app.common.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.recipe.app.common.response.BaseResponse.*;
import static com.recipe.app.common.response.BaseResponseStatus.*;


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
            throw new BaseException(POST_VIEW_BLOG_EMPTY_BLOGURL);
        }

        Integer jwtUserIdx = jwtService.getUserId();
        viewBlogService.createViewBlog(jwtUserIdx, parameters);
        return success();
    }
}
