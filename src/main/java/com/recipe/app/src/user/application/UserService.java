package com.recipe.app.src.user.application;

import com.google.common.base.Preconditions;
import com.recipe.app.common.utils.HttpUtil;
import com.recipe.app.common.utils.JwtUtil;
import com.recipe.app.src.common.application.BadWordService;
import com.recipe.app.src.user.application.dto.UserDeviceTokenRequest;
import com.recipe.app.src.user.application.dto.UserLoginRequest;
import com.recipe.app.src.user.application.dto.UserLoginResponse;
import com.recipe.app.src.user.application.dto.UserProfileRequest;
import com.recipe.app.src.user.application.dto.UserSocialLoginResponse;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.user.exception.ForbiddenAccessException;
import com.recipe.app.src.user.exception.NotFoundUserException;
import com.recipe.app.src.user.infra.UserRepository;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
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
    private final JwtUtil jwtUtil;
    private final BadWordService badWordService;

    public UserService(UserRepository userRepository, JwtUtil jwtUtil, BadWordService badWordService) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.badWordService = badWordService;
    }

    @Transactional(readOnly = true)
    public User findByUserId(Long userId) {
        return userRepository.findById(userId).orElseThrow(NotFoundUserException::new);
    }

    @Transactional
    public UserLoginResponse autoLogin(User user) {

        user.changeRecentLoginAt(LocalDateTime.now());
        userRepository.save(user);

        return UserLoginResponse.from(user);
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
    public UserSocialLoginResponse naverLogin(UserLoginRequest request) throws IOException, ParseException {

        Preconditions.checkArgument(StringUtils.hasText(request.getAccessToken()), "액세스 토큰을 입력해주세요.");

        String apiURL = "https://openapi.naver.com/v1/nid/me";
        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("Authorization", "Bearer " + request.getAccessToken());

        JSONObject jsonObject = HttpUtil.getHTTP(apiURL, requestHeaders);

        String resultCode = jsonObject.get("resultcode").toString();

        if (!resultCode.equals("00"))
            throw new ForbiddenAccessException();

        JSONObject response = (JSONObject) new JSONParser().parse(jsonObject.get("response").toString());

        String socialId = "naver_" + response.get("id").toString();
        User user = userRepository.findBySocialId(socialId)
                .orElseGet(() -> userRepository.save(User.builder()
                        .socialId(socialId)
                        .nickname(response.get("name").toString())
                        .email(response.get("email") != null ? response.get("email").toString() : null)
                        .phoneNumber(response.get("mobile") != null ? response.get("mobile").toString() : null)
                        .deviceToken(request.getFcmToken())
                        .build()));

        user.changeRecentLoginAt(LocalDateTime.now());
        userRepository.save(user);

        String accessToken = jwtUtil.createAccessToken(user.getUserId());
        String refreshToken = jwtUtil.createRefreshToken(user.getUserId());

        return UserSocialLoginResponse.from(user, accessToken, refreshToken);
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
    public UserSocialLoginResponse kakaoLogin(UserLoginRequest request) throws IOException, ParseException {

        Preconditions.checkArgument(StringUtils.hasText(request.getAccessToken()), "액세스 토큰을 입력해주세요.");

        String apiURL = "https://kapi.kakao.com/v2/user/me";
        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("Authorization", "Bearer " + request.getAccessToken());

        JSONObject response = HttpUtil.getHTTP(apiURL, requestHeaders);

        JSONParser jsonParser = new JSONParser();
        JSONObject kakaoAccount = (JSONObject) jsonParser.parse(response.get("kakao_account").toString());
        JSONObject profile = (JSONObject) jsonParser.parse(kakaoAccount.get("profile").toString());

        String socialId = "kakao_" + response.get("id").toString();
        User user = userRepository.findBySocialId(socialId)
                .orElseGet(() -> userRepository.save(User.builder()
                        .socialId(socialId)
                        .nickname(profile.get("nickname").toString())
                        .email(kakaoAccount.get("email") != null ? kakaoAccount.get("email").toString() : null)
                        .deviceToken(request.getFcmToken())
                        .build()));

        user.changeRecentLoginAt(LocalDateTime.now());
        userRepository.save(user);

        String accessToken = jwtUtil.createAccessToken(user.getUserId());
        String refreshToken = jwtUtil.createRefreshToken(user.getUserId());

        return UserSocialLoginResponse.from(user, accessToken, refreshToken);
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
    public UserSocialLoginResponse googleLogin(UserLoginRequest request) throws IOException, ParseException {

        Preconditions.checkArgument(StringUtils.hasText(request.getAccessToken()), "액세스 토큰을 입력해주세요.");

        String apiURL = "https://oauth2.googleapis.com/tokeninfo?id_token=" + request.getAccessToken();
        Map<String, String> requestHeaders = new HashMap<>();

        JSONObject response = HttpUtil.getHTTP(apiURL, requestHeaders);

        String socialId = "google_" + response.get("sub").toString();
        User user = userRepository.findBySocialId(socialId).orElseGet(() ->
                userRepository.save(User.builder()
                        .socialId(socialId)
                        .nickname(response.get("name").toString())
                        .email(response.get("email").toString())
                        .deviceToken(request.getFcmToken())
                        .build()));

        user.changeRecentLoginAt(LocalDateTime.now());
        userRepository.save(user);

        String accessToken = jwtUtil.createAccessToken(user.getUserId());
        String refreshToken = jwtUtil.createRefreshToken(user.getUserId());

        return UserSocialLoginResponse.from(user, accessToken, refreshToken);
    }

    @Transactional
    public void updateUser(User user, UserProfileRequest request) {

        badWordService.checkBadWords(request.getNickname());
        user.changeProfile(request.getProfileImgUrl(), request.getNickname());
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(User user, HttpServletRequest request) {

        userRepository.delete(user);

        jwtUtil.createJwtBlacklist(request);
    }

    @Transactional
    public void updateFcmToken(User user, UserDeviceTokenRequest request) {

        user.changeDeviceToken(request.getFcmToken());
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<User> findByUserIds(Collection<Long> userIds) {

        return userRepository.findAllById(userIds);
    }

    @Transactional
    public void logout(HttpServletRequest request) {
        jwtUtil.createJwtBlacklist(request);
    }
}
