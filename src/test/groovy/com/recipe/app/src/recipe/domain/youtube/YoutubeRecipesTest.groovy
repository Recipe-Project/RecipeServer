package com.recipe.app.src.recipe.domain.youtube

import spock.lang.Specification

import java.time.LocalDate

class YoutubeRecipesTest extends Specification {

    def "유튜브 레시피 목록 내 유튜브 레시피 아이디 목록 가져오기"() {

        given:
        YoutubeRecipes youtubeRecipes = new YoutubeRecipes([
                YoutubeRecipe.builder()
                        .youtubeRecipeId(1)
                        .title("테스트제목")
                        .description("테스트설명")
                        .postDate(LocalDate.of(2024, 1, 1))
                        .channelName("테스트")
                        .youtubeId("youtube1")
                        .thumbnailImgUrl("http://test.jpg")
                        .build(),
                YoutubeRecipe.builder()
                        .youtubeRecipeId(2)
                        .title("테스트제목")
                        .description("테스트")
                        .postDate(LocalDate.of(2024, 1, 1))
                        .channelName("테스트")
                        .youtubeId("youtube2")
                        .thumbnailImgUrl("http://test.jpg")
                        .build(),
                YoutubeRecipe.builder()
                        .youtubeRecipeId(3)
                        .title("제목")
                        .description("설명")
                        .postDate(LocalDate.of(2024, 1, 1))
                        .channelName("테스트")
                        .youtubeId("youtube3")
                        .thumbnailImgUrl("http://test.jpg")
                        .build()
        ])

        when:
        List<Long> youtubeRecipeIds = youtubeRecipes.getYoutubeRecipeIds()

        then:
        youtubeRecipeIds == [1L, 2L, 3L]
    }
}
