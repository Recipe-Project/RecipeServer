package com.recipe.app.src.user.domain

import org.springframework.util.StringUtils
import spock.lang.Specification

import java.time.LocalDateTime

class UserTest extends Specification {

    def "유저 생성"() {

        given:
        Long userId = 1
        String socialId = "naver_1"
        String nickname = "테스트"
        String email = "test@naver.com"
        String phoneNumber = "000-0000-0000"
        String deviceToken = "device_token"
        LocalDateTime recentLoginAt = LocalDateTime.now()

        when:
        User user = User.builder()
                .userId(userId)
                .socialId(socialId)
                .nickname(nickname)
                .email(email)
                .phoneNumber(phoneNumber)
                .deviceToken(deviceToken)
                .recentLoginAt(recentLoginAt)
                .build()

        then:
        user.userId == userId
        user.socialId == socialId
        user.nickname == nickname
        user.email == email
        user.phoneNumber == phoneNumber
        user.deviceToken == deviceToken
        user.recentLoginAt == recentLoginAt
    }

    def "유저 생성 시 유효하지 않은 요청값인 경우 예외 발생"() {

        when:
        User.builder()
                .userId(1)
                .socialId(socialId)
                .nickname(nickname)
                .email("test@naver.com")
                .phoneNumber("000-0000-0000")
                .deviceToken("device_token")
                .recentLoginAt(LocalDateTime.now())
                .build()

        then:
        def e = thrown(IllegalArgumentException.class)

        where:
        socialId  | nickname || expected
        null      | "테스트"    || "소셜 로그인 ID 값을 입력해주세요."
        ""        | "테스트"    || "소셜 로그인 ID 값을 입력해주세요."
        "naver_1" | null     || "닉네임을 입력해주세요."
        "naver_1" | ""       || "닉네임을 입력해주세요."
    }

    def "유저 프로필 변경 시 프로필 이미지 빈 값이 오는 경우 이전 데이터 유지"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스트")
                .email("test@naver.com")
                .phoneNumber("000-0000-0000")
                .deviceToken("device_token")
                .recentLoginAt(LocalDateTime.now())
                .build()

        when:
        user.changeProfile(profileImgUrl, nickname)

        then:
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

    def "유저 최근 로그인 시간 변경"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스트")
                .email("test@naver.com")
                .phoneNumber("000-0000-0000")
                .deviceToken("device_token")
                .recentLoginAt(LocalDateTime.MIN)
                .build()

        LocalDateTime updateLoginAt = LocalDateTime.now()

        when:
        user.changeRecentLoginAt(updateLoginAt)

        then:
        user.recentLoginAt == updateLoginAt
    }

    def "유저 최근 로그인 시간 변경 시 요청값이 Null 인 경우 예외 발생"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스트")
                .email("test@naver.com")
                .phoneNumber("000-0000-0000")
                .deviceToken("device_token")
                .recentLoginAt(LocalDateTime.MIN)
                .build()

        when:
        user.changeRecentLoginAt(null)

        then:
        def e = thrown(NullPointerException.class)
        e.message == "최근 로그인 시간을 입력해주세요."
    }

    def "유저 디바이스 토큰 변경"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스트")
                .email("test@naver.com")
                .phoneNumber("000-0000-0000")
                .deviceToken("device_token")
                .recentLoginAt(LocalDateTime.MIN)
                .build()

        String updateDeviceToken = "update_token"

        when:
        user.changeDeviceToken(updateDeviceToken)

        then:
        user.deviceToken == updateDeviceToken
    }

    def "유저 디바이스 토큰 변경 시 유효하지 않은 요청값인 경우 예외 발생"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스트")
                .email("test@naver.com")
                .phoneNumber("000-0000-0000")
                .deviceToken("device_token")
                .recentLoginAt(LocalDateTime.MIN)
                .build()

        when:
        user.changeDeviceToken(deviceToken)

        then:
        def e = thrown(IllegalArgumentException.class)
        e.message == expected

        where:
        deviceToken || expected
        null        || "FCM 토큰을 입력해주세요."
        ""          || "FCM 토큰을 입력해주세요."
    }
}
