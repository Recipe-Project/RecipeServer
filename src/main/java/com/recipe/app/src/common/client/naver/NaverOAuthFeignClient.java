package com.recipe.app.src.common.client.naver;

import com.recipe.app.src.common.client.naver.dto.NaverAccessTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "naver-oauth-client", url = "https://nid.naver.com/oauth2.0")
public interface NaverOAuthFeignClient {

    @GetMapping("/token")
    NaverAccessTokenResponse getAccessToken(
            @RequestParam(value = "grant_type") String grantType,
            @RequestParam(value = "client_id") String clientId,
            @RequestParam(value = "client_secret") String clientSecret,
            @RequestParam(value = "redirect_uri") String redirectURI,
            @RequestParam(value = "code") String code,
            @RequestParam(value = "state") String state
    );
}
