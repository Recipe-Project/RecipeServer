package com.recipe.app.src.recipe.application.blog;

import com.recipe.app.src.common.client.naver.NaverFeignClient;
import com.recipe.app.src.recipe.domain.blog.BlogRecipe;
import com.recipe.app.src.recipe.infra.blog.BlogRecipeRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BlogRecipeClientSearchService {

    @Value("${naver.client-id}")
    private String naverClientId;
    @Value("${naver.client-secret}")
    private String naverClientSecret;
    private static final int NAVER_BLOG_SEARCH_START_PAGE = 1;
    private static final int NAVER_BLOG_SEARCH_DISPLAY_SIZE = 50;
    private static final String NAVER_BLOG_SEARCH_SORT = "sim";

    private final BlogRecipeRepository blogRecipeRepository;
    private final NaverFeignClient naverFeignClient;
    private final BlogRecipeThumbnailCrawlingService blogRecipeThumbnailCrawlingService;

    public BlogRecipeClientSearchService(BlogRecipeRepository blogRecipeRepository, NaverFeignClient naverFeignClient,
                                         BlogRecipeThumbnailCrawlingService blogRecipeThumbnailCrawlingService) {
        this.blogRecipeRepository = blogRecipeRepository;
        this.naverFeignClient = naverFeignClient;
        this.blogRecipeThumbnailCrawlingService = blogRecipeThumbnailCrawlingService;
    }

    // REQUIRES_NEW: 호출한 검색 트랜잭션과 분리된 새 트랜잭션에서 저장하고, 이 메서드가 반환되는 시점에
    // 커밋된다. 호출 측(BlogRecipeService)이 잡은 키워드 락은 이 커밋 이후에 풀리므로,
    // 다음 요청은 여기서 커밋된 blogUrl 을 findByBlogUrlIn 으로 보고 걸러낸다(중복 방지).
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @CircuitBreaker(name = "recipe-blog-search", fallbackMethod = "fallback")
    public void searchNaverBlogRecipes(String keyword) {

        log.info("naver blog search api call");

        List<BlogRecipe> blogRecipes = naverFeignClient.searchNaverBlog(naverClientId,
                naverClientSecret,
                NAVER_BLOG_SEARCH_START_PAGE,
                NAVER_BLOG_SEARCH_DISPLAY_SIZE,
                NAVER_BLOG_SEARCH_SORT,
                keyword + " 레시피").toEntity();

        List<BlogRecipe> newlyInserted = createBlogRecipes(blogRecipes);

        blogRecipeThumbnailCrawlingService.saveThumbnails(newlyInserted);
    }

    public void fallback(String keyword, Throwable e) {

        log.warn("naver blog search fallback - keyword={}, cause={}", keyword, e.getMessage());
    }

    private List<BlogRecipe> createBlogRecipes(List<BlogRecipe> blogRecipes) {

        // 네이버 응답이 한 배치 안에서 같은 blogUrl 을 중복으로 내려주는 경우가 있어, 먼저 blogUrl 기준으로 distinct 처리한다.
        List<BlogRecipe> distinctBlogRecipes = new ArrayList<>(blogRecipes.stream()
                .collect(Collectors.toMap(BlogRecipe::getBlogUrl, Function.identity(), (o1, o2) -> o1, LinkedHashMap::new))
                .values());

        List<String> blogUrls = distinctBlogRecipes.stream().map(BlogRecipe::getBlogUrl).collect(Collectors.toList());
        List<BlogRecipe> existBlogRecipes = blogRecipeRepository.findByBlogUrlIn(blogUrls);
        Map<String, BlogRecipe> existBlogRecipeMapByBlogUrl = existBlogRecipes.stream().collect(Collectors.toMap(BlogRecipe::getBlogUrl, Function.identity(), (o1, o2) -> o1));

        return blogRecipeRepository.saveAll(distinctBlogRecipes.stream()
                .filter(blogRecipe -> !existBlogRecipeMapByBlogUrl.containsKey(blogRecipe.getBlogUrl()))
                .collect(Collectors.toList()));
    }
}
