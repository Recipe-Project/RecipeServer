package com.recipe.app.src.user.application;

import com.google.common.base.Preconditions;
import com.recipe.app.src.common.utils.JwtUtil;
import com.recipe.app.src.etc.application.BadWordService;
import com.recipe.app.src.recipe.domain.Recipe;
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
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserService {

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

        User loginUser = userAuthClientService.getUserByNaverAuthInfo(request);

        User user = userRepository.findBySocialId(loginUser.getSocialId())
                .orElseGet(() -> userRepository.save(loginUser));

        user.changeRecentLoginAt(LocalDateTime.now());

        String accessToken = jwtUtil.createAccessToken(user.getUserId());
        String refreshToken = jwtUtil.createRefreshToken(user.getUserId());

        return UserSocialLoginResponse.from(user, accessToken, refreshToken);
    }

    @Transactional
    public UserSocialLoginResponse kakaoLogin(UserLoginRequest request) {

        Preconditions.checkArgument(StringUtils.hasText(request.getAccessToken()), "액세스 토큰을 입력해주세요.");

        User loginUser = userAuthClientService.getUserByKakaoAuthInfo(request);

        User user = userRepository.findBySocialId(loginUser.getSocialId())
                .orElseGet(() -> userRepository.save(loginUser));

        user.changeRecentLoginAt(LocalDateTime.now());

        String accessToken = jwtUtil.createAccessToken(user.getUserId());
        String refreshToken = jwtUtil.createRefreshToken(user.getUserId());

        return UserSocialLoginResponse.from(user, accessToken, refreshToken);
    }

    @Transactional
    public UserSocialLoginResponse googleLogin(UserLoginRequest request) {

        Preconditions.checkArgument(StringUtils.hasText(request.getAccessToken()), "액세스 토큰을 입력해주세요.");

        User loginUser = userAuthClientService.getUserByGoogleAuthInfo(request);

        User user = userRepository.findBySocialId(loginUser.getSocialId())
                .orElseGet(() -> userRepository.save(loginUser));

        user.changeRecentLoginAt(LocalDateTime.now());

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
    public List<User> findRecipePostUsers(Collection<Recipe> recipes) {

        List<Long> recipePostUserIds = recipes.stream()
                .map(Recipe::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return userRepository.findAllById(recipePostUserIds);
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
