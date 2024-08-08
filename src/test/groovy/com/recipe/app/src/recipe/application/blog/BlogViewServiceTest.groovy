package com.recipe.app.src.recipe.application.blog

import com.recipe.app.src.recipe.domain.blog.BlogView
import com.recipe.app.src.recipe.infra.blog.BlogViewRepository
import spock.lang.Specification

class BlogViewServiceTest extends Specification {

    private BlogViewRepository blogViewRepository = Mock()
    private BlogViewService blogViewService = new BlogViewService(blogViewRepository)

    def "레시피 조회 정보 새로 생성"() {

        given:
        Long userId = 1
        Long blogRecipeId = 1

        blogViewRepository.findByUserIdAndBlogRecipeId(userId, blogRecipeId) >> Optional.empty()

        when:
        blogViewService.create(userId, blogRecipeId)

        then:
        1 * blogViewRepository.save(_)
    }

    def "레시피 조회 정보 생성 시 이미 조회한 경우 생성하지 않음"() {

        given:
        Long userId = 1
        Long blogRecipeId = 1

        BlogView blogView = BlogView.builder()
                .blogViewId(1)
                .userId(userId)
                .blogRecipeId(blogRecipeId)
                .build()

        blogViewRepository.findByUserIdAndBlogRecipeId(userId, blogRecipeId) >> Optional.of(blogView)

        when:
        blogViewService.create(userId, blogRecipeId)

        then:
        0 * blogViewRepository.save(_)
    }

    def "특정 유저의 블로그 레시피 조회 정보 목록 제거"() {

        given:
        Long userId = 1
        List<BlogView> blogViews = [
                BlogView.builder()
                        .blogViewId(1)
                        .userId(userId)
                        .blogRecipeId(1)
                        .build(),
                BlogView.builder()
                        .blogViewId(2)
                        .userId(userId)
                        .blogRecipeId(2)
                        .build()
        ]

        blogViewRepository.findByUserId(userId) >> blogViews

        when:
        blogViewService.deleteAllByUserId(userId)

        then:
        1 * blogViewRepository.deleteAll(blogViews)
    }

    def "특정 블로그 레시피의 조회수 조회"() {

        given:
        Long blogRecipeId = 1
        blogViewRepository.countByBlogRecipeId(blogRecipeId) >> 5

        when:
        long result = blogViewService.countByBlogRecipeId(blogRecipeId)

        then:
        result == 5
    }
}
