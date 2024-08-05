package com.recipe.app.src.common.client.kakao;

import com.recipe.app.src.common.client.kakao.dto.KakaoAccessTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "kakao-oauth-client", url = "https://kauth.kakao.com/oauth")
public interface KakaoOAuthFeignClient {

    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    KakaoAccessTokenResponse getAccessToken(@RequestParam(name = "grant_type") String grantType,
                                            @RequestParam(name = "client_id") String clientId,
                                            @RequestParam(name = "redirect_uri") String redirectUri,
                                            @RequestParam(name = "code") String code);
}
