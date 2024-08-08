package com.recipe.app.src.recipe.domain.blog

import spock.lang.Specification

import java.time.LocalDate

class BlogRecipeTest extends Specification {

    def "블로그 레시피 생성"() {

        given:
        Long blogRecipeId = 1
        String blogUrl = "https://naver.com"
        String blogThumbnailImgUrl = ""
        String title = "제목"
        String description = "설명"
        LocalDate publishedAt = LocalDate.now()
        String blogName = "블로그명"
        long scrapCnt = 1
        long viewCnt = 1

        when:
        BlogRecipe blogRecipe = BlogRecipe.builder()
                .blogRecipeId(blogRecipeId)
                .blogUrl(blogUrl)
                .blogThumbnailImgUrl(blogThumbnailImgUrl)
                .title(title)
                .description(description)
                .publishedAt(publishedAt)
                .blogName(blogName)
                .scrapCnt(scrapCnt)
                .viewCnt(viewCnt)
                .build()

        then:
        blogRecipe.blogRecipeId == blogRecipeId
        blogRecipe.blogUrl == blogUrl
        blogRecipe.blogThumbnailImgUrl == blogThumbnailImgUrl
        blogRecipe.title == title
        blogRecipe.description == description
        blogRecipe.publishedAt == publishedAt
        blogRecipe.blogName == blogName
        blogRecipe.scrapCnt == scrapCnt
        blogRecipe.viewCnt == viewCnt
    }

    def "블로그 레시피 생성 시 요청 값이 null 인 경우 예외 발생"() {

        when:
        BlogRecipe.builder()
                .blogRecipeId(1L)
                .blogUrl("https://naver.com")
                .blogThumbnailImgUrl("")
                .title("제목")
                .description("설명")
                .publishedAt(null)
                .blogName("블로그명")
                .scrapCnt(1)
                .viewCnt(1)
                .build()

        then:
        def e = thrown(NullPointerException.class)
        e.message == "블로그 레시피 게시 날짜를 입력해주세요."
    }

    def "블로그 레시피 생성 시 유효하지 않은 요청 값인 경우 예외 발생"() {

        when:
        BlogRecipe.builder()
                .blogRecipeId(1L)
                .blogUrl(blogUrl)
                .blogThumbnailImgUrl("")
                .title(title)
                .description(description)
                .publishedAt(LocalDate.now())
                .blogName(blogName)
                .scrapCnt(1)
                .viewCnt(1)
                .build()

        then:
        def e = thrown(IllegalArgumentException.class)
        e.message == expected

        where:
        blogUrl             | title | description | blogName || expected
        null                | "제목"  | "설명"        | "블로그명"   || "블로그 URL을 입력해주세요."
        ""                  | "제목"  | "설명"        | "블로그명"   || "블로그 URL을 입력해주세요."
        "https://naver.com" | null  | "설명"        | "블로그명"   || "블로그 레시피 제목을 입력해주세요."
        "https://naver.com" | ""    | "설명"        | "블로그명"   || "블로그 레시피 제목을 입력해주세요."
        "https://naver.com" | "제목"  | null        | "블로그명"   || "블로그 레시피 설명을 입력해주세요."
        "https://naver.com" | "제목"  | ""          | "블로그명"   || "블로그 레시피 설명을 입력해주세요."
        "https://naver.com" | "제목"  | "설명"        | null     || "블로그명을 입력해주세요."
        "https://naver.com" | "제목"  | "설명"        | ""       || "블로그명을 입력해주세요."
    }

    def "블로그 레시피 스크랩 수 증가"() {

        given:
        BlogRecipe blogRecipe = BlogRecipe.builder()
                .blogRecipeId(1L)
                .blogUrl("https://naver.com")
                .blogThumbnailImgUrl("")
                .title("제목")
                .description("설명")
                .publishedAt(LocalDate.now())
                .blogName("블로그명")
                .scrapCnt(1)
                .viewCnt(1)
                .build()

        when:
        blogRecipe.plusScrapCnt()

        then:
        blogRecipe.scrapCnt == 2
    }

    def "블로그 레시피 스크랩 수 감소"() {

        given:
        BlogRecipe blogRecipe = BlogRecipe.builder()
                .blogRecipeId(1L)
                .blogUrl("https://naver.com")
                .blogThumbnailImgUrl("")
                .title("제목")
                .description("설명")
                .publishedAt(LocalDate.now())
                .blogName("블로그명")
                .scrapCnt(1)
                .viewCnt(1)
                .build()

        when:
        blogRecipe.minusScrapCnt()

        then:
        blogRecipe.scrapCnt == 0
    }

    def "블로그 레시피 조회 수 증가"() {

        given:
        BlogRecipe blogRecipe = BlogRecipe.builder()
                .blogRecipeId(1L)
                .blogUrl("https://naver.com")
                .blogThumbnailImgUrl("")
                .title("제목")
                .description("설명")
                .publishedAt(LocalDate.now())
                .blogName("블로그명")
                .scrapCnt(1)
                .viewCnt(1)
                .build()

        when:
        blogRecipe.plusViewCnt()

        then:
        blogRecipe.viewCnt == 2
    }

    def "블로그 레시피 썸네일 변경"() {

        given:
        BlogRecipe blogRecipe = BlogRecipe.builder()
                .blogRecipeId(1L)
                .blogUrl("https://naver.com")
                .blogThumbnailImgUrl("")
                .title("제목")
                .description("설명")
                .publishedAt(LocalDate.now())
                .blogName("블로그명")
                .scrapCnt(1)
                .viewCnt(1)
                .build()

        String thumbnailImgUrl = "http://test.com"

        when:
        blogRecipe.changeThumbnail(thumbnailImgUrl)

        then:
        blogRecipe.blogThumbnailImgUrl == thumbnailImgUrl
    }
}
