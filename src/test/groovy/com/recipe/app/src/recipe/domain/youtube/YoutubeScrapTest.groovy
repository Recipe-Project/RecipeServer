package com.recipe.app.src.recipe.domain.youtube

import spock.lang.Specification

class YoutubeScrapTest extends Specification {

    def "유튜브 레시피 스크렙 정보 생성"() {

        given:
        Long youtubeScrapId = 1
        Long userId = 1
        Long youtubeRecipeId = 1

        when:
        YoutubeScrap youtubeScrap = YoutubeScrap.builder()
                .youtubeScrapId(youtubeScrapId)
                .userId(userId)
                .youtubeRecipeId(youtubeRecipeId)
                .build()

        then:
        youtubeScrap.youtubeScrapId == youtubeScrapId
        youtubeScrap.userId == userId
        youtubeScrap.youtubeRecipeId == youtubeRecipeId
    }

    def "유튜브 레시피 스크랩 시 요청값이 null인 경우 예외 발생"() {

        when:
        YoutubeScrap.builder()
                .userId(userId)
                .youtubeRecipeId(youtubeRecipeId)
                .build()

        then:
        def e = thrown(NullPointerException.class)
        e.message == expected

        where:
        userId | youtubeRecipeId || expected
        null   | 1               || "유저 아이디를 입력해주세요."
        null   | null            || "유저 아이디를 입력해주세요."
        1      | null            || "유튜브 레시피 아이디를 입력해주세요."
    }
}
