package com.recipe.app.src.fridge.domain


import spock.lang.Specification

import java.time.LocalDate

class FreshnessTest extends Specification {

    def "유통기한으로 신선도 찾기"() {

        when:
        Freshness result = Freshness.getFreshnessByExpiredAt(expiredAt)

        then:
        result == expected

        where:
        expiredAt                     || expected
        null                          || Freshness.FRESH
        LocalDate.now().plusMonths(1) || Freshness.FRESH
        LocalDate.now().plusDays(7)   || Freshness.FRESH
        LocalDate.now().plusDays(6)   || Freshness.RISKY
        LocalDate.now().plusDays(1)   || Freshness.RISKY
        LocalDate.now()               || Freshness.SPOILED
        LocalDate.now().minusDays(1)  || Freshness.SPOILED
    }
}
