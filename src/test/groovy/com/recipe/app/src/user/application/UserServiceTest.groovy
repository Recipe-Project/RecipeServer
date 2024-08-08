package com.recipe.app.src.user.application

import com.recipe.app.src.common.utils.JwtUtil
import com.recipe.app.src.etc.application.BadWordService
import com.recipe.app.src.user.application.dto.*
import com.recipe.app.src.user.domain.User
import com.recipe.app.src.user.exception.NotFoundUserException
import com.recipe.app.src.user.exception.UserTokenNotExistException
import com.recipe.app.src.user.infra.UserRepository
import jakarta.servlet.http.HttpServletRequest
import org.springframework.util.StringUtils
import spock.lang.Specification

class UserServiceTest extends Specification {

    private UserRepository userRepository = Mock()
    private JwtUtil jwtUtil = Mock()
    private BadWordService badWordService = Mock()
    private UserAuthClientService userAuthClientService = Mock()
    private UserService userService = new UserService(userRepository, jwtUtil, badWordService, userAuthClientService)

    def "유저 아이디로 유저 조회"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        userRepository.findById(user.userId) >> Optional.of(user)

        when:
        User result = userService.findByUserId(user.userId)

        then:
        result == user
    }

    def "유저 아이디로 유저 조회 시 해당하는 유저 없으면 예외 발생"() {

        given:
        Long userId = 1
        userRepository.findById(userId) >> Optional.empty()

        when:
        userService.findByUserId(userId)

        then:
        def e = thrown(NotFoundUserException.class)
        e.message == "존재하지 않는 회원입니다."
    }

    def "자동 로그인"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        when:
        UserLoginResponse result = userService.autoLogin(user)

        then:
        1 * userRepository.save(_)
        result.userId == user.userId
    }

    def "네이버 로그인"() {

        given:
        UserLoginRequest request = UserLoginRequest.builder()
                .accessToken("access_token")
                .fcmToken("fcm_token")
                .build()

        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        userAuthClientService.getUserByNaverAuthInfo(request) >> user

        userRepository.findBySocialId(user.socialId) >> Optional.of(user)

        String accessToken = "access_token"
        String refreshToken = "refresh_token"

        jwtUtil.createAccessToken(user.userId) >> accessToken
        jwtUtil.createRefreshToken(user.userId) >> refreshToken


        when:
        UserSocialLoginResponse result = userService.naverLogin(request)

        then:
        0 * userRepository.save(_)
        result.userId == user.userId
        result.accessToken == accessToken
        result.refreshToken == refreshToken
    }

    def "네이버 로그인 시 유효하지 않은 요청값인 경우 예외 발생"() {

        given:
        UserLoginRequest request = UserLoginRequest.builder()
                .accessToken(accessToken)
                .fcmToken("fcm_token")
                .build()

        when:
        userService.naverLogin(request)

        then:
        def e = thrown(IllegalArgumentException.class)
        e.message == expected

        where:
        accessToken || expected
        null        || "액세스 토큰을 입력해주세요."
        ""          || "액세스 토큰을 입력해주세요."
    }

    def "카카오 로그인"() {

        given:
        UserLoginRequest request = UserLoginRequest.builder()
                .accessToken("access_token")
                .fcmToken("fcm_token")
                .build()

        User user = User.builder()
                .userId(1)
                .socialId("kakao_1")
                .nickname("테스터1")
                .build()

        userAuthClientService.getUserByKakaoAuthInfo(request) >> user

        userRepository.findBySocialId(user.socialId) >> Optional.of(user)

        String accessToken = "access_token"
        String refreshToken = "refresh_token"

        jwtUtil.createAccessToken(user.userId) >> accessToken
        jwtUtil.createRefreshToken(user.userId) >> refreshToken


        when:
        UserSocialLoginResponse result = userService.kakaoLogin(request)

        then:
        0 * userRepository.save(_)
        result.userId == user.userId
        result.accessToken == accessToken
        result.refreshToken == refreshToken
    }

    def "카카오 로그인 시 유효하지 않은 요청값인 경우 예외 발생"() {

        given:
        UserLoginRequest request = UserLoginRequest.builder()
                .accessToken(accessToken)
                .fcmToken("fcm_token")
                .build()

        when:
        userService.kakaoLogin(request)

        then:
        def e = thrown(IllegalArgumentException.class)
        e.message == expected

        where:
        accessToken || expected
        null        || "액세스 토큰을 입력해주세요."
        ""          || "액세스 토큰을 입력해주세요."
    }

    def "구글 로그인"() {

        given:
        UserLoginRequest request = UserLoginRequest.builder()
                .accessToken("access_token")
                .fcmToken("fcm_token")
                .build()

        User user = User.builder()
                .userId(1)
                .socialId("kakao_1")
                .nickname("테스터1")
                .build()

        userAuthClientService.getUserByGoogleAuthInfo(request) >> user

        userRepository.findBySocialId(user.socialId) >> Optional.of(user)

        String accessToken = "access_token"
        String refreshToken = "refresh_token"

        jwtUtil.createAccessToken(user.userId) >> accessToken
        jwtUtil.createRefreshToken(user.userId) >> refreshToken


        when:
        UserSocialLoginResponse result = userService.googleLogin(request)

        then:
        0 * userRepository.save(_)
        result.userId == user.userId
        result.accessToken == accessToken
        result.refreshToken == refreshToken
    }

    def "구글 로그인 시 유효하지 않은 요청값인 경우 예외 발생"() {

        given:
        UserLoginRequest request = UserLoginRequest.builder()
                .accessToken(accessToken)
                .fcmToken("fcm_token")
                .build()

        when:
        userService.googleLogin(request)

        then:
        def e = thrown(IllegalArgumentException.class)
        e.message == expected

        where:
        accessToken || expected
        null        || "액세스 토큰을 입력해주세요."
        ""          || "액세스 토큰을 입력해주세요."
    }

    def "유저 정보 수정"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("kakao_1")
                .nickname("테스터1")
                .build()

        UserProfileRequest request = UserProfileRequest.builder()
                .profileImgUrl(profileImgUrl)
                .nickname(nickname)
                .build()

        when:
        userService.update(user, request)

        then:
        1 * userRepository.save(_)
        StringUtils.hasText(profileImgUrl) ? user.profileImgUrl == profileImgUrl : user.profileImgUrl != profileImgUrl
        StringUtils.hasText(nickname) ? user.nickname == nickname : user.nickname != nickname

        where:
        profileImgUrl    | nickname
        null             | "update_nickname"
        ""               | "update_nickname"
        "update_profile" | null
        "update_profile" | ""
        "update_profile" | "update_nickname"
    }

    def "유저 탈퇴"() {

        given:
        HttpServletRequest request = Mock()

        User user = User.builder()
                .userId(1)
                .socialId("kakao_1")
                .nickname("테스터1")
                .build()

        when:
        userService.withdraw(user, request)

        then:
        1 * userRepository.delete(user)
        1 * jwtUtil.resolveAccessToken(request)
        1 * jwtUtil.setAccessTokenBlacklist(_)
        1 * jwtUtil.getUserId(_)
        1 * jwtUtil.removeRefreshToken(_)
    }

    def "디바이스 토큰 수정"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("kakao_1")
                .nickname("테스터1")
                .build()

        UserDeviceTokenRequest request = UserDeviceTokenRequest.builder()
                .fcmToken("fcm_token")
                .build()

        when:
        userService.updateFcmToken(user, request)

        then:
        1 * userRepository.save(user)
        user.deviceToken == request.fcmToken
    }

    def "유저 아이디 목록 내 유저 목록 조회"() {

        given:
        List<Long> userIds = [1, 2]

        List<User> users = [
                User.builder()
                        .userId(1)
                        .socialId("naver_1")
                        .nickname("테스터1")
                        .build(),
                User.builder()
                        .userId(2)
                        .socialId("kakao_1")
                        .nickname("테스터2")
                        .build()
        ]

        userRepository.findAllById(userIds) >> users

        when:
        List<User> result = userService.findByUserIds(userIds)

        then:
        result == users
    }

    def "로그아웃"() {

        given:
        HttpServletRequest request = Mock()

        when:
        userService.logout(request)

        then:
        1 * jwtUtil.resolveAccessToken(request)
        1 * jwtUtil.setAccessTokenBlacklist(_)
        1 * jwtUtil.getUserId(_)
        1 * jwtUtil.removeRefreshToken(_)
    }

    def "토큰 재발급"() {

        given:
        UserTokenRefreshRequest request = UserTokenRefreshRequest.builder()
                .userId(1)
                .refreshToken("refresh_token")
                .build()

        jwtUtil.isValidRefreshToken(request.refreshToken) >> true

        String accessToken = "new_access_token"
        String refreshToken = "new_refresh_token"
        jwtUtil.createAccessToken(request.userId) >> accessToken
        jwtUtil.createRefreshToken(request.userId) >> refreshToken

        when:
        UserTokenRefreshResponse result = userService.reissueToken(request)

        then:
        result.userId == request.userId
        result.accessToken == accessToken
        result.refreshToken == refreshToken
    }

    def "토큰 재발급 시 리프레시 토큰이 유효하지 않으면 예외 발생"() {

        given:
        UserTokenRefreshRequest request = UserTokenRefreshRequest.builder()
                .userId(1)
                .refreshToken("refresh_token")
                .build()

        jwtUtil.isValidRefreshToken(request.refreshToken) >> false

        when:
        userService.reissueToken(request)

        then:
        def e = thrown(UserTokenNotExistException.class)
        e.message == "유효하지 않은 JWT입니다."
    }
}
