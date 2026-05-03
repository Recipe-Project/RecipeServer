package com.recipe.app.src.recipe.infra.youtube

import com.recipe.app.src.common.utils.SearchKeywordNormalizer
import com.recipe.app.src.common.utils.SearchKeywordNormalizer.SearchQuery
import com.recipe.app.src.recipe.domain.youtube.YoutubeRecipe
import com.recipe.app.src.recipe.domain.youtube.YoutubeScrap
import com.recipe.app.src.user.domain.User
import com.recipe.app.src.user.infra.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.cloud.openfeign.FeignAutoConfiguration
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import spock.lang.Specification

import java.time.LocalDate

@ActiveProfiles("test")
@DataJpaTest
@ImportAutoConfiguration(classes = FeignAutoConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class YoutubeRecipeCustomRepositoryTest extends Specification {

    @Autowired
    UserRepository userRepository;
    @Autowired
    YoutubeRecipeRepository youtubeRecipeRepository;
    @Autowired
    YoutubeScrapRepository youtubeScrapRepository;

    def "검색어로 유튜브 레시피 갯수 조회"() {

        given:
        List<YoutubeRecipe> youtubeRecipes = [
                YoutubeRecipe.builder()
                        .title("테스트제목")
                        .description("테스트설명")
                        .postDate(LocalDate.of(2024, 1, 1))
                        .channelName("테스트")
                        .youtubeId("youtube1")
                        .thumbnailImgUrl("http://test.jpg")
                        .build(),
                YoutubeRecipe.builder()
                        .title("테스트제목")
                        .description("테스트")
                        .postDate(LocalDate.of(2024, 1, 1))
                        .channelName("테스트")
                        .youtubeId("youtube2")
                        .thumbnailImgUrl("http://test.jpg")
                        .build(),
                YoutubeRecipe.builder()
                        .title("제목")
                        .description("설명")
                        .postDate(LocalDate.of(2024, 1, 1))
                        .channelName("테스트")
                        .youtubeId("youtube3")
                        .thumbnailImgUrl("http://test.jpg")
                        .build(),
                YoutubeRecipe.builder()
                        .title("제목")
                        .description("테스트설명")
                        .postDate(LocalDate.of(2024, 1, 1))
                        .channelName("테스트")
                        .youtubeId("youtube4")
                        .thumbnailImgUrl("http://test.jpg")
                        .build(),
        ]
        youtubeRecipeRepository.saveAll(youtubeRecipes);

        when:
        long response = youtubeRecipeRepository.countByKeyword(SearchKeywordNormalizer.normalize("테스트"));

        then:
        response == 3
    }

    def "검색어로 유튜브 레시피 목록 조회 - 최신 게시 날짜 순 정렬"() {

        given:
        List<YoutubeRecipe> youtubeRecipes = [
                YoutubeRecipe.builder()
                        .title("테스트제목")
                        .description("테스트설명")
                        .postDate(LocalDate.of(2024, 1, 3))
                        .channelName("테스트")
                        .youtubeId("youtube1")
                        .thumbnailImgUrl("http://test.jpg")
                        .build(),
                YoutubeRecipe.builder()
                        .title("테스트제목")
                        .description("테스트")
                        .postDate(LocalDate.of(2024, 1, 5))
                        .channelName("테스트")
                        .youtubeId("youtube2")
                        .thumbnailImgUrl("http://test.jpg")
                        .build(),
                YoutubeRecipe.builder()
                        .title("제목")
                        .description("설명")
                        .postDate(LocalDate.of(2024, 1, 1))
                        .channelName("테스트")
                        .youtubeId("youtube3")
                        .thumbnailImgUrl("http://test.jpg")
                        .build(),
                YoutubeRecipe.builder()
                        .title("제목")
                        .description("테스트설명")
                        .postDate(LocalDate.of(2024, 1, 1))
                        .channelName("테스트")
                        .youtubeId("youtube4")
                        .thumbnailImgUrl("http://test.jpg")
                        .build(),
        ]
        youtubeRecipeRepository.saveAll(youtubeRecipes);

        when:
        YoutubeRecipe lastYoutubeRecipe = youtubeRecipes.get(1);
        List<YoutubeRecipe> response = youtubeRecipeRepository.findByKeywordLimitOrderByPostDateDesc(SearchKeywordNormalizer.normalize("테스트"), lastYoutubeRecipe.youtubeRecipeId, lastYoutubeRecipe.postDate, 3);

        then:
        response.size() == 2
        response.get(0).youtubeRecipeId == youtubeRecipes.get(0).youtubeRecipeId
        response.get(1).youtubeRecipeId == youtubeRecipes.get(3).youtubeRecipeId
    }

    def "검색어로 유튜브 레시피 목록 조회 - 스크랩 수 많은 순 정렬"() {

        given:
        List<User> users = [
                User.builder()
                        .socialId("naver_1")
                        .nickname("테스터1")
                        .build(),
                User.builder()
                        .socialId("naver_2")
                        .nickname("테스터2")
                        .build(),
        ]
        userRepository.saveAll(users)

        List<YoutubeRecipe> youtubeRecipes = [
                YoutubeRecipe.builder()
                        .title("테스트제목")
                        .description("테스트설명")
                        .postDate(LocalDate.of(2024, 1, 3))
                        .channelName("테스트")
                        .youtubeId("youtube1")
                        .thumbnailImgUrl("http://test.jpg")
                        .scrapCnt(0L)
                        .build(),
                YoutubeRecipe.builder()
                        .title("테스트제목")
                        .description("테스트")
                        .postDate(LocalDate.of(2024, 1, 5))
                        .channelName("테스트")
                        .youtubeId("youtube2")
                        .thumbnailImgUrl("http://test.jpg")
                        .scrapCnt(2L)
                        .build(),
                YoutubeRecipe.builder()
                        .title("제목")
                        .description("설명")
                        .postDate(LocalDate.of(2024, 1, 1))
                        .channelName("테스트")
                        .youtubeId("youtube3")
                        .thumbnailImgUrl("http://test.jpg")
                        .scrapCnt(1L)
                        .build(),
                YoutubeRecipe.builder()
                        .title("제목")
                        .description("테스트설명")
                        .postDate(LocalDate.of(2024, 1, 1))
                        .channelName("테스트")
                        .youtubeId("youtube4")
                        .thumbnailImgUrl("http://test.jpg")
                        .scrapCnt(2L)
                        .build(),
        ]
        youtubeRecipeRepository.saveAll(youtubeRecipes);

        when:
        List<YoutubeRecipe> response = youtubeRecipeRepository.findByKeywordLimitOrderByYoutubeScrapCntDesc(SearchKeywordNormalizer.normalize("테스트"), youtubeRecipes.get(3).youtubeRecipeId, 2, 3);

        then:
        response.size() == 2
        response.get(0).youtubeRecipeId == youtubeRecipes.get(1).youtubeRecipeId
        response.get(1).youtubeRecipeId == youtubeRecipes.get(0).youtubeRecipeId
    }

    def "검색어로 유튜브 레시피 목록 조회 - 조회 수 많은 순 정렬"() {

        given:
        List<User> users = [
                User.builder()
                        .socialId("naver_1")
                        .nickname("테스터1")
                        .build(),
                User.builder()
                        .socialId("naver_2")
                        .nickname("테스터2")
                        .build(),
        ]
        userRepository.saveAll(users)

        List<YoutubeRecipe> youtubeRecipes = [
                YoutubeRecipe.builder()
                        .title("테스트제목")
                        .description("테스트설명")
                        .postDate(LocalDate.of(2024, 1, 3))
                        .channelName("테스트")
                        .youtubeId("youtube1")
                        .thumbnailImgUrl("http://test.jpg")
                        .viewCnt(0L)
                        .build(),
                YoutubeRecipe.builder()
                        .title("테스트제목")
                        .description("테스트")
                        .postDate(LocalDate.of(2024, 1, 5))
                        .channelName("테스트")
                        .youtubeId("youtube2")
                        .thumbnailImgUrl("http://test.jpg")
                        .viewCnt(2L)
                        .build(),
                YoutubeRecipe.builder()
                        .title("제목")
                        .description("설명")
                        .postDate(LocalDate.of(2024, 1, 1))
                        .channelName("테스트")
                        .youtubeId("youtube3")
                        .thumbnailImgUrl("http://test.jpg")
                        .viewCnt(1L)
                        .build(),
                YoutubeRecipe.builder()
                        .title("제목")
                        .description("테스트설명")
                        .postDate(LocalDate.of(2024, 1, 1))
                        .channelName("테스트")
                        .youtubeId("youtube4")
                        .thumbnailImgUrl("http://test.jpg")
                        .viewCnt(2L)
                        .build(),
        ]
        youtubeRecipeRepository.saveAll(youtubeRecipes);

        when:
        List<YoutubeRecipe> response = youtubeRecipeRepository.findByKeywordLimitOrderByYoutubeViewCntDesc(SearchKeywordNormalizer.normalize("테스트"), youtubeRecipes.get(3).youtubeRecipeId, 2, 3)

        then:
        response.size() == 2
        response.get(0).youtubeRecipeId == youtubeRecipes.get(1).youtubeRecipeId
        response.get(1).youtubeRecipeId == youtubeRecipes.get(0).youtubeRecipeId
    }

    def "유저가 스크랩한 레시피 목록 조회"() {

        given:
        User user = User.builder()
                .socialId("naver_1")
                .nickname("테스터1")
                .build();
        userRepository.save(user);

        List<YoutubeRecipe> youtubeRecipes = [
                YoutubeRecipe.builder()
                        .title("테스트제목")
                        .description("테스트설명")
                        .postDate(LocalDate.of(2024, 1, 3))
                        .channelName("테스트")
                        .youtubeId("youtube1")
                        .thumbnailImgUrl("http://test.jpg")
                        .build(),
                YoutubeRecipe.builder()
                        .title("테스트제목")
                        .description("테스트")
                        .postDate(LocalDate.of(2024, 1, 5))
                        .channelName("테스트")
                        .youtubeId("youtube2")
                        .thumbnailImgUrl("http://test.jpg")
                        .build(),
                YoutubeRecipe.builder()
                        .title("제목")
                        .description("설명")
                        .postDate(LocalDate.of(2024, 1, 1))
                        .channelName("테스트")
                        .youtubeId("youtube3")
                        .thumbnailImgUrl("http://test.jpg")
                        .build(),
                YoutubeRecipe.builder()
                        .title("제목")
                        .description("테스트설명")
                        .postDate(LocalDate.of(2024, 1, 1))
                        .channelName("테스트")
                        .youtubeId("youtube4")
                        .thumbnailImgUrl("http://test.jpg")
                        .build(),
        ]
        youtubeRecipeRepository.saveAll(youtubeRecipes);

        List<YoutubeScrap> youtubeScraps = [
                YoutubeScrap.builder()
                        .userId(user.userId)
                        .youtubeRecipeId(youtubeRecipes.get(0).youtubeRecipeId)
                        .build(),
                YoutubeScrap.builder()
                        .userId(user.userId)
                        .youtubeRecipeId(youtubeRecipes.get(2).youtubeRecipeId)
                        .build(),
                YoutubeScrap.builder()
                        .userId(user.userId)
                        .youtubeRecipeId(youtubeRecipes.get(3).youtubeRecipeId)
                        .build(),
        ]
        youtubeScrapRepository.saveAll(youtubeScraps);

        when:
        List<YoutubeRecipe> response = youtubeRecipeRepository.findUserScrapYoutubeRecipesLimit(user.userId, 0L, youtubeScraps.get(0).createdAt.plusDays(1), 3);

        then:
        response.size() == 3
        response.get(0).youtubeRecipeId == youtubeRecipes.get(3).youtubeRecipeId
        response.get(1).youtubeRecipeId == youtubeRecipes.get(2).youtubeRecipeId
        response.get(2).youtubeRecipeId == youtubeRecipes.get(0).youtubeRecipeId
    }

    def "1글자 ExactToken 검색은 단어 경계 정확 매칭만 매치한다"() {

        given:
        List<YoutubeRecipe> youtubeRecipes = [
                YoutubeRecipe.builder()
                        .title("갓")
                        .description("재료 갓 설명")
                        .postDate(LocalDate.of(2024, 1, 1))
                        .channelName("테스트")
                        .youtubeId("yt-exact-1")
                        .thumbnailImgUrl("http://test.jpg")
                        .build(),
                YoutubeRecipe.builder()
                        .title("감자")
                        .description("감자 요리")
                        .postDate(LocalDate.of(2024, 1, 1))
                        .channelName("테스트")
                        .youtubeId("yt-exact-2")
                        .thumbnailImgUrl("http://test.jpg")
                        .build(),
        ]
        youtubeRecipeRepository.saveAll(youtubeRecipes)

        when: "1글자 정확 매칭"
        SearchQuery query = SearchKeywordNormalizer.normalize(input)
        long count = youtubeRecipeRepository.countByKeyword(query)

        then:
        query instanceof SearchQuery.ExactToken
        count == expected

        where:
        input || expected
        "갓"   || 1L  // title="갓" 인 row 만 매치
        "자"   || 0L  // 어떤 토큰도 정확히 '자' 가 아님
    }
}
