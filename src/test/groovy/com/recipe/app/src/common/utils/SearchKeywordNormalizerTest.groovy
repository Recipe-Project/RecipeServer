package com.recipe.app.src.common.utils

import com.recipe.app.src.common.utils.SearchKeywordNormalizer.SearchQuery
import spock.lang.Specification

class SearchKeywordNormalizerTest extends Specification {

    def "null/empty/blank 입력은 Empty 를 반환한다"() {

        expect:
        SearchKeywordNormalizer.normalize(input) instanceof SearchQuery.Empty

        where:
        input << [null, "", "  ", "\t"]
    }

    def "1글자 입력은 ExactToken 으로 반환된다"() {

        when:
        SearchQuery query = SearchKeywordNormalizer.normalize(input)

        then:
        query instanceof SearchQuery.ExactToken
        ((SearchQuery.ExactToken) query).token() == expectedToken

        where:
        input || expectedToken
        "감"   || "감"
        "갓"   || "갓"
        "닭"   || "닭"
        "B"    || "b"
    }

    def "한글 합성어는 BooleanQuery 로 +token 형태가 된다"() {

        when:
        SearchQuery query = SearchKeywordNormalizer.normalize(input)

        then:
        query instanceof SearchQuery.BooleanQuery
        ((SearchQuery.BooleanQuery) query).query() == expected

        where:
        input    || expected
        "소면"     || "+소면"
        "양파"     || "+양파"
        "감자전"   || "+감자전"
        "김치찌개" || "+김치찌개"
    }

    def "복수 토큰은 +token1 +token2 형식으로 AND 매칭된다"() {

        when:
        SearchQuery query = SearchKeywordNormalizer.normalize("감자 양파")

        then:
        query instanceof SearchQuery.BooleanQuery
        ((SearchQuery.BooleanQuery) query).query() == "+감자 +양파"
    }

    def "BOOLEAN MODE 특수문자는 제거된다"() {

        when:
        SearchQuery query = SearchKeywordNormalizer.normalize("+감자 -양파*")

        then:
        query instanceof SearchQuery.BooleanQuery
        ((SearchQuery.BooleanQuery) query).query() == "+감자 +양파"
    }

    def "조사가 붙은 입력은 명사만 추출되어 반영된다"() {

        when:
        SearchQuery query = SearchKeywordNormalizer.normalize("감자를")

        then:
        query instanceof SearchQuery.BooleanQuery
        ((SearchQuery.BooleanQuery) query).query() == "+감자"
    }

    def "연속 공백/탭은 단일 공백으로 정리된다"() {

        when:
        SearchQuery query = SearchKeywordNormalizer.normalize("  감자  \t양파 ")

        then:
        query instanceof SearchQuery.BooleanQuery
        ((SearchQuery.BooleanQuery) query).query() == "+감자 +양파"
    }
}
