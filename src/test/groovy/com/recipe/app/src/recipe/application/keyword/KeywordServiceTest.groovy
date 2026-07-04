package com.recipe.app.src.recipe.application.keyword

import com.recipe.app.src.recipe.domain.keyword.Keyword
import com.recipe.app.src.recipe.infra.keyword.KeywordRepository
import spock.lang.Specification
import spock.lang.Timeout

import java.util.concurrent.TimeUnit

class KeywordServiceTest extends Specification {

    KeywordRepository keywordRepository = Mock()
    KeywordService keywordService = new KeywordService(keywordRepository)

    def kw(String k) {
        Stub(Keyword) { getKeyword() >> k }
    }

    def "검색어가 없으면 빈 목록을 반환한다 (예외 없이)"() {

        given:
        keywordRepository.findAll() >> []

        expect:
        keywordService.retrieveRecipesBestKeyword() == []
    }

    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    def "고유 검색어가 10개 미만이면 무한루프 없이 보유 개수만 반환한다"() {

        given:
        keywordRepository.findAll() >> [kw("감자"), kw("양파"), kw("당근")]

        when:
        List<String> result = keywordService.retrieveRecipesBestKeyword()

        then:
        result.size() == 3
        result.toSet() == ["감자", "양파", "당근"].toSet()
    }

    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    def "고유 검색어가 10개 이상이면 10개를 반환한다"() {

        given:
        keywordRepository.findAll() >> (1..15).collect { kw("키워드" + it) }

        when:
        List<String> result = keywordService.retrieveRecipesBestKeyword()

        then:
        result.size() == 10
        result.unique().size() == 10
    }
}
