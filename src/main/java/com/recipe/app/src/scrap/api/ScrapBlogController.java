package com.recipe.app.src.scrap.api;

import com.recipe.app.common.response.BaseResponse;
import com.recipe.app.src.scrap.application.ScrapBlogService;
import com.recipe.app.src.scrap.application.dto.ScrapBlogDto;
import com.recipe.app.src.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.recipe.app.common.response.BaseResponse.success;

@RestController
@RequiredArgsConstructor
@RequestMapping("/scraps/blog")
public class ScrapBlogController {

    private final ScrapBlogService scrapBlogService;

    @ResponseBody
    @PostMapping("")
    public BaseResponse<Void> postScrapBlog(final Authentication authentication, @RequestBody ScrapBlogDto.ScrapBlogRequest request) {
        User user = ((User) authentication.getPrincipal());
        scrapBlogService.createOrDeleteScrapBlog(user, request);

        return success();
    }

    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<ScrapBlogDto.ScrapBlogResponse>> getScrapBlogs(final Authentication authentication) {
        User user = ((User) authentication.getPrincipal());
        List<ScrapBlogDto.ScrapBlogResponse> data = scrapBlogService.retrieveScrapBlogs(user).stream()
                .map((sb) -> new ScrapBlogDto.ScrapBlogResponse(sb, scrapBlogService.countScrapBlogs(sb.getBlogUrl())))
                .collect(Collectors.toList());

        return success(data);
    }
}