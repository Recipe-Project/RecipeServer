package com.recipe.app.src.common.utils

import spock.lang.Specification

class SearchKeywordNormalizerTest extends Specification {

    def "null/empty/blank/1글자 입력은 null을 반환한다"() {

        expect:
        SearchKeywordNormalizer.toBooleanModeQuery(input) == null

        where:
        input << [null, "", "  ", "\t", "감"]
    }

    def "한글 합성어는 그대로 +token 으로 변환된다"() {

        expect:
        SearchKeywordNormalizer.toBooleanModeQuery(input) == expected

        where:
        input    || expected
        "소면"     || "+소면"
        "양파"     || "+양파"
        "감자전"   || "+감자전"
        "김치찌개" || "+김치찌개"
    }

    def "복수 토큰은 +token1 +token2 형식으로 AND 매칭된다"() {

        when:
        String query = SearchKeywordNormalizer.toBooleanModeQuery("감자 양파")

        then:
        query == "+감자 +양파"
    }

    def "BOOLEAN MODE 특수문자는 제거된다"() {

        when:
        String query = SearchKeywordNormalizer.toBooleanModeQuery("+감자 -양파*")

        then:
        query == "+감자 +양파"
    }

    def "조사가 붙은 입력은 명사만 추출되어 반영된다"() {

        when:
        String query = SearchKeywordNormalizer.toBooleanModeQuery("감자를")

        then:
        query == "+감자"
    }

    def "연속 공백/탭은 단일 공백으로 정리된다"() {

        when:
        String query = SearchKeywordNormalizer.toBooleanModeQuery("  감자  \t양파 ")

        then:
        query == "+감자 +양파"
    }
}
