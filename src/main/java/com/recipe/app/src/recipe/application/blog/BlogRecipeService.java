package com.recipe.app.src.recipe.application.blog;

import com.recipe.app.common.utils.HttpUtil;
import com.recipe.app.src.common.application.BadWordService;
import com.recipe.app.src.recipe.application.dto.RecipeResponse;
import com.recipe.app.src.recipe.application.dto.RecipesResponse;
import com.recipe.app.src.recipe.domain.blog.BlogRecipe;
import com.recipe.app.src.recipe.domain.blog.BlogScrap;
import com.recipe.app.src.recipe.domain.blog.BlogView;
import com.recipe.app.src.recipe.infra.blog.BlogRecipeRepository;
import com.recipe.app.src.user.domain.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
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

    public BlogRecipeService(BlogRecipeRepository blogRecipeRepository, BlogScrapService blogScrapService, BlogViewService blogViewService, BadWordService badWordService) {
        this.blogRecipeRepository = blogRecipeRepository;
        this.blogScrapService = blogScrapService;
        this.blogViewService = blogViewService;
        this.badWordService = badWordService;
    }

    @Value("${naver.client-id}")
    private String naverClientId;
    @Value("${naver.client-secret}")
    private String naverClientSecret;

    @Transactional(readOnly = true)
    public RecipesResponse getBlogRecipes(User user, String keyword, int page, int size, String sort) throws IOException, ParseException {

        badWordService.checkBadWords(keyword);

        Page<BlogRecipe> blogRecipes = findByKeywordSortBy(keyword, page, size, sort);

        if (blogRecipes.getTotalElements() < 10) {
            searchNaverBlogRecipes(keyword);
            blogRecipes = findByKeywordSortBy(keyword, page, size, sort);
        }

        return getRecipes(user, blogRecipes);
    }

    private Page<BlogRecipe> findByKeywordSortBy(String keyword, int page, int size, String sort) {

        Pageable pageable = PageRequest.of(page, size);
        Page<BlogRecipe> blogRecipes;
        if (sort.equals("blogScraps"))
            blogRecipes = blogRecipeRepository.findByTitleContainingOrDescriptionContainingOrderByBlogScrapSizeDesc(keyword, keyword, pageable);
        else if (sort.equals("blogViews"))
            blogRecipes = blogRecipeRepository.findByTitleContainingOrDescriptionContainingOrderByBlogViewSizeDesc(keyword, keyword, pageable);
        else
            blogRecipes = blogRecipeRepository.findByTitleContainingOrDescriptionContainingOrderByCreatedAtDesc(keyword, keyword, pageable);

        return blogRecipes;
    }

    @Transactional(readOnly = true)
    public RecipesResponse getScrapBlogRecipes(User user, int page, int size) {

        Page<BlogScrap> blogScraps = blogScrapService.findByUserId(user.getUserId(), page, size);
        List<Long> blogRecipeIds = blogScraps.stream()
                .map(BlogScrap::getBlogRecipeId)
                .collect(Collectors.toList());
        Map<Long, BlogRecipe> blogRecipeMapById = blogRecipeRepository.findAllById(blogRecipeIds).stream()
                .collect(Collectors.toMap(BlogRecipe::getBlogRecipeId, Function.identity()));
        Page<BlogRecipe> blogRecipes = blogScraps.map(blogScrap -> blogRecipeMapById.get(blogScrap.getBlogRecipeId()));

        return getRecipes(user, blogRecipes);
    }

    private RecipesResponse getRecipes(User user, Page<BlogRecipe> blogRecipes) {

        List<Long> blogRecipeIds = blogRecipes.stream()
                .map(BlogRecipe::getBlogRecipeId)
                .collect(Collectors.toList());
        List<BlogScrap> blogScraps = blogScrapService.findByBlogRecipeIds(blogRecipeIds);
        List<BlogView> blogViews = blogViewService.findByBlogRecipeIds(blogRecipeIds);

        return new RecipesResponse(blogRecipes.getTotalElements(), blogRecipes.stream()
                .map(blogRecipe -> {
                    long scrapCnt = getBlogScrapCnt(blogScraps, blogRecipe.getBlogRecipeId(), user);
                    long viewCnt = getBlogViewCnt(blogViews, blogRecipe.getBlogRecipeId(), user);
                    boolean isUserScrap = isUserScrap(blogScraps, blogRecipe.getBlogRecipeId(), user);

                    return RecipeResponse.from(blogRecipe, isUserScrap, scrapCnt, viewCnt);
                })
                .collect(Collectors.toList()));
    }

    private boolean isUserScrap(List<BlogScrap> blogScraps, Long blogRecipeId, User user) {
        return blogScraps.stream()
                .anyMatch(blogScrap -> blogScrap.getBlogRecipeId().equals(blogRecipeId) && blogScrap.getUserId().equals(user.getUserId()));
    }

    private long getBlogViewCnt(List<BlogView> blogViews, Long blogRecipeId, User user) {
        return blogViews.stream()
                .filter(blogView -> blogView.getBlogViewId().equals(blogRecipeId) && blogView.getUserId().equals(user.getUserId()))
                .count();
    }

    private long getBlogScrapCnt(List<BlogScrap> blogScraps, Long blogRecipeId, User user) {
        return blogScraps.stream()
                .filter(blogScrap -> blogScrap.getBlogRecipeId().equals(blogRecipeId) && blogScrap.getUserId().equals(user.getUserId()))
                .count();
    }

    @Transactional
    public void searchNaverBlogRecipes(String keyword) throws IOException, ParseException {

        String apiURL = "https://openapi.naver.com/v1/search/blog?sort=sim&start=1&display=100&query=" + URLEncoder.encode(keyword + " 레시피", "UTF-8");

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", naverClientId);
        requestHeaders.put("X-Naver-Client-Secret", naverClientSecret);

        JSONObject response = HttpUtil.getHTTP(apiURL, requestHeaders);
        JSONArray items = (JSONArray) response.get("items");
        int total = Integer.parseInt(response.get("total").toString());

        List<BlogRecipe> blogs = new ArrayList<>();
        for (Object item : items) {
            String title = ((JSONObject) item).get("title").toString()
                    .replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
            String blogUrl = ((JSONObject) item).get("link").toString();
            String description = ((JSONObject) item).get("description").toString()
                    .replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
            String blogName = ((JSONObject) item).get("bloggername").toString();
            LocalDate publishedAt = LocalDate.parse(((JSONObject) item).get("postdate").toString(), DateTimeFormatter.ofPattern("yyyyMMdd"));
            String thumbnail = getBlogThumbnailUrl(blogUrl);

            blogs.add(BlogRecipe.builder()
                    .blogUrl(blogUrl)
                    .blogThumbnailImgUrl(thumbnail)
                    .title(title)
                    .description(description)
                    .publishedAt(publishedAt)
                    .blogName(blogName)
                    .build());
        }

        List<String> blogUrls = blogs.stream().map(BlogRecipe::getBlogUrl).collect(Collectors.toList());
        List<BlogRecipe> existBlogRecipes = blogRecipeRepository.findByBlogUrlIn(blogUrls);
        Map<String, BlogRecipe> existBlogRecipeMapByBlogUrl = existBlogRecipes.stream().collect(Collectors.toMap(BlogRecipe::getBlogUrl, Function.identity()));
        List<BlogRecipe> blogRecipes = blogs.stream()
                .map(blog -> existBlogRecipeMapByBlogUrl.getOrDefault(blog.getBlogUrl(), blog))
                .collect(Collectors.toList());

        blogRecipeRepository.saveAll(blogRecipes);
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
}
