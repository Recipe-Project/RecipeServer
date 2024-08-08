package com.recipe.app.src.recipe.domain.blog

import spock.lang.Specification

class BlogViewTest extends Specification {

    def "블로그 조회 정보 생성"() {

        given:
        Long blogViewId = 1L
        Long userId = 1L
        Long blogRecipeId = 1L

        when:
        BlogView blogView = BlogView.builder()
                .blogViewId(blogViewId)
                .userId(userId)
                .blogRecipeId(blogRecipeId)
                .build()

        then:
        blogView.blogViewId == blogViewId
        blogView.userId == userId
        blogView.blogRecipeId == blogRecipeId
    }

    def "블로그 조회 정보 생성 시 null 인 요청값인 경우 예외 발생"() {

        when:
        BlogView.builder()
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
