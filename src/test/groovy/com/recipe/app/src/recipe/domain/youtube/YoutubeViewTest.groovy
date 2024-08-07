package com.recipe.app.src.recipe.domain.youtube

import spock.lang.Specification

class YoutubeViewTest extends Specification {

    def "유튜브 조회 정보 생성"() {

        given:
        Long youtubeViewId = 1
        Long userId = 1
        Long youtubeRecipeId = 1

        when:
        YoutubeView youtubeView = YoutubeView.builder()
                .youtubeViewId(youtubeViewId)
                .userId(userId)
                .youtubeRecipeId(youtubeRecipeId)
                .build()

        then:
        youtubeView.youtubeViewId == youtubeViewId
        youtubeView.userId == userId
        youtubeView.youtubeRecipeId == youtubeRecipeId
    }

    def "유튜브 조회 정보 생성 시 요청값이 null 인 경우 예외 발생"() {

        when:
        YoutubeView.builder()
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
