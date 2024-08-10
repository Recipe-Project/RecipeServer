package com.recipe.app.src.recipe.application.blog;

import com.recipe.app.src.common.client.naver.NaverFeignClient;
import com.recipe.app.src.recipe.domain.blog.BlogRecipe;
import com.recipe.app.src.recipe.infra.blog.BlogRecipeRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @CircuitBreaker(name = "recipe-blog-search", fallbackMethod = "fallback")
    public List<BlogRecipe> searchNaverBlogRecipes(String keyword, int size) {

        log.info("naver blog search api call");

        List<BlogRecipe> blogRecipes = naverFeignClient.searchNaverBlog(naverClientId,
                naverClientSecret,
                NAVER_BLOG_SEARCH_START_PAGE,
                NAVER_BLOG_SEARCH_DISPLAY_SIZE,
                NAVER_BLOG_SEARCH_SORT,
                keyword + " 레시피").toEntity();

        createBlogRecipes(blogRecipes);

        blogRecipeThumbnailCrawlingService.saveThumbnails(blogRecipes);

        return blogRecipes.subList(0, size);
    }

    public List<BlogRecipe> fallback(String keyword, int size, Exception e) {

        log.info("fallback call - " + e.getMessage());

        return blogRecipeRepository.findByKeywordLimit(keyword, size);
    }

    @Transactional
    public void createBlogRecipes(List<BlogRecipe> blogRecipes) {

        List<String> blogUrls = blogRecipes.stream().map(BlogRecipe::getBlogUrl).collect(Collectors.toList());
        List<BlogRecipe> existBlogRecipes = blogRecipeRepository.findByBlogUrlIn(blogUrls);
        Map<String, BlogRecipe> existBlogRecipeMapByBlogUrl = existBlogRecipes.stream().collect(Collectors.toMap(BlogRecipe::getBlogUrl, Function.identity()));

        blogRecipeRepository.saveAll(blogRecipes.stream()
                .filter(blogRecipe -> !existBlogRecipeMapByBlogUrl.containsKey(blogRecipe.getBlogUrl()))
                .collect(Collectors.toList()));
    }
}
