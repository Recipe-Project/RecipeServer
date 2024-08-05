package com.recipe.app.src.common.client.google;

import com.recipe.app.src.common.client.google.dto.GoogleAccessTokenResponse;
import com.recipe.app.src.common.client.google.dto.GoogleAuthResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "google-client", url = "https://oauth2.googleapis.com")
public interface GoogleOAuthFeignClient {

    @PostMapping("/token")
    GoogleAccessTokenResponse getAccessToken(@RequestParam(name = "grant_type") String grantType,
                                             @RequestParam(name = "client_id") String clientId,
                                             @RequestParam(name = "client_secret") String clientSecret,
                                             @RequestParam(name = "redirect_uri") String redirectUri,
                                             @RequestParam(name = "code") String code);

    @GetMapping("/tokeninfo")
    GoogleAuthResponse getAuthInfo(@RequestParam(name = "id_token") String accessToken);
}
