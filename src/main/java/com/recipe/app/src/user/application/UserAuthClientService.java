package com.recipe.app.src.user.application;

import com.recipe.app.src.common.client.apple.AppleOAuthFeignClient;
import com.recipe.app.src.common.client.apple.dto.AppleAuthResponse;
import com.recipe.app.src.common.client.apple.dto.ApplePublicKeyResponse;
import com.recipe.app.src.common.client.apple.dto.ApplePublicKeysResponse;
import com.recipe.app.src.common.client.google.GoogleOAuthFeignClient;
import com.recipe.app.src.common.client.kakao.KakaoFeignClient;
import com.recipe.app.src.common.client.kakao.KakaoOAuthFeignClient;
import com.recipe.app.src.common.client.naver.NaverFeignClient;
import com.recipe.app.src.common.client.naver.NaverOAuthFeignClient;
import com.recipe.app.src.common.client.naver.dto.NaverAuthResponse;
import com.recipe.app.src.common.utils.JwtUtil;
import com.recipe.app.src.user.application.dto.UserLoginRequest;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.user.exception.ForbiddenAccessException;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;

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

    private final Logger logger = LoggerFactory.getLogger(UserAuthClientService.class);
    private final NaverFeignClient naverFeignClient;
    private final NaverOAuthFeignClient naverOAuthFeignClient;
    private final KakaoFeignClient kakaoFeignClient;
    private final KakaoOAuthFeignClient kakaoOAuthFeignClient;
    private final GoogleOAuthFeignClient googleOAuthFeignClient;
    private final AppleOAuthFeignClient appleOAuthFeignClient;
    private final JwtUtil jwtUtil;

    public UserAuthClientService(NaverFeignClient naverFeignClient, NaverOAuthFeignClient naverOAuthFeignClient,
                                 KakaoFeignClient kakaoFeignClient, KakaoOAuthFeignClient kakaoOAuthFeignClient,
                                 GoogleOAuthFeignClient googleOAuthFeignClient, AppleOAuthFeignClient appleOAuthFeignClient,
                                 JwtUtil jwtUtil) {
        this.naverFeignClient = naverFeignClient;
        this.naverOAuthFeignClient = naverOAuthFeignClient;
        this.kakaoFeignClient = kakaoFeignClient;
        this.kakaoOAuthFeignClient = kakaoOAuthFeignClient;
        this.googleOAuthFeignClient = googleOAuthFeignClient;
        this.appleOAuthFeignClient = appleOAuthFeignClient;
        this.jwtUtil = jwtUtil;
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

    public User getUserByAppleAuthInfo(UserLoginRequest request) {

        String idToken = request.getAccessToken();

        // 1. Apple Public Keys 조회
        ApplePublicKeysResponse publicKeys = appleOAuthFeignClient.getPublicKeys();

        // 2. id_token 헤더에서 kid, alg 추출
        String kid = getKidFromIdToken(idToken);
        String alg = getAlgFromIdToken(idToken);

        // 3. 매칭되는 Public Key 찾기
        ApplePublicKeyResponse matchedKey = publicKeys.getMatchKey(alg, kid);

        // 4. JWT 검증 및 Claims 추출
        Claims claims = jwtUtil.parseAppleIdToken(idToken, matchedKey);

        // 5. User 엔티티 생성
        return AppleAuthResponse.builder()
                .sub(claims.get("sub", String.class))
                .email(claims.get("email", String.class))
                .name(null)
                .build()
                .toEntity(request.getFcmToken());
    }

    public String getKidFromIdToken(String idToken) {

        try {
            String header = idToken.split("\\.")[0];
            String decodedHeader = new String(Base64.getUrlDecoder().decode(header));

            String kid = decodedHeader.split("\"kid\":\"")[1].split("\"")[0];
            return kid;
        } catch (Exception e) {
            logger.error("id_token 헤더에서 kid 추출 실패", e);
            throw new IllegalArgumentException("id_token 헤더에서 kid를 추출할 수 없습니다.", e);
        }
    }

    private String getAlgFromIdToken(String idToken) {

        try {
            String header = idToken.split("\\.")[0];
            String decodedHeader = new String(Base64.getUrlDecoder().decode(header));

            String alg = decodedHeader.split("\"alg\":\"")[1].split("\"")[0];
            return alg;
        } catch (Exception e) {
            logger.error("id_token 헤더에서 alg 추출 실패", e);
            throw new IllegalArgumentException("id_token 헤더에서 alg를 추출할 수 없습니다.", e);
        }
    }

}
