package com.recipe.app.src.recipe.infra.blog

import com.recipe.app.src.common.utils.SearchKeywordNormalizer
import com.recipe.app.src.recipe.domain.blog.BlogRecipe
import com.recipe.app.src.recipe.domain.blog.BlogScrap
import com.recipe.app.src.user.domain.User
import com.recipe.app.src.user.infra.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.cloud.openfeign.FeignAutoConfiguration
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.support.TransactionTemplate
import spock.lang.Specification

import java.time.LocalDate

// InnoDB FULLTEXT 가시성 한계: 같은 트랜잭션 안에서 INSERT 한 row 는
// MATCH AGAINST 결과로 잡히지 않음 (FT 캐시 → 커밋 시 인덱스로 머지).
// @DataJpaTest 디폴트 ROLLBACK 트랜잭션을 우회하려고 INSERT 는 REQUIRES_NEW 로 별도 커밋,
// cleanup 에서 같은 방식으로 정리.
@ActiveProfiles("test")
@DataJpaTest
@ImportAutoConfiguration(classes = FeignAutoConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class BlogRecipeCustomRepositoryTest extends Specification {

    @Autowired
    UserRepository userRepository;
    @Autowired
    BlogScrapRepository blogScrapRepository;
    @Autowired
    BlogRecipeRepository blogRecipeRepository;
    @Autowired
    PlatformTransactionManager transactionManager;

    private TransactionTemplate committedTx;

    void setup() {
        committedTx = new TransactionTemplate(transactionManager)
        committedTx.propagationBehavior = TransactionDefinition.PROPAGATION_REQUIRES_NEW
    }

    void cleanup() {
        committedTx.executeWithoutResult { status ->
            blogScrapRepository.deleteAll()
            blogRecipeRepository.deleteAll()
            userRepository.deleteAll()
        }
    }

    def "검색어로 블로그 레시피 갯수 조회"() {

        given:
        List<BlogRecipe> blogRecipes = [
                BlogRecipe.builder()
                        .title("테스트제목")
                        .description("설명")
                        .publishedAt(LocalDate.of(2024, 1, 1))
                        .blogUrl("http://naver.com")
                        .blogThumbnailImgUrl("http://test.jpg")
                        .blogName("테스트")
                        .build(),
                BlogRecipe.builder()
                        .title("제목")
                        .description("테스트설명")
                        .publishedAt(LocalDate.of(2024, 1, 1))
                        .blogUrl("http://naver.com")
                        .blogThumbnailImgUrl("http://test.jpg")
                        .blogName("테스트")
                        .build(),
                BlogRecipe.builder()
                        .title("제목")
                        .description("설명")
                        .publishedAt(LocalDate.of(2024, 1, 1))
                        .blogUrl("http://naver.com")
                        .blogThumbnailImgUrl("http://test.jpg")
                        .blogName("테스트")
                        .build()
        ]
        committedTx.executeWithoutResult { status -> blogRecipeRepository.saveAll(blogRecipes) }

        when:
        long response = blogRecipeRepository.countByKeyword(SearchKeywordNormalizer.normalize("테스트"));

        then:
        response == 2
    }

    def "검색어로 블로그 레시피 목록 조회 - 최신 게시 날짜 순 정렬"() {

        given:
        List<BlogRecipe> blogRecipes = [
                BlogRecipe.builder()
                        .title("테스트제목1")
                        .description("설명1")
                        .publishedAt(LocalDate.of(2024, 1, 1))
                        .blogUrl("http://naver.com")
                        .blogThumbnailImgUrl("http://test.jpg")
                        .blogName("테스트")
                        .build(),
                BlogRecipe.builder()
                        .title("제목2")
                        .description("테스트설명2")
                        .publishedAt(LocalDate.of(2024, 1, 5))
                        .blogUrl("http://naver.com")
                        .blogThumbnailImgUrl("http://test.jpg")
                        .blogName("테스트")
                        .build(),
                BlogRecipe.builder()
                        .title("제목3")
                        .description("설명3")
                        .publishedAt(LocalDate.of(2024, 1, 3))
                        .blogUrl("http://naver.com")
                        .blogThumbnailImgUrl("http://test.jpg")
                        .blogName("테스트")
                        .build(),
                BlogRecipe.builder()
                        .title("테스트제목3")
                        .description("테스트설명3")
                        .publishedAt(LocalDate.of(2024, 1, 3))
                        .blogUrl("http://naver.com")
                        .blogThumbnailImgUrl("http://test.jpg")
                        .blogName("테스트")
                        .build()
        ]
        committedTx.executeWithoutResult { status -> blogRecipeRepository.saveAll(blogRecipes) }

        when:
        BlogRecipe lastBlogRecipe = blogRecipes.get(1);
        List<BlogRecipe> response = blogRecipeRepository.findByKeywordLimitOrderByPublishedAtDesc(SearchKeywordNormalizer.normalize("테스트"), lastBlogRecipe.getBlogRecipeId(), lastBlogRecipe.getPublishedAt(), 3);

        then:
        response.size() == 2
        response.get(0).getBlogRecipeId() == blogRecipes.get(3).getBlogRecipeId()
        response.get(1).getBlogRecipeId() == blogRecipes.get(0).getBlogRecipeId()
    }

    def "검색어로 블로그 레시피 목록 조회 - 스크랩 수 많은 순 정렬"() {

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
                User.builder()
                        .socialId("naver_3")
                        .nickname("테스터3")
                        .build(),
        ]
        committedTx.executeWithoutResult { status -> userRepository.saveAll(users) }

        List<BlogRecipe> blogRecipes = [
                BlogRecipe.builder()
                        .title("테스트제목1")
                        .description("설명1")
                        .publishedAt(LocalDate.of(2024, 1, 1))
                        .blogUrl("http://naver.com")
                        .blogThumbnailImgUrl("http://test.jpg")
                        .blogName("테스트")
                        .scrapCnt(0L)
                        .build(),
                BlogRecipe.builder()
                        .title("제목2")
                        .description("테스트설명2")
                        .publishedAt(LocalDate.of(2024, 1, 5))
                        .blogUrl("http://naver.com")
                        .blogThumbnailImgUrl("http://test.jpg")
                        .blogName("테스트")
                        .scrapCnt(2L)
                        .build(),
                BlogRecipe.builder()
                        .title("제목3")
                        .description("설명3")
                        .publishedAt(LocalDate.of(2024, 1, 3))
                        .blogUrl("http://naver.com")
                        .blogThumbnailImgUrl("http://test.jpg")
                        .blogName("테스트")
                        .scrapCnt(1L)
                        .build(),
                BlogRecipe.builder()
                        .title("테스트제목3")
                        .description("테스트설명3")
                        .publishedAt(LocalDate.of(2024, 1, 3))
                        .blogUrl("http://naver.com")
                        .blogThumbnailImgUrl("http://test.jpg")
                        .blogName("테스트")
                        .scrapCnt(3L)
                        .build()
        ]
        committedTx.executeWithoutResult { status -> blogRecipeRepository.saveAll(blogRecipes) }

        when:
        List<BlogRecipe> response = blogRecipeRepository.findByKeywordLimitOrderByBlogScrapCntDesc(SearchKeywordNormalizer.normalize("테스트"), blogRecipes.get(3).blogRecipeId, 3, 3);

        then:
        response.size() == 2
        response.get(0).blogRecipeId == blogRecipes.get(1).blogRecipeId
        response.get(1).blogRecipeId == blogRecipes.get(0).blogRecipeId
    }

    def "검색어로 블로그 레시피 목록 조회 - 조회 수 많은 순 정렬"() {

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
        committedTx.executeWithoutResult { status -> userRepository.saveAll(users) }

        List<BlogRecipe> blogRecipes = [
                BlogRecipe.builder()
                        .title("테스트제목1")
                        .description("설명1")
                        .publishedAt(LocalDate.of(2024, 1, 1))
                        .blogUrl("http://naver.com")
                        .blogThumbnailImgUrl("http://test.jpg")
                        .blogName("테스트")
                        .viewCnt(2L)
                        .build(),
                BlogRecipe.builder()
                        .title("제목2")
                        .description("테스트설명2")
                        .publishedAt(LocalDate.of(2024, 1, 5))
                        .blogUrl("http://naver.com")
                        .blogThumbnailImgUrl("http://test.jpg")
                        .blogName("테스트")
                        .viewCnt(0L)
                        .build(),
                BlogRecipe.builder()
                        .title("제목3")
                        .description("설명3")
                        .publishedAt(LocalDate.of(2024, 1, 3))
                        .blogUrl("http://naver.com")
                        .blogThumbnailImgUrl("http://test.jpg")
                        .blogName("테스트")
                        .viewCnt(0L)
                        .build(),
                BlogRecipe.builder()
                        .title("테스트제목3")
                        .description("테스트설명3")
                        .publishedAt(LocalDate.of(2024, 1, 3))
                        .blogUrl("http://naver.com")
                        .blogThumbnailImgUrl("http://test.jpg")
                        .blogName("테스트")
                        .viewCnt(2L)
                        .build()
        ]
        committedTx.executeWithoutResult { status -> blogRecipeRepository.saveAll(blogRecipes) }

        when:
        List<BlogRecipe> response = blogRecipeRepository.findByKeywordLimitOrderByBlogViewCntDesc(SearchKeywordNormalizer.normalize("테스트"), blogRecipes.get(3).blogRecipeId, 2, 3);

        then:
        response.size() == 2
        response.get(0).blogRecipeId == blogRecipes.get(0).blogRecipeId
        response.get(1).blogRecipeId == blogRecipes.get(1).blogRecipeId
    }

    def "유저가 스크랩한 블로그 레시피 목록 조회"() {

        given:
        User user = User.builder()
                .socialId("naver_1")
                .nickname("테스터1")
                .build();
        committedTx.executeWithoutResult { status -> userRepository.save(user) }

        List<BlogRecipe> blogRecipes = [
                BlogRecipe.builder()
                        .title("테스트제목1")
                        .description("설명1")
                        .publishedAt(LocalDate.of(2024, 1, 1))
                        .blogUrl("http://naver.com")
                        .blogThumbnailImgUrl("http://test.jpg")
                        .blogName("테스트")
                        .build(),
                BlogRecipe.builder()
                        .title("제목2")
                        .description("테스트설명2")
                        .publishedAt(LocalDate.of(2024, 1, 5))
                        .blogUrl("http://naver.com")
                        .blogThumbnailImgUrl("http://test.jpg")
                        .blogName("테스트")
                        .build(),
                BlogRecipe.builder()
                        .title("제목3")
                        .description("설명3")
                        .publishedAt(LocalDate.of(2024, 1, 3))
                        .blogUrl("http://naver.com")
                        .blogThumbnailImgUrl("http://test.jpg")
                        .blogName("테스트")
                        .build(),
                BlogRecipe.builder()
                        .title("테스트제목3")
                        .description("테스트설명3")
                        .publishedAt(LocalDate.of(2024, 1, 3))
                        .blogUrl("http://naver.com")
                        .blogThumbnailImgUrl("http://test.jpg")
                        .blogName("테스트")
                        .build()
        ]
        committedTx.executeWithoutResult { status -> blogRecipeRepository.saveAll(blogRecipes) }

        List<BlogScrap> blogScraps = [
                BlogScrap.builder()
                        .userId(user.userId)
                        .blogRecipeId(blogRecipes.get(0).blogRecipeId)
                        .build(),
                BlogScrap.builder()
                        .userId(user.userId)
                        .blogRecipeId(blogRecipes.get(3).blogRecipeId)
                        .build(),
        ]
        committedTx.executeWithoutResult { status -> blogScrapRepository.saveAll(blogScraps) }

        when:
        List<BlogRecipe> response = blogRecipeRepository.findUserScrapBlogRecipesLimit(user.userId, 0L, blogScraps.get(0).createdAt.plusHours(1), 3)

        then:
        response.size() == 2
        response.get(0).blogRecipeId == blogRecipes.get(3).blogRecipeId
        response.get(1).blogRecipeId == blogRecipes.get(0).blogRecipeId
    }

}
