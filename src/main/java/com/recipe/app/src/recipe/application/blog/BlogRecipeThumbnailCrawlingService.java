package com.recipe.app.src.recipe.application.blog;

import com.recipe.app.src.recipe.domain.blog.BlogRecipe;
import com.recipe.app.src.recipe.infra.blog.BlogRecipeRepository;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BlogRecipeThumbnailCrawlingService {

    // 썸네일 크롤링은 URL 당 외부 HTTP 를 최대 2회 호출한다. 순차로 하면 매우 느려서 병렬로 처리한다.
    private static final int CRAWL_POOL_SIZE = 32;
    // 첫 검색이 썸네일 크롤링 때문에 지나치게 오래 블로킹되지 않도록 전체 대기 상한을 둔다.
    private static final long CRAWL_TIMEOUT_SECONDS = 6L;
    // 각 페이지 fetch 타임아웃(ms). Jsoup connect 의 기본값(30s)은 느린 URL 하나가 스레드를 오래 점유하므로 줄인다.
    private static final int FETCH_TIMEOUT_MS = 3000;

    private final BlogRecipeRepository blogRecipeRepository;

    public BlogRecipeThumbnailCrawlingService(BlogRecipeRepository blogRecipeRepository) {
        this.blogRecipeRepository = blogRecipeRepository;
    }

    /**
     * 신규 저장된 블로그 레시피들의 썸네일을 크롤링해 채운다.
     * 첫 검색에서도 썸네일이 보이도록 동기로 처리하되, 병렬 + 전체 타임아웃으로 대기 시간을 제한한다.
     * 타임아웃 내에 못 끝낸 소수는 썸네일이 빈 채로 남는다(크롤링 실패도 빈 문자열).
     */
    public void saveThumbnails(List<BlogRecipe> blogRecipes) {

        if (blogRecipes == null || blogRecipes.isEmpty()) {
            return;
        }

        ExecutorService pool = Executors.newFixedThreadPool(Math.min(CRAWL_POOL_SIZE, blogRecipes.size()));
        try {
            List<Callable<Void>> tasks = blogRecipes.stream()
                    .map(blogRecipe -> (Callable<Void>) () -> {
                        blogRecipe.changeThumbnail(getBlogThumbnailUrl(blogRecipe.getBlogUrl()));
                        return null;
                    })
                    .collect(Collectors.toList());
            pool.invokeAll(tasks, CRAWL_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("blog thumbnail crawling interrupted");
        } finally {
            pool.shutdownNow();
        }

        blogRecipeRepository.saveAll(blogRecipes);
    }

    public String getBlogThumbnailUrl(String blogUrl) {

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
            Document doc = Jsoup.parse(url, FETCH_TIMEOUT_MS);

            Elements iframes = doc.select("iframe#mainFrame");
            String src = iframes.attr("src");

            String url2 = "http://blog.naver.com" + src;
            Document doc2 = Jsoup.connect(url2).timeout(FETCH_TIMEOUT_MS).get();

            return doc2.select("meta[property=og:image]").get(0).attr("content");
        } catch (Exception e) {
            return "";
        }
    }

    private String getTistoryBlogThumbnailUrl(String blogUrl) {

        try {

            Document doc = Jsoup.connect(blogUrl).timeout(FETCH_TIMEOUT_MS).get();

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
