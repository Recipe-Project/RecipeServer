package com.recipe.app.src.recipe.application.youtube

import com.recipe.app.src.recipe.domain.youtube.YoutubeView
import com.recipe.app.src.recipe.infra.youtube.YoutubeViewRepository
import spock.lang.Specification

class YoutubeViewServiceTest extends Specification {

    private YoutubeViewRepository youtubeViewRepository = Mock()
    private YoutubeViewService youtubeViewService = new YoutubeViewService(youtubeViewRepository)

    def "유튜브 레시피 조회 정보 생성"() {

        given:
        Long userId = 1
        Long youtubeRecipeId = 1

        youtubeViewRepository.findByUserIdAndYoutubeRecipeId(userId, youtubeRecipeId) >> Optional.empty()

        when:
        youtubeViewService.create(userId, youtubeRecipeId)

        then:
        1 * youtubeViewRepository.save(_)
    }

    def "유튜브 레시피 조회 정보 생성 시 조회 정보가 이미 존재하면 실행 안함"() {

        given:
        Long userId = 1
        Long youtubeRecipeId = 1

        YoutubeView youtubeView = YoutubeView.builder()
                .youtubeViewId(1)
                .userId(userId)
                .youtubeRecipeId(youtubeRecipeId)
                .build()

        youtubeViewRepository.findByUserIdAndYoutubeRecipeId(userId, youtubeRecipeId) >> Optional.of(youtubeView)

        when:
        youtubeViewService.create(userId, youtubeRecipeId)

        then:
        0 * youtubeViewRepository.save(_)
    }

    def "특정 유저의 유튜브 레시피 조회 정보 목록 제거"() {

        given:
        Long userId = 1

        List<YoutubeView> youtubeViews = [
                YoutubeView.builder()
                        .youtubeViewId(1)
                        .userId(userId)
                        .youtubeRecipeId(1)
                        .build(),
                YoutubeView.builder()
                        .youtubeViewId(2)
                        .userId(userId)
                        .youtubeRecipeId(2)
                        .build()
        ]

        youtubeViewRepository.findByUserId(userId) >> youtubeViews

        when:
        youtubeViewService.deleteAllByUserId(userId)

        then:
        1 * youtubeViewRepository.deleteAll(youtubeViews)
    }

    def "특정 유튜브 레시피의 조회 수 조회"() {

        given:
        Long youtubeRecipeId = 1
        youtubeViewRepository.countByYoutubeRecipeId(youtubeRecipeId) >> 5

        when:
        long result = youtubeViewService.countByYoutubeRecipeId(youtubeRecipeId)

        then:
        result == 5
    }
}
