package com.recipe.app.src.recipe.application.blog

import com.recipe.app.src.recipe.domain.blog.BlogScrap
import com.recipe.app.src.recipe.infra.blog.BlogScrapRepository
import spock.lang.Specification

class BlogScrapServiceTest extends Specification {

    private BlogScrapRepository blogScrapRepository = Mock()
    private BlogScrapService blogScrapService = new BlogScrapService(blogScrapRepository)

    def "블로그 스크랩 생성"() {

        given:
        Long userId = 1
        Long blogRecipeId = 1

        blogScrapRepository.findByUserIdAndBlogRecipeId(userId, blogRecipeId) >> Optional.empty()

        when:
        blogScrapService.create(userId, blogRecipeId)

        then:
        1 * blogScrapRepository.save(_)
    }

    def "블로그 스크랩 생성 시 이미 스크랩한 경우 실행 안함"() {

        given:
        Long userId = 1
        Long blogRecipeId = 1

        BlogScrap blogScrap = BlogScrap.builder()
                .blogScrapId(1)
                .userId(userId)
                .blogRecipeId(blogRecipeId)
                .build()

        blogScrapRepository.findByUserIdAndBlogRecipeId(userId, blogRecipeId) >> Optional.of(blogScrap)

        when:
        blogScrapService.create(userId, blogRecipeId)

        then:
        0 * blogScrapRepository.save(_)
    }

    def "블로그 스크랩 삭제"() {

        given:
        Long userId = 1
        Long blogRecipeId = 1

        BlogScrap blogScrap = BlogScrap.builder()
                .blogScrapId(1)
                .userId(userId)
                .blogRecipeId(blogRecipeId)
                .build()

        blogScrapRepository.findByUserIdAndBlogRecipeId(userId, blogRecipeId) >> Optional.of(blogScrap)

        when:
        blogScrapService.delete(userId, blogRecipeId)

        then:
        1 * blogScrapRepository.delete(blogScrap)
    }

    def "블로그 스크랩 삭제 시 해당하는 블로그 스크랩 없으면 실행 안함"() {

        given:
        Long userId = 1
        Long blogRecipeId = 1

        blogScrapRepository.findByUserIdAndBlogRecipeId(userId, blogRecipeId) >> Optional.empty()

        when:
        blogScrapService.delete(userId, blogRecipeId)

        then:
        0 * blogScrapRepository.delete(_)
    }

    def "특정 유저의 블로그 스크랩 목록 삭제"() {

        given:
        Long userId = 1
        List<BlogScrap> blogScraps = [
                BlogScrap.builder()
                        .blogScrapId(1)
                        .userId(userId)
                        .blogRecipeId(1)
                        .build(),
                BlogScrap.builder()
                        .blogScrapId(1)
                        .userId(userId)
                        .blogRecipeId(2)
                        .build(),
        ]

        blogScrapRepository.findByUserId(userId) >> blogScraps

        when:
        blogScrapService.deleteAllByUserId(userId)

        then:
        1 * blogScrapRepository.deleteAll(blogScraps)
    }

    def "특정 유저의 블로그 스크랩 수 조회"() {

        given:
        Long userId = 1
        blogScrapRepository.countByUserId(userId) >> 5

        when:
        long result = blogScrapService.countByUserId(userId)

        then:
        result == 5
    }

    def "블로그 레시피 아이디 목록에 해당하는 블로그 레시피 스크랩 목록 조회"() {

        given:
        List<Long> blogRecipeIds = [1, 2, 3]

        List<BlogScrap> blogScraps = [
                BlogScrap.builder()
                        .blogScrapId(1)
                        .userId(1)
                        .blogRecipeId(1)
                        .build(),
                BlogScrap.builder()
                        .blogScrapId(1)
                        .userId(1)
                        .blogRecipeId(2)
                        .build(),
                BlogScrap.builder()
                        .blogScrapId(1)
                        .userId(2)
                        .blogRecipeId(2)
                        .build(),
        ]

        blogScrapRepository.findByBlogRecipeIdIn(blogRecipeIds) >> blogScraps

        when:
        List<BlogScrap> result = blogScrapService.findByBlogRecipeIds(blogRecipeIds)

        then:
        result == blogScraps
    }

    def "특정 블로그 레시피의 블로그 스크랩 수 조회"() {

        given:
        Long blogRecipeId = 1
        blogScrapRepository.countByBlogRecipeId(blogRecipeId) >> 5

        when:
        long result = blogScrapService.countByBlogRecipeId(blogRecipeId)

        then:
        result == 5
    }

    def "유저 아이디와 블로그 레시피 아이디로 블로그 스크랩 조회"() {

        given:
        Long userId = 1
        Long blogRecipeId = 1

        BlogScrap blogScrap = BlogScrap.builder()
                .blogScrapId(1)
                .userId(userId)
                .blogRecipeId(blogRecipeId)
                .build()

        blogScrapRepository.findByUserIdAndBlogRecipeId(userId, blogRecipeId) >> Optional.of(blogScrap)

        when:
        BlogScrap result = blogScrapService.findByUserIdAndBlogRecipeId(userId, blogRecipeId)

        then:
        result == blogScrap
    }

    def "유저 아이디와 블로그 레시피 아이디로 블로그 스크랩 조회 시 없으면 null 반환"() {

        given:
        Long userId = 1
        Long blogRecipeId = 1

        blogScrapRepository.findByUserIdAndBlogRecipeId(userId, blogRecipeId) >> Optional.empty()

        when:
        BlogScrap result = blogScrapService.findByUserIdAndBlogRecipeId(userId, blogRecipeId)

        then:
        result == null
    }
}
