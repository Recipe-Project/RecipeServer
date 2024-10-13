package com.recipe.app.src.recipe.application.blog

import com.recipe.app.src.common.utils.BadWordFiltering
import com.recipe.app.src.recipe.application.dto.RecipesResponse
import com.recipe.app.src.recipe.domain.blog.BlogRecipe
import com.recipe.app.src.recipe.domain.blog.BlogScrap
import com.recipe.app.src.recipe.infra.blog.BlogRecipeRepository
import com.recipe.app.src.user.domain.User
import spock.lang.Specification

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class BlogRecipeServiceTest extends Specification {

    private BlogRecipeRepository blogRecipeRepository = Mock()
    private BlogScrapService blogScrapService = Mock()
    private BlogViewService blogViewService = Mock()
    private BadWordFiltering badWordService = Mock()
    private BlogRecipeClientSearchService blogRecipeClientSearchService = Mock()
    private BlogRecipeService blogRecipeService = new BlogRecipeService(blogRecipeRepository, blogScrapService, blogViewService, badWordService, blogRecipeClientSearchService)

    def "블로그 레시피 검색 - 스크랩순"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        String keyword = "테스트"
        long lastBlogRecipeId = 0
        int size = 2
        String sort = "scraps"

        blogRecipeRepository.countByKeyword(keyword) >> 20

        blogScrapService.countByBlogRecipeId(lastBlogRecipeId) >> 0

        List<BlogRecipe> blogRecipes = [
                BlogRecipe.builder()
                        .blogRecipeId(1L)
                        .blogUrl("https://naver.com")
                        .blogThumbnailImgUrl("")
                        .title("제목")
                        .description("설명")
                        .publishedAt(LocalDate.now())
                        .blogName("블로그명")
                        .scrapCnt(1)
                        .viewCnt(1)
                        .build(),
                BlogRecipe.builder()
                        .blogRecipeId(2L)
                        .blogUrl("https://naver.com")
                        .blogThumbnailImgUrl("")
                        .title("제목")
                        .description("설명")
                        .publishedAt(LocalDate.now())
                        .blogName("블로그명")
                        .scrapCnt(1)
                        .viewCnt(1)
                        .build(),
        ]

        blogRecipeRepository.findByKeywordLimitOrderByBlogScrapCntDesc(keyword, lastBlogRecipeId, 0, size) >> blogRecipes

        List<BlogScrap> blogScraps = [
                BlogScrap.builder()
                        .blogScrapId(1)
                        .userId(user.userId)
                        .blogRecipeId(blogRecipes.get(0).blogRecipeId)
                        .build()
        ]

        blogScrapService.findByBlogRecipeIds(blogRecipes.blogRecipeId) >> blogScraps

        when:
        RecipesResponse result = blogRecipeService.findBlogRecipesByKeyword(user, keyword, lastBlogRecipeId, size, sort)

        then:
        result.totalCnt == 20
        result.recipes.recipeId == blogRecipes.blogRecipeId
        result.recipes.recipeName == blogRecipes.title
        result.recipes.introduction == blogRecipes.description
        result.recipes.thumbnailImgUrl == blogRecipes.blogThumbnailImgUrl
        result.recipes.postDate == [blogRecipes.get(0).publishedAt.format(DateTimeFormatter.ofPattern("yyyy.M.d")), blogRecipes.get(1).publishedAt.format(DateTimeFormatter.ofPattern("yyyy.M.d"))]
        result.recipes.isUserScrap == [true, false]
        result.recipes.scrapCnt == blogRecipes.scrapCnt
        result.recipes.viewCnt == blogRecipes.viewCnt
    }

    def "블로그 레시피 검색 - 조회순"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        String keyword = "테스트"
        long lastBlogRecipeId = 0
        int size = 2
        String sort = "views"

        blogRecipeRepository.countByKeyword(keyword) >> 20

        blogViewService.countByBlogRecipeId(lastBlogRecipeId) >> 0

        List<BlogRecipe> blogRecipes = [
                BlogRecipe.builder()
                        .blogRecipeId(1L)
                        .blogUrl("https://naver.com")
                        .blogThumbnailImgUrl("")
                        .title("제목")
                        .description("설명")
                        .publishedAt(LocalDate.now())
                        .blogName("블로그명")
                        .scrapCnt(1)
                        .viewCnt(1)
                        .build(),
                BlogRecipe.builder()
                        .blogRecipeId(2L)
                        .blogUrl("https://naver.com")
                        .blogThumbnailImgUrl("")
                        .title("제목")
                        .description("설명")
                        .publishedAt(LocalDate.now())
                        .blogName("블로그명")
                        .scrapCnt(1)
                        .viewCnt(1)
                        .build(),
        ]

        blogRecipeRepository.findByKeywordLimitOrderByBlogViewCntDesc(keyword, lastBlogRecipeId, 0, size) >> blogRecipes

        List<BlogScrap> blogScraps = [
                BlogScrap.builder()
                        .blogScrapId(1)
                        .userId(user.userId)
                        .blogRecipeId(blogRecipes.get(0).blogRecipeId)
                        .build()
        ]

        blogScrapService.findByBlogRecipeIds(blogRecipes.blogRecipeId) >> blogScraps

        when:
        RecipesResponse result = blogRecipeService.findBlogRecipesByKeyword(user, keyword, lastBlogRecipeId, size, sort)

        then:
        result.totalCnt == 20
        result.recipes.recipeId == blogRecipes.blogRecipeId
        result.recipes.recipeName == blogRecipes.title
        result.recipes.introduction == blogRecipes.description
        result.recipes.thumbnailImgUrl == blogRecipes.blogThumbnailImgUrl
        result.recipes.postDate == [blogRecipes.get(0).publishedAt.format(DateTimeFormatter.ofPattern("yyyy.M.d")), blogRecipes.get(1).publishedAt.format(DateTimeFormatter.ofPattern("yyyy.M.d"))]
        result.recipes.isUserScrap == [true, false]
        result.recipes.scrapCnt == blogRecipes.scrapCnt
        result.recipes.viewCnt == blogRecipes.viewCnt
    }

    def "블로그 레시피 검색 - 최신순"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        String keyword = "테스트"
        long lastBlogRecipeId = 0
        int size = 2
        String sort = "newest"

        blogRecipeRepository.countByKeyword(keyword) >> 20

        List<BlogRecipe> blogRecipes = [
                BlogRecipe.builder()
                        .blogRecipeId(1L)
                        .blogUrl("https://naver.com")
                        .blogThumbnailImgUrl("")
                        .title("제목")
                        .description("설명")
                        .publishedAt(LocalDate.now())
                        .blogName("블로그명")
                        .scrapCnt(1)
                        .viewCnt(1)
                        .build(),
                BlogRecipe.builder()
                        .blogRecipeId(2L)
                        .blogUrl("https://naver.com")
                        .blogThumbnailImgUrl("")
                        .title("제목")
                        .description("설명")
                        .publishedAt(LocalDate.now())
                        .blogName("블로그명")
                        .scrapCnt(1)
                        .viewCnt(1)
                        .build(),
        ]

        blogRecipeRepository.findById(lastBlogRecipeId) >> Optional.empty()

        blogRecipeRepository.findByKeywordLimitOrderByPublishedAtDesc(keyword, lastBlogRecipeId, null, size) >> blogRecipes

        List<BlogScrap> blogScraps = [
                BlogScrap.builder()
                        .blogScrapId(1)
                        .userId(user.userId)
                        .blogRecipeId(blogRecipes.get(0).blogRecipeId)
                        .build()
        ]

        blogScrapService.findByBlogRecipeIds(blogRecipes.blogRecipeId) >> blogScraps

        when:
        RecipesResponse result = blogRecipeService.findBlogRecipesByKeyword(user, keyword, lastBlogRecipeId, size, sort)

        then:
        result.totalCnt == 20
        result.recipes.recipeId == blogRecipes.blogRecipeId
        result.recipes.recipeName == blogRecipes.title
        result.recipes.introduction == blogRecipes.description
        result.recipes.thumbnailImgUrl == blogRecipes.blogThumbnailImgUrl
        result.recipes.postDate == [blogRecipes.get(0).publishedAt.format(DateTimeFormatter.ofPattern("yyyy.M.d")), blogRecipes.get(1).publishedAt.format(DateTimeFormatter.ofPattern("yyyy.M.d"))]
        result.recipes.isUserScrap == [true, false]
        result.recipes.scrapCnt == blogRecipes.scrapCnt
        result.recipes.viewCnt == blogRecipes.viewCnt
    }

    def "블로그 레시피 검색 - 외부 API 요청"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        String keyword = "테스트"
        long lastBlogRecipeId = 0
        int size = 2
        String sort = "newest"

        blogRecipeRepository.countByKeyword(keyword) >> 5

        List<BlogRecipe> blogRecipes = [
                BlogRecipe.builder()
                        .blogRecipeId(1L)
                        .blogUrl("https://naver.com")
                        .blogThumbnailImgUrl("")
                        .title("제목")
                        .description("설명")
                        .publishedAt(LocalDate.now())
                        .blogName("블로그명")
                        .scrapCnt(1)
                        .viewCnt(1)
                        .build(),
                BlogRecipe.builder()
                        .blogRecipeId(2L)
                        .blogUrl("https://naver.com")
                        .blogThumbnailImgUrl("")
                        .title("제목")
                        .description("설명")
                        .publishedAt(LocalDate.now())
                        .blogName("블로그명")
                        .scrapCnt(1)
                        .viewCnt(1)
                        .build(),
        ]

        blogRecipeClientSearchService.searchNaverBlogRecipes(keyword, size) >> blogRecipes

        List<BlogScrap> blogScraps = [
                BlogScrap.builder()
                        .blogScrapId(1)
                        .userId(user.userId)
                        .blogRecipeId(blogRecipes.get(0).blogRecipeId)
                        .build()
        ]

        blogScrapService.findByBlogRecipeIds(blogRecipes.blogRecipeId) >> blogScraps

        when:
        RecipesResponse result = blogRecipeService.findBlogRecipesByKeyword(user, keyword, lastBlogRecipeId, size, sort)

        then:
        result.totalCnt == 5
        result.recipes.recipeId == blogRecipes.blogRecipeId
        result.recipes.recipeName == blogRecipes.title
        result.recipes.introduction == blogRecipes.description
        result.recipes.thumbnailImgUrl == blogRecipes.blogThumbnailImgUrl
        result.recipes.postDate == [blogRecipes.get(0).publishedAt.format(DateTimeFormatter.ofPattern("yyyy.M.d")), blogRecipes.get(1).publishedAt.format(DateTimeFormatter.ofPattern("yyyy.M.d"))]
        result.recipes.isUserScrap == [true, false]
        result.recipes.scrapCnt == blogRecipes.scrapCnt
        result.recipes.viewCnt == blogRecipes.viewCnt
    }

    def "스크랩한 블로그 레시피 목록 조회"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        long lastBlogRecipeId = 0
        int size = 2

        blogScrapService.countByUserId(user.userId) >> 5

        blogScrapService.findByUserIdAndBlogRecipeId(user.userId, lastBlogRecipeId) >> null

        List<BlogRecipe> blogRecipes = [
                BlogRecipe.builder()
                        .blogRecipeId(1L)
                        .blogUrl("https://naver.com")
                        .blogThumbnailImgUrl("")
                        .title("제목")
                        .description("설명")
                        .publishedAt(LocalDate.now())
                        .blogName("블로그명")
                        .scrapCnt(1)
                        .viewCnt(1)
                        .build(),
                BlogRecipe.builder()
                        .blogRecipeId(2L)
                        .blogUrl("https://naver.com")
                        .blogThumbnailImgUrl("")
                        .title("제목")
                        .description("설명")
                        .publishedAt(LocalDate.now())
                        .blogName("블로그명")
                        .scrapCnt(1)
                        .viewCnt(1)
                        .build(),
        ]

        blogRecipeRepository.findUserScrapBlogRecipesLimit(user.userId, lastBlogRecipeId, null, size) >> blogRecipes

        List<BlogScrap> blogScraps = [
                BlogScrap.builder()
                        .blogScrapId(1)
                        .userId(user.userId)
                        .blogRecipeId(blogRecipes.get(0).blogRecipeId)
                        .build()
        ]

        blogScrapService.findByBlogRecipeIds(blogRecipes.blogRecipeId) >> blogScraps

        when:
        RecipesResponse result = blogRecipeService.findScrapBlogRecipes(user, lastBlogRecipeId, size)

        then:
        result.totalCnt == 5
        result.recipes.recipeId == blogRecipes.blogRecipeId
        result.recipes.recipeName == blogRecipes.title
        result.recipes.introduction == blogRecipes.description
        result.recipes.thumbnailImgUrl == blogRecipes.blogThumbnailImgUrl
        result.recipes.postDate == [blogRecipes.get(0).publishedAt.format(DateTimeFormatter.ofPattern("yyyy.M.d")), blogRecipes.get(1).publishedAt.format(DateTimeFormatter.ofPattern("yyyy.M.d"))]
        result.recipes.isUserScrap == [true, false]
        result.recipes.scrapCnt == blogRecipes.scrapCnt
        result.recipes.viewCnt == blogRecipes.viewCnt
    }

    def "블로그 스크랩 정보 생성"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        BlogRecipe blogRecipe = BlogRecipe.builder()
                .blogRecipeId(1L)
                .blogUrl("https://naver.com")
                .blogThumbnailImgUrl("")
                .title("제목")
                .description("설명")
                .publishedAt(LocalDate.now())
                .blogName("블로그명")
                .scrapCnt(1)
                .viewCnt(1)
                .build()

        blogRecipeRepository.findById(blogRecipe.blogRecipeId) >> Optional.of(blogRecipe)

        when:
        blogRecipeService.createBlogScrap(user, blogRecipe.blogRecipeId)

        then:
        1 * blogScrapService.create(user.userId, blogRecipe.blogRecipeId)
        blogRecipe.scrapCnt == 2
    }

    def "블로그 스크랩 정보 생성 시 블로그 레시피 존재하지 않으면 실행 안함"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        BlogRecipe blogRecipe = BlogRecipe.builder()
                .blogRecipeId(1L)
                .blogUrl("https://naver.com")
                .blogThumbnailImgUrl("")
                .title("제목")
                .description("설명")
                .publishedAt(LocalDate.now())
                .blogName("블로그명")
                .scrapCnt(1)
                .viewCnt(1)
                .build()

        blogRecipeRepository.findById(blogRecipe.blogRecipeId) >> Optional.empty()

        when:
        blogRecipeService.createBlogScrap(user, blogRecipe.blogRecipeId)

        then:
        0 * blogScrapService.create(user.userId, blogRecipe.blogRecipeId)
    }

    def "블로그 스크랩 정보 삭제"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        BlogRecipe blogRecipe = BlogRecipe.builder()
                .blogRecipeId(1L)
                .blogUrl("https://naver.com")
                .blogThumbnailImgUrl("")
                .title("제목")
                .description("설명")
                .publishedAt(LocalDate.now())
                .blogName("블로그명")
                .scrapCnt(1)
                .viewCnt(1)
                .build()

        blogRecipeRepository.findById(blogRecipe.blogRecipeId) >> Optional.of(blogRecipe)

        when:
        blogRecipeService.deleteBlogScrap(user, blogRecipe.blogRecipeId)

        then:
        1 * blogScrapService.delete(user.userId, blogRecipe.blogRecipeId)
        blogRecipe.scrapCnt == 0
    }

    def "블로그 스크랩 정보 삭제 시 블로그 레시피 존재하지 않으면 실행 안함"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        BlogRecipe blogRecipe = BlogRecipe.builder()
                .blogRecipeId(1L)
                .blogUrl("https://naver.com")
                .blogThumbnailImgUrl("")
                .title("제목")
                .description("설명")
                .publishedAt(LocalDate.now())
                .blogName("블로그명")
                .scrapCnt(1)
                .viewCnt(1)
                .build()

        blogRecipeRepository.findById(blogRecipe.blogRecipeId) >> Optional.empty()

        when:
        blogRecipeService.deleteBlogScrap(user, blogRecipe.blogRecipeId)

        then:
        0 * blogScrapService.delete(user.userId, blogRecipe.blogRecipeId)
    }

    def "블로그 조회 정보 생성"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        BlogRecipe blogRecipe = BlogRecipe.builder()
                .blogRecipeId(1L)
                .blogUrl("https://naver.com")
                .blogThumbnailImgUrl("")
                .title("제목")
                .description("설명")
                .publishedAt(LocalDate.now())
                .blogName("블로그명")
                .scrapCnt(1)
                .viewCnt(1)
                .build()

        blogRecipeRepository.findById(blogRecipe.blogRecipeId) >> Optional.of(blogRecipe)

        when:
        blogRecipeService.createBlogView(user, blogRecipe.blogRecipeId)

        then:
        1 * blogViewService.create(user.userId, blogRecipe.blogRecipeId)
        blogRecipe.viewCnt == 2
    }

    def "블로그 조회 정보 생성 시 블로그 레시피 존재하지 않으면 실행 안함"() {

        given:
        User user = User.builder()
                .userId(1)
                .socialId("naver_1")
                .nickname("테스터1")
                .build()

        BlogRecipe blogRecipe = BlogRecipe.builder()
                .blogRecipeId(1L)
                .blogUrl("https://naver.com")
                .blogThumbnailImgUrl("")
                .title("제목")
                .description("설명")
                .publishedAt(LocalDate.now())
                .blogName("블로그명")
                .scrapCnt(1)
                .viewCnt(1)
                .build()

        blogRecipeRepository.findById(blogRecipe.blogRecipeId) >> Optional.empty()

        when:
        blogRecipeService.createBlogView(user, blogRecipe.blogRecipeId)

        then:
        0 * blogViewService.create(user.userId, blogRecipe.blogRecipeId)
    }
}
