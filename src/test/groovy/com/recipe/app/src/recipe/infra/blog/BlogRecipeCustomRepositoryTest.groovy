package com.recipe.app.src.recipe.infra.blog

import com.recipe.app.src.recipe.domain.blog.BlogRecipe
import com.recipe.app.src.recipe.domain.blog.BlogScrap
import com.recipe.app.src.recipe.domain.blog.BlogView
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
class BlogRecipeCustomRepositoryTest extends Specification {

    @Autowired
    UserRepository userRepository;
    @Autowired
    BlogScrapRepository blogScrapRepository;
    @Autowired
    BlogViewRepository blogViewRepository;
    @Autowired
    BlogRecipeRepository blogRecipeRepository;

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
        blogRecipeRepository.saveAll(blogRecipes);

        when:
        long response = blogRecipeRepository.countByKeyword("테스트");

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
        blogRecipeRepository.saveAll(blogRecipes);

        when:
        BlogRecipe lastBlogRecipe = blogRecipes.get(1);
        List<BlogRecipe> response = blogRecipeRepository.findByKeywordLimitOrderByPublishedAtDesc("테스트", lastBlogRecipe.getBlogRecipeId(), lastBlogRecipe.getPublishedAt(), 3);

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
        userRepository.saveAll(users)

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
        blogRecipeRepository.saveAll(blogRecipes);

        List<BlogScrap> blogScraps = [
                BlogScrap.builder()
                        .userId(users.get(0).userId)
                        .blogRecipeId(blogRecipes.get(3).blogRecipeId)
                        .build(),
                BlogScrap.builder()
                        .userId(users.get(1).userId)
                        .blogRecipeId(blogRecipes.get(3).blogRecipeId)
                        .build(),
                BlogScrap.builder()
                        .userId(users.get(2).userId)
                        .blogRecipeId(blogRecipes.get(3).blogRecipeId)
                        .build(),
                BlogScrap.builder()
                        .userId(users.get(0).userId)
                        .blogRecipeId(blogRecipes.get(2).blogRecipeId)
                        .build(),
                BlogScrap.builder()
                        .userId(users.get(0).userId)
                        .blogRecipeId(blogRecipes.get(1).blogRecipeId)
                        .build(),
                BlogScrap.builder()
                        .userId(users.get(1).userId)
                        .blogRecipeId(blogRecipes.get(1).blogRecipeId)
                        .build()
        ]
        blogScrapRepository.saveAll(blogScraps);

        when:
        List<BlogRecipe> response = blogRecipeRepository.findByKeywordLimitOrderByBlogScrapCntDesc("테스트", blogRecipes.get(3).blogRecipeId, 3, 3);

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
        userRepository.saveAll(users)

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
        blogRecipeRepository.saveAll(blogRecipes);

        List<BlogView> blogViews = [
                BlogView.builder()
                        .userId(users.get(0).userId)
                        .blogRecipeId(blogRecipes.get(3).blogRecipeId)
                        .build(),
                BlogView.builder()
                        .userId(users.get(1).userId)
                        .blogRecipeId(blogRecipes.get(3).blogRecipeId)
                        .build(),
                BlogView.builder()
                        .userId(users.get(0).userId)
                        .blogRecipeId(blogRecipes.get(0).blogRecipeId)
                        .build(),
                BlogView.builder()
                        .userId(users.get(1).userId)
                        .blogRecipeId(blogRecipes.get(0).blogRecipeId)
                        .build(),
        ]
        blogViewRepository.saveAll(blogViews);

        when:
        List<BlogRecipe> response = blogRecipeRepository.findByKeywordLimitOrderByBlogViewCntDesc("테스트", blogRecipes.get(3).blogRecipeId, 2, 3);

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
        userRepository.save(user);

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
        blogRecipeRepository.saveAll(blogRecipes);

        List<BlogScrap> blogScraps = [
                BlogScrap.builder()
                        .userId(user.userId)
                        .blogRecipeId(blogRecipes.get(3).blogRecipeId)
                        .build(),
                BlogScrap.builder()
                        .userId(user.userId)
                        .blogRecipeId(blogRecipes.get(0).blogRecipeId)
                        .build(),
        ]
        blogScrapRepository.saveAll(blogScraps);

        when:
        List<BlogRecipe> response = blogRecipeRepository.findUserScrapBlogRecipesLimit(user.userId, 0L, blogScraps.get(0).createdAt.plusHours(1),3)

        then:
        response.size() == 2
        response.get(0).blogRecipeId == blogRecipes.get(3).blogRecipeId
        response.get(1).blogRecipeId == blogRecipes.get(0).blogRecipeId
    }
}
