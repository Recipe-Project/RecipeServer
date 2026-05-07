package com.recipe.app.src.common.utils

import spock.lang.Specification

class KoreanTokenizerTest extends Specification {

    def "null/blank 입력은 빈 문자열을 반환한다"() {

        expect:
        KoreanTokenizer.tokenize(input) == ""

        where:
        input << [null, "", "   ", "\t\n"]
    }

    def "동일 입력은 동일 토큰 결과를 반환한다 (idempotency)"() {

        expect:
        KoreanTokenizer.tokenize(input) == KoreanTokenizer.tokenize(input)

        where:
        input << ["소면", "감자전", "김치 찌개 만들기", "에어프라이어"]
    }

    def "한국어 합성어는 NONE 모드로 토큰이 분리되지 않는다"() {

        expect:
        KoreanTokenizer.tokenize(input) == expected

        where:
        input    || expected
        "소면"     || "소면"
        "감자"     || "감자"
        "양파"     || "양파"
        "감자전"   || "감자전"
        "김치찌개" || "김치찌개"
        "닭볶음탕" || "닭볶음탕"
        "고추장"   || "고추장"
        "고춧가루" || "고춧가루"
    }

    def "조사가 붙은 단어는 명사만 추출된다"() {

        when:
        String tokens = KoreanTokenizer.tokenize("감자를 샀다")

        then:
        tokens.contains("감자")
        !tokens.contains("를")
    }

    def "사전에 없는 외래어도 토큰화된다 (분할되더라도 빈 결과는 아님)"() {

        when:
        String tokens = KoreanTokenizer.tokenize("에어프라이어")

        then:
        !tokens.isEmpty()
    }

    def "영어/숫자도 토큰화된다"() {

        when:
        String tokens = KoreanTokenizer.tokenize("Pasta 2024")

        then:
        tokens.toLowerCase().contains("pasta")
        tokens.contains("2024")
    }

    def "단어 경계가 다른 텍스트는 토큰이 겹치지 않는다 (소면 vs 소고기 미역국)"() {

        given:
        Set<String> soMyeon = KoreanTokenizer.tokenize("소면").split(" ") as Set
        Set<String> soGoGiSoup = KoreanTokenizer.tokenize("소고기 미역국").split(" ") as Set

        expect: "소면 검색이 소고기 글의 토큰과 겹치지 않아야 함 (false positive 방지의 핵심 invariant)"
        soMyeon.intersect(soGoGiSoup).isEmpty()
    }
}
