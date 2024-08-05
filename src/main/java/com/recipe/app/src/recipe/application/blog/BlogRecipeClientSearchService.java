package com.recipe.app.src.recipe.application.blog;

import com.recipe.app.common.client.NaverFeignClient;
import com.recipe.app.src.recipe.domain.blog.BlogRecipe;
import com.recipe.app.src.recipe.infra.blog.BlogRecipeRepository;
import com.recipe.app.src.user.domain.User;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
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

    public BlogRecipeClientSearchService(BlogRecipeRepository blogRecipeRepository, NaverFeignClient naverFeignClient) {
        this.blogRecipeRepository = blogRecipeRepository;
        this.naverFeignClient = naverFeignClient;
    }

    @CircuitBreaker(name = "recipe-blog-search", fallbackMethod = "fallback")
    public List<BlogRecipe> searchNaverBlogRecipes(User user, String keyword, int size) {

        log.info("naver blog search api call");

        List<BlogRecipe> blogRecipes = naverFeignClient.searchNaverBlog(naverClientId,
                naverClientSecret,
                NAVER_BLOG_SEARCH_START_PAGE,
                NAVER_BLOG_SEARCH_DISPLAY_SIZE,
                NAVER_BLOG_SEARCH_SORT,
                keyword + " 레시피").toEntity();

        for (BlogRecipe blogRecipe : blogRecipes) {
            blogRecipe.changeThumbnail(getBlogThumbnailUrl(blogRecipe.getBlogUrl()));
        }

        createBlogRecipes(blogRecipes);

        return blogRecipes.subList(0, size);
    }

    public List<BlogRecipe> fallback(User user, String keyword, int size, Exception e) {

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

    private String getBlogThumbnailUrl(String blogUrl) {

        if (blogUrl.contains("naver")) {
            return getNaverBlogThumbnailUrl(blogUrl);
        } else if (blogUrl.contains("tistory")) {
            return getTistoryBlogThumbnailUrl(blogUrl);
        } else {
            return "";
        }
    }

    private String getNaverBlogThumbnailUrl(String blogUrl) {

        try {

            URL url = new URL(blogUrl);
            Document doc = Jsoup.parse(url, 5000);

            Elements iframes = doc.select("iframe#mainFrame");
            String src = iframes.attr("src");

            String url2 = "http://blog.naver.com" + src;
            Document doc2 = Jsoup.connect(url2).get();

            return doc2.select("meta[property=og:image]").get(0).attr("content");
        } catch (Exception e) {
            return "";
        }
    }

    private String getTistoryBlogThumbnailUrl(String blogUrl) {

        try {

            Document doc = Jsoup.connect(blogUrl).get();

            Elements imageLinks = doc.getElementsByTag("img");
            String thumbnailUrl = null;
            for (Element image : imageLinks) {
                String temp = image.attr("src");
                if (!temp.contains("admin")) {
                    thumbnailUrl = temp;
                    break;
                }
            }

            return thumbnailUrl;
        } catch (Exception e) {
            return "";
        }
    }

}
