package com.recipe.app.src.user.application;

import com.google.common.base.Preconditions;
import com.recipe.app.common.utils.HttpUtil;
import com.recipe.app.src.user.application.dto.UserDto;
import com.recipe.app.src.user.domain.JwtBlacklist;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.user.exception.ForbiddenAccessException;
import com.recipe.app.src.user.infra.JwtBlacklistRepository;
import com.recipe.app.src.user.infra.UserRepository;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class UserService {

    @Value("${kakao.client-id}")
    private String kakaoClientId;
    @Value("${kakao.redirect-uri}")
    private String kakaoRedirectURI;
    @Value("${naver.client-id}")
    private String naverClientId;
    @Value("${naver.client-secret}")
    private String naverClientSecret;
    @Value("${kakao.redirect-uri}")
    private String naverRedirectURI;
    @Value("${google.client-id}")
    private String googleClientId;
    @Value("${google.client-secret}")
    private String googleClientSecret;
    @Value("${google.redirect-uri}")
    private String googleRedirectURI;

    private final UserRepository userRepository;
    private final JwtBlacklistRepository jwtBlacklistRepository;

    public UserService(UserRepository userRepository, JwtBlacklistRepository jwtBlacklistRepository) {
        this.userRepository = userRepository;
        this.jwtBlacklistRepository = jwtBlacklistRepository;
    }

    @Transactional
    public User autoLogin(User user) {
        user.changeRecentLoginAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public String getNaverAccessToken(String code, String state) throws IOException, ParseException {

        String apiURL = "https://nid.naver.com/oauth2.0/token" +
                "?grant_type=authorization_code" +
                "&client_id=" + naverClientId +
                "&client_secret=" + naverClientSecret +
                "&redirect_uri=" + naverRedirectURI +
                "&code=" + code +
                "&state=" + state;
        Map<String, String> requestHeaders = new HashMap<>();

        JSONObject response = HttpUtil.getHTTP(apiURL, requestHeaders);

        return response.get("access_token").toString();
    }

    @Transactional
    public User naverLogin(String accessToken, String fcmToken) throws IOException, ParseException {

        Preconditions.checkArgument(StringUtils.hasText(accessToken), "액세스 토큰을 입력해주세요.");

        String apiURL = "https://openapi.naver.com/v1/nid/me";
        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("Authorization", "Bearer " + accessToken);

        JSONObject jsonObject = HttpUtil.getHTTP(apiURL, requestHeaders);

        String resultCode = jsonObject.get("resultcode").toString();

        if (!resultCode.equals("00"))
            throw new ForbiddenAccessException();

        JSONObject response = (JSONObject) new JSONParser().parse(jsonObject.get("response").toString());

        User user = User.builder()
                .socialId("naver_" + response.get("id").toString())
                .nickname(response.get("name").toString())
                .email(response.get("email") != null ? response.get("email").toString() : null)
                .phoneNumber(response.get("mobile") != null ? response.get("mobile").toString() : null)
                .deviceToken(fcmToken)
                .build();

        return userRepository.findBySocialId(user.getSocialId())
                .orElseGet(() -> userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public String getKakaoAccessToken(String code) throws IOException, ParseException {

        String apiURL = "https://kauth.kakao.com/oauth/token";
        String request = "grant_type=authorization_code" +
                "&client_id=" + kakaoClientId +
                "&redirect_uri=" + kakaoRedirectURI +
                "&code=" + code;

        JSONObject response = HttpUtil.postHTTP(apiURL, request);

        return response.get("access_token").toString();
    }

    @Transactional
    public User kakaoLogin(String accessToken, String fcmToken) throws IOException, ParseException {

        Preconditions.checkArgument(StringUtils.hasText(accessToken), "액세스 토큰을 입력해주세요.");

        String apiURL = "https://kapi.kakao.com/v2/user/me";
        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("Authorization", "Bearer " + accessToken);

        JSONObject response = HttpUtil.getHTTP(apiURL, requestHeaders);

        JSONParser jsonParser = new JSONParser();
        JSONObject kakaoAccount = (JSONObject) jsonParser.parse(response.get("kakao_account").toString());
        JSONObject profile = (JSONObject) jsonParser.parse(kakaoAccount.get("profile").toString());

        User user = User.builder()
                .socialId("kakao_" + response.get("id").toString())
                .nickname(profile.get("nickname").toString())
                .email(kakaoAccount.get("email") != null ? kakaoAccount.get("email").toString() : null)
                .deviceToken(fcmToken)
                .build();

        return userRepository.findBySocialId(user.getSocialId())
                .orElseGet(() -> userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public String getGoogleIdToken(String code) throws IOException, ParseException {

        String apiURL = "https://oauth2.googleapis.com/token";
        String request = "grant_type=authorization_code" +
                "&client_id=" + googleClientId +
                "&client_secret=" + googleClientSecret +
                "&redirect_uri=" + googleRedirectURI +
                "&code=" + code;

        JSONObject response = HttpUtil.postHTTP(apiURL, request);

        return response.get("id_token").toString();
    }

    @Transactional
    public User googleLogin(String idToken, String fcmToken) throws IOException, ParseException {

        Preconditions.checkArgument(StringUtils.hasText(idToken), "ID 토큰을 입력해주세요.");

        String apiURL = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
        Map<String, String> requestHeaders = new HashMap<>();

        JSONObject response = HttpUtil.getHTTP(apiURL, requestHeaders);

        User user = User.builder()
                .socialId("google_" + response.get("sub").toString())
                .nickname(response.get("name").toString())
                .email(response.get("email").toString())
                .deviceToken(fcmToken)
                .build();

        return userRepository.findBySocialId(user.getSocialId()).orElseGet(() ->
                userRepository.save(user));
    }

    @Transactional
    public User updateUser(User user, UserDto.UserProfileRequest request) {
        user.changeProfile(request.getProfileImgUrl(), request.getNickname());
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(User user, String jwt) {
        userRepository.delete(user);
        registerJwtBlackList(jwt);
    }

    @Transactional
    public void registerJwtBlackList(String jwt) {
        jwtBlacklistRepository.save(new JwtBlacklist(jwt));
    }

    @Transactional
    public void updateFcmToken(UserDto.UserDeviceTokenRequest request, User user) {
        user.changeDeviceToken(request.getFcmToken());
        userRepository.save(user);
    }
}
