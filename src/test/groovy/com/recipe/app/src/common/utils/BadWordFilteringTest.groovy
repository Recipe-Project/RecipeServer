package com.recipe.app.src.common.utils

import com.recipe.app.src.etc.exception.BadWordException
import spock.lang.Specification

class BadWordFilteringTest extends Specification {

    private BadWordFiltering badWordFiltering = new BadWordFiltering();

    def "비속어 테스트"() {

        when:
        badWordFiltering.check(keyword)

        then:
        def e = thrown(BadWordException.class)
        e.message == expected

        where:
        keyword || expected
        "시발"    || "금칙어 설정된 단어(시발)를 포함하고 있습니다."
        "시//발"    || "금칙어 설정된 단어(시발)를 포함하고 있습니다."
        "시 발"    || "금칙어 설정된 단어(시발)를 포함하고 있습니다."
    }
}
