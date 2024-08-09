package com.recipe.app.src.user.application;

import com.google.common.base.Preconditions;
import com.recipe.app.src.common.utils.JwtUtil;
import com.recipe.app.src.common.utils.BadWordFiltering;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BadWordFiltering badWordFiltering;
    private final UserAuthClientService userAuthClientService;

    public UserService(UserRepository userRepository, JwtUtil jwtUtil, BadWordFiltering badWordFiltering, UserAuthClientService userAuthClientService) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.badWordFiltering = badWordFiltering;
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

        User user = create(userAuthClientService.getUserByNaverAuthInfo(request));

        user.changeRecentLoginAt(LocalDateTime.now());

        String accessToken = jwtUtil.createAccessToken(user.getUserId());
        String refreshToken = jwtUtil.createRefreshToken(user.getUserId());

        return UserSocialLoginResponse.from(user, accessToken, refreshToken);
    }

    @Transactional
    public UserSocialLoginResponse kakaoLogin(UserLoginRequest request) {

        Preconditions.checkArgument(StringUtils.hasText(request.getAccessToken()), "액세스 토큰을 입력해주세요.");

        User user = create(userAuthClientService.getUserByKakaoAuthInfo(request));

        user.changeRecentLoginAt(LocalDateTime.now());

        String accessToken = jwtUtil.createAccessToken(user.getUserId());
        String refreshToken = jwtUtil.createRefreshToken(user.getUserId());

        return UserSocialLoginResponse.from(user, accessToken, refreshToken);
    }

    @Transactional
    public UserSocialLoginResponse googleLogin(UserLoginRequest request) {

        Preconditions.checkArgument(StringUtils.hasText(request.getAccessToken()), "액세스 토큰을 입력해주세요.");

        User user = create(userAuthClientService.getUserByGoogleAuthInfo(request));

        user.changeRecentLoginAt(LocalDateTime.now());

        String accessToken = jwtUtil.createAccessToken(user.getUserId());
        String refreshToken = jwtUtil.createRefreshToken(user.getUserId());

        return UserSocialLoginResponse.from(user, accessToken, refreshToken);
    }

    private User create(User user) {

        return userRepository.findBySocialId(user.getSocialId())
                .orElseGet(() -> userRepository.save(user));
    }

    @Transactional
    public void update(User user, UserProfileRequest request) {

        badWordFiltering.checkBadWords(request.getNickname());
        user.changeProfile(request.getProfileImgUrl(), request.getNickname());
        userRepository.save(user);
    }

    @Transactional
    public void withdraw(User user, HttpServletRequest request) {

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
