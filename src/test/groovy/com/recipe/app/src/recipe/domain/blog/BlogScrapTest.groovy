package com.recipe.app.src.recipe.domain.blog

import spock.lang.Specification

class BlogScrapTest extends Specification {

    def "블로그 스크랩 정보 생성"() {

        given:
        Long blogScrapId = 1L
        Long userId = 1L
        Long blogRecipeId = 1L

        when:
        BlogScrap blogScrap = BlogScrap.builder()
                .blogScrapId(blogScrapId)
                .userId(userId)
                .blogRecipeId(blogRecipeId)
                .build()

        then:
        blogScrap.blogScrapId == blogScrapId
        blogScrap.userId == userId
        blogScrap.blogRecipeId == blogRecipeId
    }

    def "블로그 스크랩 정보 생성 시 요청값이 Null 인 경우 예외 발생"() {

        when:
        BlogScrap.builder()
                .userId(userId)
                .blogRecipeId(blogRecipeId)
                .build()

        then:
        def e = thrown(NullPointerException.class)
        e.message == expected

        where:
        userId | blogRecipeId || expected
        null   | 1L           || "유저 아이디를 입력해주세요."
        null   | null         || "유저 아이디를 입력해주세요."
        1L     | null         || "블로그 레시피 아이디를 입력해주세요."
    }
}
