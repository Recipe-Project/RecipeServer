package com.recipe.app.src.common.client;

import com.recipe.app.src.common.client.dto.NaverAuthResponse;
import com.recipe.app.src.common.client.dto.NaverBlogSearchResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "naver-client", url = "https://openapi.naver.com")
public interface NaverFeignClient {

    @GetMapping("/v1/search/blog")
    NaverBlogSearchResponse searchNaverBlog(
            @RequestHeader("X-Naver-Client-Id") String clientId,
            @RequestHeader("X-Naver-Client-Secret") String clientSecret,
            @RequestParam(value = "start") int start,
            @RequestParam(value = "display") int display,
            @RequestParam(value = "sort") String sort,
            @RequestParam(value = "query") String query);

    @GetMapping("/v1/nid/me")
    NaverAuthResponse getAuthInfo(@RequestHeader("Authorization") String authorization);
}