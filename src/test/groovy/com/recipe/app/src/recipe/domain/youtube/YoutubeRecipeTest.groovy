package com.recipe.app.src.recipe.domain.youtube

import spock.lang.Specification

import java.time.LocalDate

class YoutubeRecipeTest extends Specification {

    def "유튜브 레시피 생성"() {

        given:
        Long youtubeRecipeId = 1
        String title = "제목"
        String description = "설명"
        String thumbnailImgUrl = "http://img.jpg"
        LocalDate postDate = LocalDate.now()
        String channelName = "채널명"
        String youtubeId = "abcdef"
        long scrapCnt = 1
        long viewCnt = 1

        when:
        YoutubeRecipe youtubeRecipe = YoutubeRecipe.builder()
                .youtubeRecipeId(youtubeRecipeId)
                .title(title)
                .description(description)
                .thumbnailImgUrl(thumbnailImgUrl)
                .postDate(postDate)
                .channelName(channelName)
                .youtubeId(youtubeId)
                .scrapCnt(scrapCnt)
                .viewCnt(viewCnt)
                .build()

        then:
        youtubeRecipe.youtubeRecipeId == youtubeRecipeId
        youtubeRecipe.title == title
        youtubeRecipe.description == description
        youtubeRecipe.thumbnailImgUrl == thumbnailImgUrl
        youtubeRecipe.postDate == postDate
        youtubeRecipe.channelName == channelName
        youtubeRecipe.youtubeId == youtubeId
        youtubeRecipe.scrapCnt == scrapCnt
        youtubeRecipe.viewCnt == viewCnt
    }

    def "유튜브 레시피 생성 시 요청값이 null 인 경우 예외 발생"() {

        when:
        YoutubeRecipe.builder()
                .youtubeRecipeId(1)
                .title("제목")
                .description("설명")
                .thumbnailImgUrl("http://img.jpg")
                .postDate(null)
                .channelName("채널명")
                .youtubeId("abcdef")
                .scrapCnt(1)
                .viewCnt(1)
                .build()

        then:
        def e = thrown(NullPointerException.class)
        e.message == "유튜브 게시 날짜를 입력해주세요."
    }

    def "유튜브 레시피 생성 시 유효하지 않은 요청값인 경우 예외 발생"() {

        when:
        YoutubeRecipe.builder()
                .youtubeRecipeId(1)
                .title(title)
                .description("")
                .thumbnailImgUrl(thumbnailImgUrl)
                .postDate(LocalDate.now())
                .channelName(channelName)
                .youtubeId(youtubeId)
                .scrapCnt(1)
                .viewCnt(1)
                .build()

        then:
        def e = thrown(IllegalArgumentException.class)
        e.message == expected

        where:
        title | thumbnailImgUrl  | channelName | youtubeId || expected
        null  | "http://img.jpg" | "채널명"       | "abcdef"  || "유튜브 레시피 제목을 입력해주세요."
        ""    | "http://img.jpg" | "채널명"       | "abcdef"  || "유튜브 레시피 제목을 입력해주세요."
        "제목"  | null             | "채널명"       | "abcdef"  || "유튜브 레시피 썸네일 URL을 입력해주세요."
        "제목"  | ""               | "채널명"       | "abcdef"  || "유튜브 레시피 썸네일 URL을 입력해주세요."
        "제목"  | "http://img.jpg" | null        | "abcdef"  || "유튜브 채널명을 입력해주세요."
        "제목"  | "http://img.jpg" | ""          | "abcdef"  || "유튜브 채널명을 입력해주세요."
        "제목"  | "http://img.jpg" | "채널명"       | null      || "유튜브 아이디를 입력해주세요."
        "제목"  | "http://img.jpg" | "채널명"       | ""        || "유튜브 아이디를 입력해주세요."
    }

    def "유튜브 레시피 스크랩 수 증가"() {

        given:
        YoutubeRecipe youtubeRecipe = YoutubeRecipe.builder()
                .youtubeRecipeId(1)
                .title("제목")
                .description("설명")
                .thumbnailImgUrl("http://img.jpg")
                .postDate(LocalDate.now())
                .channelName("채널명")
                .youtubeId("abcdef")
                .scrapCnt(1)
                .viewCnt(1)
                .build()

        when:
        youtubeRecipe.plusScrapCnt()

        then:
        youtubeRecipe.scrapCnt == 2
    }

    def "유튜브 레시피 스크랩 수 감소"() {

        given:
        YoutubeRecipe youtubeRecipe = YoutubeRecipe.builder()
                .youtubeRecipeId(1)
                .title("제목")
                .description("설명")
                .thumbnailImgUrl("http://img.jpg")
                .postDate(LocalDate.now())
                .channelName("채널명")
                .youtubeId("abcdef")
                .scrapCnt(1)
                .viewCnt(1)
                .build()

        when:
        youtubeRecipe.minusScrapCnt()

        then:
        youtubeRecipe.scrapCnt == 0
    }

    def "유튜브 레시피 조회 수 증가"() {

        given:
        YoutubeRecipe youtubeRecipe = YoutubeRecipe.builder()
                .youtubeRecipeId(1)
                .title("제목")
                .description("설명")
                .thumbnailImgUrl("http://img.jpg")
                .postDate(LocalDate.now())
                .channelName("채널명")
                .youtubeId("abcdef")
                .scrapCnt(1)
                .viewCnt(1)
                .build()

        when:
        youtubeRecipe.plusViewCnt()

        then:
        youtubeRecipe.viewCnt == 2
    }
}
