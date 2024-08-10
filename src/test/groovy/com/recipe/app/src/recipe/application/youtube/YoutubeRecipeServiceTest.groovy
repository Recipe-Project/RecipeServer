package com.recipe.app.src.recipe.application.youtube

import com.recipe.app.src.common.utils.BadWordFiltering
import com.recipe.app.src.recipe.application.dto.RecipesResponse
import com.recipe.app.src.recipe.domain.youtube.YoutubeRecipe
import com.recipe.app.src.recipe.domain.youtube.YoutubeScrap
import com.recipe.app.src.recipe.infra.youtube.YoutubeRecipeRepository
import com.recipe.app.src.user.domain.User
import spock.lang.Specification

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class YoutubeRecipeServiceTest extends Specification {

    private YoutubeRecipeRepository youtubeRecipeRepository = Mock()
    private YoutubeScrapService youtubeScrapService = Mock()
    private YoutubeViewService youtubeViewService = Mock()
    private BadWordFiltering badWordService = Mock()
    private YoutubeRecipeClientSearchService youtubeRecipeClientSearchService = Mock()
    private YoutubeRecipeService youtubeRecipeService = new YoutubeRecipeService(youtubeRecipeRepository, youtubeScrapService,
            youtubeViewService, badWordService, youtubeRecipeClientSearchService)

    def "유튜브 레시피 검색 - 스크랩순"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        String keyword = "테스트"
        long lastYoutubeRecipeId = 0
        int size = 2
        String sort = "youtubeScraps"

        youtubeRecipeRepository.countByKeyword(keyword) >> 20

        youtubeScrapService.countByYoutubeRecipeId(lastYoutubeRecipeId) >> 0

        List<YoutubeRecipe> youtubeRecipes = [
                YoutubeRecipe.builder()
                        .youtubeRecipeId(1)
                        .title("제목")
                        .description("설명")
                        .thumbnailImgUrl("http://img.jpg")
                        .postDate(LocalDate.now())
                        .channelName("채널명")
                        .youtubeId("abcdef")
                        .scrapCnt(1)
                        .viewCnt(1)
                        .build(),
                YoutubeRecipe.builder()
                        .youtubeRecipeId(2)
                        .title("제목")
                        .description("설명")
                        .thumbnailImgUrl("http://img.jpg")
                        .postDate(LocalDate.now())
                        .channelName("채널명")
                        .youtubeId("abcdef")
                        .scrapCnt(1)
                        .viewCnt(1)
                        .build()
        ]

        youtubeRecipeRepository.findByKeywordLimitOrderByYoutubeScrapCntDesc(keyword, lastYoutubeRecipeId, 0, size) >> youtubeRecipes

        List<YoutubeScrap> youtubeScraps = [
                YoutubeScrap.builder()
                        .youtubeScrapId(1)
                        .userId(user.userId)
                        .youtubeRecipeId(youtubeRecipes.get(0).youtubeRecipeId)
                        .build()
        ]

        youtubeScrapService.findByYoutubeRecipeIds(youtubeRecipes.youtubeRecipeId) >> youtubeScraps

        when:
        RecipesResponse result = youtubeRecipeService.findYoutubeRecipesByKeyword(user, keyword, lastYoutubeRecipeId, size, sort)

        then:
        result.totalCnt == 20
        result.recipes.recipeId == youtubeRecipes.youtubeRecipeId
        result.recipes.recipeName == youtubeRecipes.title
        result.recipes.introduction == youtubeRecipes.description
        result.recipes.thumbnailImgUrl == youtubeRecipes.thumbnailImgUrl
        result.recipes.postDate == [youtubeRecipes.get(0).postDate.format(DateTimeFormatter.ofPattern("yyyy.M.d")), youtubeRecipes.get(1).postDate.format(DateTimeFormatter.ofPattern("yyyy.M.d"))]
        result.recipes.isUserScrap == [true, false]
        result.recipes.scrapCnt == youtubeRecipes.scrapCnt
        result.recipes.viewCnt == youtubeRecipes.viewCnt
    }

    def "유튜브 레시피 검색 - 조회순"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        String keyword = "테스트"
        long lastYoutubeRecipeId = 0
        int size = 2
        String sort = "youtubeViews"

        youtubeRecipeRepository.countByKeyword(keyword) >> 20

        youtubeViewService.countByYoutubeRecipeId(lastYoutubeRecipeId) >> 0

        List<YoutubeRecipe> youtubeRecipes = [
                YoutubeRecipe.builder()
                        .youtubeRecipeId(1)
                        .title("제목")
                        .description("설명")
                        .thumbnailImgUrl("http://img.jpg")
                        .postDate(LocalDate.now())
                        .channelName("채널명")
                        .youtubeId("abcdef")
                        .scrapCnt(1)
                        .viewCnt(1)
                        .build(),
                YoutubeRecipe.builder()
                        .youtubeRecipeId(2)
                        .title("제목")
                        .description("설명")
                        .thumbnailImgUrl("http://img.jpg")
                        .postDate(LocalDate.now())
                        .channelName("채널명")
                        .youtubeId("abcdef")
                        .scrapCnt(1)
                        .viewCnt(1)
                        .build()
        ]

        youtubeRecipeRepository.findByKeywordLimitOrderByYoutubeViewCntDesc(keyword, lastYoutubeRecipeId, 0, size) >> youtubeRecipes

        List<YoutubeScrap> youtubeScraps = [
                YoutubeScrap.builder()
                        .youtubeScrapId(1)
                        .userId(user.userId)
                        .youtubeRecipeId(youtubeRecipes.get(0).youtubeRecipeId)
                        .build()
        ]

        youtubeScrapService.findByYoutubeRecipeIds(youtubeRecipes.youtubeRecipeId) >> youtubeScraps

        when:
        RecipesResponse result = youtubeRecipeService.findYoutubeRecipesByKeyword(user, keyword, lastYoutubeRecipeId, size, sort)

        then:
        result.totalCnt == 20
        result.recipes.recipeId == youtubeRecipes.youtubeRecipeId
        result.recipes.recipeName == youtubeRecipes.title
        result.recipes.introduction == youtubeRecipes.description
        result.recipes.thumbnailImgUrl == youtubeRecipes.thumbnailImgUrl
        result.recipes.postDate == [youtubeRecipes.get(0).postDate.format(DateTimeFormatter.ofPattern("yyyy.M.d")), youtubeRecipes.get(1).postDate.format(DateTimeFormatter.ofPattern("yyyy.M.d"))]
        result.recipes.isUserScrap == [true, false]
        result.recipes.scrapCnt == youtubeRecipes.scrapCnt
        result.recipes.viewCnt == youtubeRecipes.viewCnt
    }

    def "유튜브 레시피 검색 - 최신순"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        String keyword = "테스트"
        long lastYoutubeRecipeId = 0
        int size = 2
        String sort = "newest"

        youtubeRecipeRepository.countByKeyword(keyword) >> 20

        youtubeViewService.countByYoutubeRecipeId(lastYoutubeRecipeId) >> 0

        List<YoutubeRecipe> youtubeRecipes = [
                YoutubeRecipe.builder()
                        .youtubeRecipeId(1)
                        .title("제목")
                        .description("설명")
                        .thumbnailImgUrl("http://img.jpg")
                        .postDate(LocalDate.now())
                        .channelName("채널명")
                        .youtubeId("abcdef")
                        .scrapCnt(1)
                        .viewCnt(1)
                        .build(),
                YoutubeRecipe.builder()
                        .youtubeRecipeId(2)
                        .title("제목")
                        .description("설명")
                        .thumbnailImgUrl("http://img.jpg")
                        .postDate(LocalDate.now())
                        .channelName("채널명")
                        .youtubeId("abcdef")
                        .scrapCnt(1)
                        .viewCnt(1)
                        .build()
        ]

        youtubeRecipeRepository.findById(lastYoutubeRecipeId) >> Optional.empty()

        youtubeRecipeRepository.findByKeywordLimitOrderByPostDateDesc(keyword, lastYoutubeRecipeId, null, size) >> youtubeRecipes

        List<YoutubeScrap> youtubeScraps = [
                YoutubeScrap.builder()
                        .youtubeScrapId(1)
                        .userId(user.userId)
                        .youtubeRecipeId(youtubeRecipes.get(0).youtubeRecipeId)
                        .build()
        ]

        youtubeScrapService.findByYoutubeRecipeIds(youtubeRecipes.youtubeRecipeId) >> youtubeScraps

        when:
        RecipesResponse result = youtubeRecipeService.findYoutubeRecipesByKeyword(user, keyword, lastYoutubeRecipeId, size, sort)

        then:
        result.totalCnt == 20
        result.recipes.recipeId == youtubeRecipes.youtubeRecipeId
        result.recipes.recipeName == youtubeRecipes.title
        result.recipes.introduction == youtubeRecipes.description
        result.recipes.thumbnailImgUrl == youtubeRecipes.thumbnailImgUrl
        result.recipes.postDate == [youtubeRecipes.get(0).postDate.format(DateTimeFormatter.ofPattern("yyyy.M.d")), youtubeRecipes.get(1).postDate.format(DateTimeFormatter.ofPattern("yyyy.M.d"))]
        result.recipes.isUserScrap == [true, false]
        result.recipes.scrapCnt == youtubeRecipes.scrapCnt
        result.recipes.viewCnt == youtubeRecipes.viewCnt
    }

    def "유튜브 레시피 검색 - 외부 API 요청"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        String keyword = "테스트"
        long lastYoutubeRecipeId = 0
        int size = 2
        String sort = "newest"

        youtubeRecipeRepository.countByKeyword(keyword) >> 5

        List<YoutubeRecipe> youtubeRecipes = [
                YoutubeRecipe.builder()
                        .youtubeRecipeId(1)
                        .title("제목")
                        .description("설명")
                        .thumbnailImgUrl("http://img.jpg")
                        .postDate(LocalDate.now())
                        .channelName("채널명")
                        .youtubeId("abcdef")
                        .scrapCnt(1)
                        .viewCnt(1)
                        .build(),
                YoutubeRecipe.builder()
                        .youtubeRecipeId(2)
                        .title("제목")
                        .description("설명")
                        .thumbnailImgUrl("http://img.jpg")
                        .postDate(LocalDate.now())
                        .channelName("채널명")
                        .youtubeId("abcdef")
                        .scrapCnt(1)
                        .viewCnt(1)
                        .build()
        ]

        youtubeRecipeClientSearchService.searchYoutube(keyword, size) >> youtubeRecipes

        List<YoutubeScrap> youtubeScraps = [
                YoutubeScrap.builder()
                        .youtubeScrapId(1)
                        .userId(user.userId)
                        .youtubeRecipeId(youtubeRecipes.get(0).youtubeRecipeId)
                        .build()
        ]

        youtubeScrapService.findByYoutubeRecipeIds(youtubeRecipes.youtubeRecipeId) >> youtubeScraps

        when:
        RecipesResponse result = youtubeRecipeService.findYoutubeRecipesByKeyword(user, keyword, lastYoutubeRecipeId, size, sort)

        then:
        result.totalCnt == 5
        result.recipes.recipeId == youtubeRecipes.youtubeRecipeId
        result.recipes.recipeName == youtubeRecipes.title
        result.recipes.introduction == youtubeRecipes.description
        result.recipes.thumbnailImgUrl == youtubeRecipes.thumbnailImgUrl
        result.recipes.postDate == [youtubeRecipes.get(0).postDate.format(DateTimeFormatter.ofPattern("yyyy.M.d")), youtubeRecipes.get(1).postDate.format(DateTimeFormatter.ofPattern("yyyy.M.d"))]
        result.recipes.isUserScrap == [true, false]
        result.recipes.scrapCnt == youtubeRecipes.scrapCnt
        result.recipes.viewCnt == youtubeRecipes.viewCnt
    }

    def "스크랩한 유튜브 레시피 목록 조회"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        long lastYoutubeRecipeId = 0
        int size = 2

        youtubeScrapService.countByUserId(user.userId) >> 5

        youtubeScrapService.findByUserIdAndYoutubeRecipeId(user.userId, lastYoutubeRecipeId) >> null

        List<YoutubeRecipe> youtubeRecipes = [
                YoutubeRecipe.builder()
                        .youtubeRecipeId(1)
                        .title("제목")
                        .description("설명")
                        .thumbnailImgUrl("http://img.jpg")
                        .postDate(LocalDate.now())
                        .channelName("채널명")
                        .youtubeId("abcdef")
                        .scrapCnt(1)
                        .viewCnt(1)
                        .build(),
                YoutubeRecipe.builder()
                        .youtubeRecipeId(2)
                        .title("제목")
                        .description("설명")
                        .thumbnailImgUrl("http://img.jpg")
                        .postDate(LocalDate.now())
                        .channelName("채널명")
                        .youtubeId("abcdef")
                        .scrapCnt(1)
                        .viewCnt(1)
                        .build()
        ]

        youtubeRecipeRepository.findUserScrapYoutubeRecipesLimit(user.userId, lastYoutubeRecipeId, null, size) >> youtubeRecipes

        List<YoutubeScrap> youtubeScraps = [
                YoutubeScrap.builder()
                        .youtubeScrapId(1)
                        .userId(user.userId)
                        .youtubeRecipeId(youtubeRecipes.get(0).youtubeRecipeId)
                        .build()
        ]

        youtubeScrapService.findByYoutubeRecipeIds(youtubeRecipes.youtubeRecipeId) >> youtubeScraps

        when:
        RecipesResponse result = youtubeRecipeService.findScrapYoutubeRecipes(user, lastYoutubeRecipeId, size)

        then:
        result.totalCnt == 5
        result.recipes.recipeId == youtubeRecipes.youtubeRecipeId
        result.recipes.recipeName == youtubeRecipes.title
        result.recipes.introduction == youtubeRecipes.description
        result.recipes.thumbnailImgUrl == youtubeRecipes.thumbnailImgUrl
        result.recipes.postDate == [youtubeRecipes.get(0).postDate.format(DateTimeFormatter.ofPattern("yyyy.M.d")), youtubeRecipes.get(1).postDate.format(DateTimeFormatter.ofPattern("yyyy.M.d"))]
        result.recipes.isUserScrap == [true, false]
        result.recipes.scrapCnt == youtubeRecipes.scrapCnt
        result.recipes.viewCnt == youtubeRecipes.viewCnt
    }

    def "유튜브 스크랩 정보 생성"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

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

        youtubeRecipeRepository.findById(youtubeRecipe.youtubeRecipeId) >> Optional.of(youtubeRecipe)

        when:
        youtubeRecipeService.createYoutubeScrap(user, youtubeRecipe.youtubeRecipeId)

        then:
        1 * youtubeScrapService.create(user.userId, youtubeRecipe.youtubeRecipeId)
        youtubeRecipe.scrapCnt == 2
    }

    def "유튜브 스크랩 정보 생성 시 유튜브 레시피 존재하지 않으면 실행 안함"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

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

        youtubeRecipeRepository.findById(youtubeRecipe.youtubeRecipeId) >> Optional.empty()

        when:
        youtubeRecipeService.createYoutubeScrap(user, youtubeRecipe.youtubeRecipeId)

        then:
        0 * youtubeScrapService.create(user.userId, youtubeRecipe.youtubeRecipeId)
    }

    def "유튜브 스크랩 정보 삭제"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

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

        youtubeRecipeRepository.findById(youtubeRecipe.youtubeRecipeId) >> Optional.of(youtubeRecipe)

        when:
        youtubeRecipeService.deleteYoutubeScrap(user, youtubeRecipe.youtubeRecipeId)

        then:
        1 * youtubeScrapService.delete(user.userId, youtubeRecipe.youtubeRecipeId)
        youtubeRecipe.scrapCnt == 0
    }

    def "유튜브 스크랩 정보 삭제 시 유튜브 레시피 존재하지 않으면 실행 안함"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

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

        youtubeRecipeRepository.findById(youtubeRecipe.youtubeRecipeId) >> Optional.empty()

        when:
        youtubeRecipeService.deleteYoutubeScrap(user, youtubeRecipe.youtubeRecipeId)

        then:
        0 * youtubeScrapService.delete(user.userId, youtubeRecipe.youtubeRecipeId)
    }

    def "유튜브 조회 정보 생성"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

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

        youtubeRecipeRepository.findById(youtubeRecipe.youtubeRecipeId) >> Optional.of(youtubeRecipe)

        when:
        youtubeRecipeService.createYoutubeView(user, youtubeRecipe.youtubeRecipeId)

        then:
        1 * youtubeViewService.create(user.userId, youtubeRecipe.youtubeRecipeId)
        youtubeRecipe.viewCnt == 2
    }

    def "유튜브 조회 정보 생성 시 유튜브 레시피 존재하지 않으면 실행 안함"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

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

        youtubeRecipeRepository.findById(youtubeRecipe.youtubeRecipeId) >> Optional.empty()

        when:
        youtubeRecipeService.createYoutubeView(user, youtubeRecipe.youtubeRecipeId)

        then:
        0 * youtubeViewService.create(user.userId, youtubeRecipe.youtubeRecipeId)
    }
}
