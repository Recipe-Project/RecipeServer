package com.recipe.app.src.user.application;

import com.google.common.base.Preconditions;
import com.recipe.app.src.common.utils.HttpUtil;
import com.recipe.app.src.common.utils.JwtUtil;
import com.recipe.app.src.etc.application.BadWordService;
import com.recipe.app.src.user.application.dto.UserDeviceTokenRequest;
import com.recipe.app.src.user.application.dto.UserLoginRequest;
import com.recipe.app.src.user.application.dto.UserLoginResponse;
import com.recipe.app.src.user.application.dto.UserProfileRequest;
import com.recipe.app.src.user.application.dto.UserSocialLoginResponse;
import com.recipe.app.src.user.application.dto.UserTokenRefreshRequest;
import com.recipe.app.src.user.application.dto.UserTokenRefreshResponse;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.user.exception.NotFoundUserException;
import com.recipe.app.src.user.exception.UserTokenNotExistException;
import com.recipe.app.src.user.infra.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    @Value("${google.client-id}")
    private String googleClientId;
    @Value("${google.client-secret}")
    private String googleClientSecret;
    @Value("${google.redirect-uri}")
    private String googleRedirectURI;

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BadWordService badWordService;
    private final UserAuthClientService userAuthClientService;

    public UserService(UserRepository userRepository, JwtUtil jwtUtil, BadWordService badWordService, UserAuthClientService userAuthClientService) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.badWordService = badWordService;
        this.userAuthClientService = userAuthClientService;
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

    @Transactional
    public UserSocialLoginResponse naverLogin(UserLoginRequest request) {

        Preconditions.checkArgument(StringUtils.hasText(request.getAccessToken()), "액세스 토큰을 입력해주세요.");

        User user = userAuthClientService.getUserByNaverAuthInfo(request);

        userRepository.findBySocialId(user.getSocialId())
                .orElseGet(() -> {
                    user.changeRecentLoginAt(LocalDateTime.now());
                    return userRepository.save(user);
                });

        String accessToken = jwtUtil.createAccessToken(user.getUserId());
        String refreshToken = jwtUtil.createRefreshToken(user.getUserId());

        return UserSocialLoginResponse.from(user, accessToken, refreshToken);
    }

    @Transactional
    public UserSocialLoginResponse kakaoLogin(UserLoginRequest request) {

        Preconditions.checkArgument(StringUtils.hasText(request.getAccessToken()), "액세스 토큰을 입력해주세요.");

        User user = userAuthClientService.getUserByKakaoAuthInfo(request);

        userRepository.findBySocialId(user.getSocialId())
                .orElseGet(() -> {
                    user.changeRecentLoginAt(LocalDateTime.now());
                    return userRepository.save(user);
                });

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

        logout(request);
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

        String accessToken = jwtUtil.resolveAccessToken(request);
        jwtUtil.setAccessTokenBlacklist(accessToken);
        jwtUtil.removeRefreshToken(jwtUtil.getUserId(accessToken));
    }

    @Transactional(readOnly = true)
    public UserTokenRefreshResponse reissueToken(UserTokenRefreshRequest request) {

        if (!jwtUtil.isValidRefreshToken(request.getRefreshToken())) {
            throw new UserTokenNotExistException();
        }

        return UserTokenRefreshResponse.builder()
                .userId(request.getUserId())
                .accessToken(jwtUtil.createAccessToken(request.getUserId()))
                .refreshToken(jwtUtil.createRefreshToken(request.getUserId()))
                .build();
    }
}
