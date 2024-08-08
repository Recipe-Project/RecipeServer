package com.recipe.app.src.recipe.application.youtube


import com.recipe.app.src.recipe.domain.youtube.YoutubeScrap
import com.recipe.app.src.recipe.infra.youtube.YoutubeScrapRepository
import spock.lang.Specification

class YoutubeScrapServiceTest extends Specification {

    private YoutubeScrapRepository youtubeScrapRepository = Mock()
    private YoutubeScrapService youtubeScrapService = new YoutubeScrapService(youtubeScrapRepository)

    def "유튜브 스크랩 생성"() {

        given:
        Long userId = 1
        Long youtubeRecipeId = 1

        youtubeScrapRepository.findByUserIdAndYoutubeRecipeId(userId, youtubeRecipeId) >> Optional.empty()

        when:
        youtubeScrapService.create(userId, youtubeRecipeId)

        then:
        1 * youtubeScrapRepository.save(_)
    }

    def "유튜브 스크랩 생성 시 이미 스크랩한 경우 실행 안함"() {

        given:
        Long userId = 1
        Long youtubeRecipeId = 1

        YoutubeScrap youtubeScrap = YoutubeScrap.builder()
                .youtubeScrapId(1)
                .userId(userId)
                .youtubeRecipeId(youtubeRecipeId)
                .build()

        youtubeScrapRepository.findByUserIdAndYoutubeRecipeId(userId, youtubeRecipeId) >> Optional.of(youtubeScrap)

        when:
        youtubeScrapService.create(userId, youtubeRecipeId)

        then:
        0 * youtubeScrapRepository.save(_)
    }

    def "유튜브 스크랩 삭제"() {

        given:
        Long userId = 1
        Long youtubeRecipeId = 1

        YoutubeScrap youtubeScrap = YoutubeScrap.builder()
                .youtubeScrapId(1)
                .userId(userId)
                .youtubeRecipeId(youtubeRecipeId)
                .build()

        youtubeScrapRepository.findByUserIdAndYoutubeRecipeId(userId, youtubeRecipeId) >> Optional.of(youtubeScrap)

        when:
        youtubeScrapService.delete(userId, youtubeRecipeId)

        then:
        1 * youtubeScrapRepository.delete(youtubeScrap)
    }

    def "유튜브 스크랩 삭제 시 해당하는 유튜브 스크랩 없으면 실행 안함"() {

        given:
        Long userId = 1
        Long youtubeRecipeId = 1

        youtubeScrapRepository.findByUserIdAndYoutubeRecipeId(userId, youtubeRecipeId) >> Optional.empty()

        when:
        youtubeScrapService.delete(userId, youtubeRecipeId)

        then:
        0 * youtubeScrapRepository.delete(_)
    }

    def "특정 유저의 유튜브 스크랩 목록 삭제"() {

        given:
        Long userId = 1
        List<YoutubeScrap> youtubeScraps = [
                YoutubeScrap.builder()
                        .youtubeScrapId(1)
                        .userId(userId)
                        .youtubeRecipeId(1)
                        .build(),
                YoutubeScrap.builder()
                        .youtubeScrapId(2)
                        .userId(userId)
                        .youtubeRecipeId(2)
                        .build(),
        ]

        youtubeScrapRepository.findByUserId(userId) >> youtubeScraps

        when:
        youtubeScrapService.deleteAllByUserId(userId)

        then:
        1 * youtubeScrapRepository.deleteAll(youtubeScraps)
    }

    def "특정 유저의 유튜브 스크랩 수 조회"() {

        given:
        Long userId = 1
        youtubeScrapRepository.countByUserId(userId) >> 5

        when:
        long result = youtubeScrapService.countByUserId(userId)

        then:
        result == 5
    }

    def "유튜브 레시피 아이디 목록에 해당하는 유튜브 레시피 스크랩 목록 조회"() {

        given:
        List<Long> youtubeRecipeIds = [1, 2, 3]

        List<YoutubeScrap> youtubeScraps = [
                YoutubeScrap.builder()
                        .youtubeScrapId(1)
                        .userId(1)
                        .youtubeRecipeId(1)
                        .build(),
                YoutubeScrap.builder()
                        .youtubeScrapId(2)
                        .userId(1)
                        .youtubeRecipeId(2)
                        .build(),
                YoutubeScrap.builder()
                        .youtubeScrapId(3)
                        .userId(2)
                        .youtubeRecipeId(2)
                        .build(),
        ]

        youtubeScrapRepository.findByYoutubeRecipeIdIn(youtubeRecipeIds) >> youtubeScraps

        when:
        List<YoutubeScrap> result = youtubeScrapService.findByYoutubeRecipeIds(youtubeRecipeIds)

        then:
        result == youtubeScraps
    }

    def "특정 유튜브 레시피의 유튜브 스크랩 수 조회"() {

        given:
        Long youtubeRecipeId = 1
        youtubeScrapRepository.countByYoutubeRecipeId(youtubeRecipeId) >> 5

        when:
        long result = youtubeScrapService.countByYoutubeRecipeId(youtubeRecipeId)

        then:
        result == 5
    }

    def "유저 아이디와 유튜브 레시피 아이디로 유튜브 스크랩 조회"() {

        given:
        Long userId = 1
        Long youtubeRecipeId = 1

        YoutubeScrap youtubeScrap = YoutubeScrap.builder()
                .youtubeScrapId(1)
                .userId(userId)
                .youtubeRecipeId(youtubeRecipeId)
                .build()

        youtubeScrapRepository.findByUserIdAndYoutubeRecipeId(userId, youtubeRecipeId) >> Optional.of(youtubeScrap)

        when:
        YoutubeScrap result = youtubeScrapService.findByUserIdAndYoutubeRecipeId(userId, youtubeRecipeId)

        then:
        result == youtubeScrap
    }

    def "유저 아이디와 유튜브 레시피 아이디로 유튜브 스크랩 조회 시 없으면 null 반환"() {

        given:
        Long userId = 1
        Long youtubeRecipeId = 1

        youtubeScrapRepository.findByUserIdAndYoutubeRecipeId(userId, youtubeRecipeId) >> Optional.empty()

        when:
        YoutubeScrap result = youtubeScrapService.findByUserIdAndYoutubeRecipeId(userId, youtubeRecipeId)

        then:
        result == null
    }
}
