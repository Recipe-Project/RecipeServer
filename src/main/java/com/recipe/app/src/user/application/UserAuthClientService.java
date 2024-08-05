package com.recipe.app.src.user.application;

import com.recipe.app.src.common.client.google.GoogleOAuthFeignClient;
import com.recipe.app.src.common.client.kakao.KakaoFeignClient;
import com.recipe.app.src.common.client.kakao.KakaoOAuthFeignClient;
import com.recipe.app.src.common.client.naver.NaverFeignClient;
import com.recipe.app.src.common.client.naver.NaverOAuthFeignClient;
import com.recipe.app.src.common.client.naver.dto.NaverAuthResponse;
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
    @Value("${kakao.client-id}")
    private String kakaoClientId;
    @Value("${kakao.redirect-uri}")
    private String kakaoRedirectURI;
    @Value("${google.client-id}")
    private String googleClientId;
    @Value("${google.client-secret}")
    private String googleClientSecret;
    @Value("${google.redirect-uri}")
    private String googleRedirectURI;

    private final NaverFeignClient naverFeignClient;
    private final NaverOAuthFeignClient naverOAuthFeignClient;
    private final KakaoFeignClient kakaoFeignClient;
    private final KakaoOAuthFeignClient kakaoOAuthFeignClient;
    private final GoogleOAuthFeignClient googleOAuthFeignClient;

    public UserAuthClientService(NaverFeignClient naverFeignClient, NaverOAuthFeignClient naverOAuthFeignClient,
                                 KakaoFeignClient kakaoFeignClient, KakaoOAuthFeignClient kakaoOAuthFeignClient,
                                 GoogleOAuthFeignClient googleOAuthFeignClient) {
        this.naverFeignClient = naverFeignClient;
        this.naverOAuthFeignClient = naverOAuthFeignClient;
        this.kakaoFeignClient = kakaoFeignClient;
        this.kakaoOAuthFeignClient = kakaoOAuthFeignClient;
        this.googleOAuthFeignClient = googleOAuthFeignClient;
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

    public UserLoginRequest getKakaoLoginRequest(String code) {

        return kakaoOAuthFeignClient.getAccessToken(
                "authorization_code",
                kakaoClientId,
                kakaoRedirectURI,
                code
        ).toLoginRequest();
    }

    public User getUserByKakaoAuthInfo(UserLoginRequest request) {

        return kakaoFeignClient.getAuthInfo("Bearer " + request.getAccessToken())
                .toEntity(request.getFcmToken());
    }

    public UserLoginRequest getGoogleLoginRequest(String code) {

        return googleOAuthFeignClient.getAccessToken(
                "authorization_code",
                googleClientId,
                googleClientSecret,
                googleRedirectURI,
                code
        ).toLoginRequest();
    }

    public User getUserByGoogleAuthInfo(UserLoginRequest request) {

        return googleOAuthFeignClient.getAuthInfo(request.getAccessToken())
                .toEntity(request.getFcmToken());
    }
}
