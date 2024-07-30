package com.recipe.app.src.recipe.application.blog;

import com.recipe.app.common.client.NaverFeignClient;
import com.recipe.app.src.common.application.BadWordService;
import com.recipe.app.src.recipe.application.dto.RecipesResponse;
import com.recipe.app.src.recipe.domain.blog.BlogRecipe;
import com.recipe.app.src.recipe.domain.blog.BlogRecipes;
import com.recipe.app.src.recipe.domain.blog.BlogScrap;
import com.recipe.app.src.recipe.exception.NotFoundRecipeException;
import com.recipe.app.src.recipe.infra.blog.BlogRecipeRepository;
import com.recipe.app.src.user.domain.User;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class BlogRecipeService {

    private final BlogRecipeRepository blogRecipeRepository;
    private final BlogScrapService blogScrapService;
    private final BlogViewService blogViewService;
    private final BadWordService badWordService;
    private final NaverFeignClient naverFeignClient;

    @Value("${naver.client-id}")
    private String naverClientId;
    @Value("${naver.client-secret}")
    private String naverClientSecret;
    private static final int NAVER_BLOG_SEARCH_START_PAGE = 1;
    private static final int NAVER_BLOG_SEARCH_DISPLAY_SIZE = 50;
    private static final String NAVER_BLOG_SEARCH_SORT = "sim";
    private static final int MIN_RECIPE_CNT = 10;

    public BlogRecipeService(BlogRecipeRepository blogRecipeRepository, BlogScrapService blogScrapService, BlogViewService blogViewService,
                             BadWordService badWordService, NaverFeignClient naverFeignClient) {
        this.blogRecipeRepository = blogRecipeRepository;
        this.blogScrapService = blogScrapService;
        this.blogViewService = blogViewService;
        this.badWordService = badWordService;
        this.naverFeignClient = naverFeignClient;
    }

    public RecipesResponse getBlogRecipes(User user, String keyword, Long lastBlogRecipeId, int size, String sort) throws UnsupportedEncodingException {

        badWordService.checkBadWords(keyword);

        long totalCnt = blogRecipeRepository.countByKeyword(keyword);

        if (totalCnt < MIN_RECIPE_CNT) {
            return searchNaverBlogRecipes(user, keyword, size);
        }

        List<BlogRecipe> blogRecipes = findByKeywordSortBy(keyword, lastBlogRecipeId, size, sort);

        return getRecipes(user, totalCnt, blogRecipes);
    }

    @Transactional(readOnly = true)
    public List<BlogRecipe> findByKeywordSortBy(String keyword, Long lastBlogRecipeId, int size, String sort) {

        if (sort.equals("blogScraps")) {
            long lastBlogScrapCnt = blogScrapService.countByBlogRecipeId(lastBlogRecipeId);
            return blogRecipeRepository.findByKeywordLimitOrderByBlogScrapCntDesc(keyword, lastBlogRecipeId, lastBlogScrapCnt, size);
        } else if (sort.equals("blogViews")) {
            long lastBlogViewCnt = blogViewService.countByBlogRecipeId(lastBlogRecipeId);
            return blogRecipeRepository.findByKeywordLimitOrderByBlogViewCntDesc(keyword, lastBlogRecipeId, lastBlogViewCnt, size);
        } else {
            BlogRecipe blogRecipe = null;
            if (lastBlogRecipeId != null && lastBlogRecipeId > 0) {
                blogRecipe = blogRecipeRepository.findById(lastBlogRecipeId).orElseThrow(()
                        -> {
                    throw new NotFoundRecipeException();
                });
            }
            return blogRecipeRepository.findByKeywordLimitOrderByPublishedAtDesc(keyword, lastBlogRecipeId, blogRecipe == null ? null : blogRecipe.getPublishedAt(), size);
        }
    }

    @Transactional(readOnly = true)
    public RecipesResponse getScrapBlogRecipes(User user, Long lastBlogRecipeId, int size) {

        long totalCnt = blogScrapService.countBlogScrapByUser(user);
        BlogScrap blogScrap = null;
        if (lastBlogRecipeId != null && lastBlogRecipeId > 0) {
            blogScrap = blogScrapService.findByUserIdAndBlogRecipeId(user.getUserId(), lastBlogRecipeId);
        }
        List<BlogRecipe> blogRecipes = blogRecipeRepository.findUserScrapBlogRecipesLimit(user.getUserId(), lastBlogRecipeId, blogScrap != null ? blogScrap.getCreatedAt() : null, size);

        return getRecipes(user, totalCnt, blogRecipes);
    }

    private RecipesResponse getRecipes(User user, long totalCnt, List<BlogRecipe> blogRecipes) {

        List<Long> blogRecipeIds = blogRecipes.stream()
                .map(BlogRecipe::getBlogRecipeId)
                .collect(Collectors.toList());
        List<BlogScrap> blogScraps = blogScrapService.findByBlogRecipeIds(blogRecipeIds);

        return RecipesResponse.from(totalCnt, new BlogRecipes(blogRecipes), blogScraps, user);
    }

    @CircuitBreaker(name = "recipe", fallbackMethod = "fallbackSearchNaverBlog(")
    public RecipesResponse searchNaverBlogRecipes(User user, String keyword, int size) throws UnsupportedEncodingException {

        String query = URLEncoder.encode(keyword + " 레시피", "UTF-8");

        List<BlogRecipe> blogRecipes = naverFeignClient.searchNaverBlog(naverClientId,
                naverClientSecret,
                NAVER_BLOG_SEARCH_START_PAGE,
                NAVER_BLOG_SEARCH_DISPLAY_SIZE,
                NAVER_BLOG_SEARCH_SORT,
                query).toEntity();

        for (BlogRecipe blogRecipe : blogRecipes) {
            blogRecipe.changeThumbnail(getBlogThumbnailUrl(blogRecipe.getBlogUrl()));
        }

        createBlogRecipes(blogRecipes);

        return getRecipes(user, blogRecipes.size(), blogRecipes.subList(0, size));
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

    public RecipesResponse fallbackSearchNaverBlog(User user, String keyword, int size) {

        long totalCnt = blogRecipeRepository.countByKeyword(keyword);
        List<BlogRecipe> blogRecipes = blogRecipeRepository.findByKeywordLimit(keyword, size);

        return getRecipes(user, totalCnt, blogRecipes);
    }

    private String getBlogThumbnailUrl(String blogUrl) {

        try {
            if (blogUrl.contains("naver")) {
                URL url = new URL(blogUrl);
                Document doc = Jsoup.parse(url, 5000);

                Elements iframes = doc.select("iframe#mainFrame");
                String src = iframes.attr("src");

                String url2 = "http://blog.naver.com" + src;
                Document doc2 = Jsoup.connect(url2).get();

                String[] blog_logNo = src.split("&");
                String[] logNo_split = blog_logNo[1].split("=");
                String logNo = logNo_split[1];

                // 블로그 썸네일 가져오기
                return doc2.select("meta[property=og:image]").get(0).attr("content");

            } else if (blogUrl.contains("tistory")) {
                Document doc = Jsoup.connect(blogUrl).get();

                Elements imageLinks = doc.getElementsByTag("img");
                String result = null;
                for (Element image : imageLinks) {
                    String temp = image.attr("src");
                    if (!temp.contains("admin")) {
                        result = temp;
                        break;
                    }
                }

                return result;
            }

            return null;

        } catch (Exception exception) {
            return null;
        }
    }

    @Transactional
    public void createBlogScrap(User user, Long blogRecipeId) {

        BlogRecipe blogRecipe = blogRecipeRepository.findById(blogRecipeId).orElseThrow(() -> {
            throw new NotFoundRecipeException();
        });
        blogRecipe.plusScrapCnt();
        blogRecipeRepository.save(blogRecipe);
        blogScrapService.createBlogScrap(user, blogRecipeId);
    }

    @Transactional
    public void deleteBlogScrap(User user, Long blogRecipeId) {

        BlogRecipe blogRecipe = blogRecipeRepository.findById(blogRecipeId).orElseThrow(() -> {
            throw new NotFoundRecipeException();
        });
        blogRecipe.minusScrapCnt();
        blogRecipeRepository.save(blogRecipe);
        blogScrapService.deleteBlogScrap(user, blogRecipeId);
    }

    @Transactional
    public void createBlogView(User user, Long blogRecipeId) {

        BlogRecipe blogRecipe = blogRecipeRepository.findById(blogRecipeId).orElseThrow(() -> {
            throw new NotFoundRecipeException();
        });
        blogRecipe.plusViewCnt();
        blogRecipeRepository.save(blogRecipe);
        blogViewService.createBlogView(user, blogRecipeId);
    }
}
