package com.recipe.app.src.common.client.kakao;

import com.recipe.app.src.common.client.kakao.dto.KakaoAuthResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "kakao-client", url = "https://kapi.kakao.com")
public interface KakaoFeignClient {

    @GetMapping("/v2/user/me")
    KakaoAuthResponse getAuthInfo(@RequestHeader("Authorization") String authorization);
}
