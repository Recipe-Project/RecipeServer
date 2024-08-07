package com.recipe.app.src.recipe.domain.blog

import spock.lang.Specification

import java.time.LocalDate

class BlogRecipesTest extends Specification {

    def "블로그 레시피 목록 내 블로그 레시피 아이디 목록 가져오기"() {

        given:
        BlogRecipes blogRecipes = new BlogRecipes([
                BlogRecipe.builder()
                        .blogRecipeId(1L)
                        .title("테스트제목")
                        .description("설명")
                        .publishedAt(LocalDate.of(2024, 1, 1))
                        .blogUrl("http://naver.com")
                        .blogThumbnailImgUrl("http://test.jpg")
                        .blogName("테스트")
                        .build(),
                BlogRecipe.builder()
                        .blogRecipeId(2L)
                        .title("제목")
                        .description("테스트설명")
                        .publishedAt(LocalDate.of(2024, 1, 1))
                        .blogUrl("http://naver.com")
                        .blogThumbnailImgUrl("http://test.jpg")
                        .blogName("테스트")
                        .build(),
                BlogRecipe.builder()
                        .blogRecipeId(3L)
                        .title("제목")
                        .description("설명")
                        .publishedAt(LocalDate.of(2024, 1, 1))
                        .blogUrl("http://naver.com")
                        .blogThumbnailImgUrl("http://test.jpg")
                        .blogName("테스트")
                        .build()
        ])

        when:
        List<Long> result = blogRecipes.getBlogRecipeIds()

        then:
        result == [1L, 2L, 3L]
    }
}
