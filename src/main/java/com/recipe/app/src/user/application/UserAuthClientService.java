package com.recipe.app.src.user.application;

import com.recipe.app.src.common.client.NaverFeignClient;
import com.recipe.app.src.common.client.NaverOAuthFeignClient;
import com.recipe.app.src.common.client.dto.NaverAuthResponse;
import com.recipe.app.src.user.application.dto.UserLoginRequest;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.user.exception.ForbiddenAccessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserAuthClientService {

    @Value("${naver.client-id}")
    private String naverClientId;
    @Value("${naver.client-secret}")
    private String naverClientSecret;
    @Value("${kakao.redirect-uri}")
    private String naverRedirectURI;

    private final NaverFeignClient naverFeignClient;
    private final NaverOAuthFeignClient naverOAuthFeignClient;

    public UserAuthClientService(NaverFeignClient naverFeignClient, NaverOAuthFeignClient naverOAuthFeignClient) {
        this.naverFeignClient = naverFeignClient;
        this.naverOAuthFeignClient = naverOAuthFeignClient;
    }

    public UserLoginRequest getNaverLoginRequest(String code, String state) {

        return naverOAuthFeignClient.getAccessToken("authorization_code",
                naverClientId,
                naverClientSecret,
                naverRedirectURI,
                code,
                state
        ).toLoginRequest();
    }

    public User getUserByNaverAuthInfo(UserLoginRequest request) {

        NaverAuthResponse response = naverFeignClient.getAuthInfo("Bearer " + request.getAccessToken());

        if (!response.getResultcode().equals("00"))
            throw new ForbiddenAccessException();

        return response.toEntity(request.getFcmToken());
    }
}
