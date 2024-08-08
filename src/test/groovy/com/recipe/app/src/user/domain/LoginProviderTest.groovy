package com.recipe.app.src.user.domain

import spock.lang.Specification

class LoginProviderTest extends Specification {

    def "소셜 아이디에 포함된 글자로 로그인 방법 찾기"() {

        when:
        LoginProvider result = LoginProvider.findLoginProvider(socialId)

        then:
        result == expected

        where:
        socialId   || expected
        "naver_1"  || LoginProvider.NAVER
        "kakao_1"  || LoginProvider.KAKAO
        "google_1" || LoginProvider.GOOGLE
    }

    def "소셜 아이디에 포함된 글자로 로그인 방법 찾을 때 해당하는 값이 없으면 예외 발생"() {

        when:
        LoginProvider.findLoginProvider("abcde")

        then:
        def e = thrown(IllegalArgumentException.class)
        e.message == "로그인 정보를 찾을 수 없습니다."
    }
}
