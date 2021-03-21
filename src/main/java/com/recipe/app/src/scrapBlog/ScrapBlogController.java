package com.recipe.app.src.scrapBlog;

import com.recipe.app.config.BaseException;
import com.recipe.app.config.BaseResponse;
import com.recipe.app.src.scrapBlog.models.*;
import com.recipe.app.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.recipe.app.config.BaseResponseStatus.*;


@RestController
@RequestMapping("/scraps/blog")
public class ScrapBlogController {
    private final ScrapBlogProvider scrapBlogProvider;
    private final ScrapBlogService scrapBlogService;
    private final JwtService jwtService;

    @Autowired
    public ScrapBlogController(ScrapBlogProvider scrapBlogProvider, ScrapBlogService scrapBlogService , JwtService jwtService) {
        this.scrapBlogProvider = scrapBlogProvider;
        this.scrapBlogService = scrapBlogService;
        this.jwtService = jwtService;
    }

    /**
     * 블로그 스크랩하기 API
     * [POST] /scraps/blog
     * @return BaseResponse<Void>
     * @RequestBody parameters
     */

    @PostMapping("")
    public BaseResponse<Void> postScrapBlog(@RequestBody PostScrapBlogReq parameters) {
        if (parameters.getTitle() != null && parameters.getTitle() ==null) {
            return new BaseResponse<>(POST_SCRAP_BLOG_EMPTY_TITLE);
        }
        if (parameters.getThumbnail() == null && parameters.getThumbnail() !=null) {
            return new BaseResponse<>(POST_SCRAP_BLOG_EMPTY_THUMBNAIL);
        }
        if (parameters.getBlogUrl() != null && parameters.getBlogUrl() ==null) {
            return new BaseResponse<>(POST_SCRAP_BLOG_EMPTY_BLOGURL);
        }
        if (parameters.getDescription() == null && parameters.getDescription() !=null) {
            return new BaseResponse<>(POST_SCRAP_BLOG_EMPTY_DESCRIPTION);
        }
        if (parameters.getBloggerName() != null && parameters.getBloggerName() ==null) {
            return new BaseResponse<>(POST_SCRAP_BLOG_EMPTY_BLOGGER_NAME);
        }
        if (parameters.getPostDate() != null && parameters.getPostDate() ==null) {
            return new BaseResponse<>(POST_SCRAP_BLOG_EMPTY_POST_DATE);
        }

        try {
            Integer jwtUserIdx = jwtService.getUserId();
            scrapBlogService.createOrDeleteScrapBlog(jwtUserIdx, parameters);
            return new BaseResponse<>(SUCCESS);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

    }



    /**
     * 블로그 스크랩 조회 API
     * [GET] /scraps/blog
     * @return BaseResponse<List<GetScrapBlogsRes>>
     * @PageableDefault pageable
     */

    @GetMapping("")
    public BaseResponse<List<GetScrapBlogsRes>> getScrapBlogs(@RequestParam(value = "sort", required = false) Integer sort) {

        try {
            Integer jwtUserIdx = jwtService.getUserId();
            List<GetScrapBlogsRes> getScrapBlogsResList = scrapBlogProvider.retrieveScrapBlogs(jwtUserIdx, sort);
            return new BaseResponse<>(getScrapBlogsResList);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}