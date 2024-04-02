package com.recipe.app.src.recipe.infra.youtube

import com.recipe.app.src.recipe.domain.youtube.YoutubeRecipe
import com.recipe.app.src.recipe.domain.youtube.YoutubeScrap
import com.recipe.app.src.recipe.domain.youtube.YoutubeView
import com.recipe.app.src.user.domain.User
import com.recipe.app.src.user.infra.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import spock.lang.Specification

import java.time.LocalDate

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class YoutubeRecipeCustomRepositoryTest extends Specification {

    @Autowired
    UserRepository userRepository;
    @Autowired
    YoutubeRecipeRepository youtubeRecipeRepository;
    @Autowired
    YoutubeScrapRepository youtubeScrapRepository;
    @Autowired
    YoutubeViewRepository youtubeViewRepository;

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
        long response = youtubeRecipeRepository.countByKeyword("테스트");

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
        List<YoutubeRecipe> response = youtubeRecipeRepository.findByKeywordLimitOrderByPostDateDesc("테스트", lastYoutubeRecipe.youtubeRecipeId, lastYoutubeRecipe.postDate, 3);

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
                        .userId(users.get(0).userId)
                        .youtubeRecipeId(youtubeRecipes.get(3).youtubeRecipeId)
                        .build(),
                YoutubeScrap.builder()
                        .userId(users.get(1).userId)
                        .youtubeRecipeId(youtubeRecipes.get(3).youtubeRecipeId)
                        .build(),
                YoutubeScrap.builder()
                        .userId(users.get(0).userId)
                        .youtubeRecipeId(youtubeRecipes.get(2).youtubeRecipeId)
                        .build(),
                YoutubeScrap.builder()
                        .userId(users.get(0).userId)
                        .youtubeRecipeId(youtubeRecipes.get(1).youtubeRecipeId)
                        .build(),
                YoutubeScrap.builder()
                        .userId(users.get(1).userId)
                        .youtubeRecipeId(youtubeRecipes.get(1).youtubeRecipeId)
                        .build(),
        ]
        youtubeScrapRepository.saveAll(youtubeScraps);

        when:
        List<YoutubeRecipe> response = youtubeRecipeRepository.findByKeywordLimitOrderByYoutubeScrapCntDesc("테스트", youtubeRecipes.get(3).youtubeRecipeId, 2, 3);

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

        List<YoutubeView> youtubeViews = [
                YoutubeView.builder()
                        .userId(users.get(0).userId)
                        .youtubeRecipeId(youtubeRecipes.get(3).youtubeRecipeId)
                        .build(),
                YoutubeView.builder()
                        .userId(users.get(1).userId)
                        .youtubeRecipeId(youtubeRecipes.get(3).youtubeRecipeId)
                        .build(),
                YoutubeView.builder()
                        .userId(users.get(0).userId)
                        .youtubeRecipeId(youtubeRecipes.get(2).youtubeRecipeId)
                        .build(),
                YoutubeView.builder()
                        .userId(users.get(0).userId)
                        .youtubeRecipeId(youtubeRecipes.get(1).youtubeRecipeId)
                        .build(),
                YoutubeView.builder()
                        .userId(users.get(0).userId)
                        .youtubeRecipeId(youtubeRecipes.get(1).youtubeRecipeId)
                        .build(),
        ]
        youtubeViewRepository.saveAll(youtubeViews);

        when:
        List<YoutubeRecipe> response = youtubeRecipeRepository.findByKeywordLimitOrderByYoutubeViewCntDesc("테스트", youtubeRecipes.get(3).youtubeRecipeId, 2, 3)

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
}
