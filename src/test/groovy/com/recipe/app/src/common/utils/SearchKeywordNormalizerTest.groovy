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

    def "한글 합성어는 BooleanQuery 로 OR(연산자 없는 토큰 나열) 형태가 된다"() {

        when:
        SearchQuery query = SearchKeywordNormalizer.normalize(input)

        then:
        query instanceof SearchQuery.BooleanQuery
        ((SearchQuery.BooleanQuery) query).query() == expected

        where:
        input    || expected
        "소면"     || "소면"
        "양파"     || "양파"
        "감자전"   || "감자전"
        "김치찌개" || "김치찌개"
    }

    def "복수 토큰은 공백 구분 OR 형식으로 매칭된다"() {

        when:
        SearchQuery query = SearchKeywordNormalizer.normalize("감자 양파")

        then:
        query instanceof SearchQuery.BooleanQuery
        ((SearchQuery.BooleanQuery) query).query() == "감자 양파"
    }

    def "1글자로 쪼개지는 합성어도 OR 라 1글자 토큰이 그대로 남는다 (닭가슴살)"() {

        when: "nori 가 '닭가슴살' 을 '닭'(1글자) + '가슴살' 로 분리"
        SearchQuery query = SearchKeywordNormalizer.normalize("닭가슴살")

        then: "OR 이므로 1글자 '닭' 이 남아도 FULLTEXT 가 무시하고 '가슴살' 로 매칭 가능 (AND 였으면 +닭 때문에 0건)"
        query instanceof SearchQuery.BooleanQuery
        ((SearchQuery.BooleanQuery) query).query() == "닭 가슴살"
    }

    def "BOOLEAN MODE 특수문자는 제거된다"() {

        when:
        SearchQuery query = SearchKeywordNormalizer.normalize("+감자 -양파*")

        then:
        query instanceof SearchQuery.BooleanQuery
        ((SearchQuery.BooleanQuery) query).query() == "감자 양파"
    }

    def "조사가 붙은 입력은 명사만 추출되어 반영된다"() {

        when:
        SearchQuery query = SearchKeywordNormalizer.normalize("감자를")

        then:
        query instanceof SearchQuery.BooleanQuery
        ((SearchQuery.BooleanQuery) query).query() == "감자"
    }

    def "연속 공백/탭은 단일 공백으로 정리된다"() {

        when:
        SearchQuery query = SearchKeywordNormalizer.normalize("  감자  \t양파 ")

        then:
        query instanceof SearchQuery.BooleanQuery
        ((SearchQuery.BooleanQuery) query).query() == "감자 양파"
    }
}
