package com.recipe.app.src.recipe.application.blog;

import com.recipe.app.src.recipe.domain.blog.BlogRecipe;
import com.recipe.app.src.recipe.infra.blog.BlogRecipeRepository;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
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

    // 첫 검색 경로: 응답을 오래 막지 않도록 병렬 처리하되, 네이버 rate-limit 을 피하려 동시성은 과하지 않게.
    // (개별 크롤이 ~0.2~0.5초라 12 동시성이면 50건도 수 초 내, 6초 예산 안에서 처리)
    private static final int FIRST_SEARCH_POOL_SIZE = 12;
    private static final long FIRST_SEARCH_TIMEOUT_SECONDS = 6L;
    // 백필(스케줄러/수동) 경로: 배경 작업이라 네이버 부담을 줄이려 병렬 작게 + 대기 넉넉히.
    private static final int BACKFILL_POOL_SIZE = 8;
    private static final long BACKFILL_TIMEOUT_SECONDS = 120L;

    // 각 페이지 fetch 타임아웃(ms). Jsoup 기본(30s)은 느린 URL 하나가 스레드를 오래 점유하므로 줄인다.
    private static final int FETCH_TIMEOUT_MS = 3000;
    // 타깃 재시도: "페이지 fetch 실패(네트워크/타임아웃)" 에만 재시도. og:image 부재 등은 재시도해도 소용없어 즉시 포기.
    private static final int MAX_FETCH_ATTEMPTS = 3;
    private static final long RETRY_BACKOFF_MS = 700L;

    private final BlogRecipeRepository blogRecipeRepository;

    public BlogRecipeThumbnailCrawlingService(BlogRecipeRepository blogRecipeRepository) {
        this.blogRecipeRepository = blogRecipeRepository;
    }

    /**
     * 신규 저장된 블로그 레시피들의 썸네일을 크롤링해 채운다(첫 검색 경로).
     * 첫 검색에서도 썸네일이 보이도록 동기로 처리하되, 병렬 + 전체 타임아웃으로 대기 시간을 제한한다.
     */
    public void saveThumbnails(List<BlogRecipe> blogRecipes) {
        crawlAndSave(blogRecipes, FIRST_SEARCH_POOL_SIZE, FIRST_SEARCH_TIMEOUT_SECONDS);
    }

    /**
     * 썸네일이 비어 있는(크롤 실패로 미채움) 블로그 레시피를 한 번에 limit 개까지 다시 크롤링해 채운다.
     * (스케줄러 백필 경로. 여러 회차에 걸쳐 백로그를 소진한다.)
     *
     * @return 처리 대상 건수
     */
    public int retryEmptyThumbnails(int limit) {

        List<BlogRecipe> targets = blogRecipeRepository.findEmptyThumbnails(PageRequest.of(0, limit));

        if (targets.isEmpty()) {
            return 0;
        }

        log.info("blog thumbnail retry start - targets={}", targets.size());
        crawlAndSave(targets, BACKFILL_POOL_SIZE, BACKFILL_TIMEOUT_SECONDS);
        log.info("blog thumbnail retry done - targets={}", targets.size());
        return targets.size();
    }

    private void crawlAndSave(List<BlogRecipe> blogRecipes, int poolSize, long timeoutSeconds) {

        if (blogRecipes == null || blogRecipes.isEmpty()) {
            return;
        }

        ExecutorService pool = Executors.newFixedThreadPool(Math.min(poolSize, blogRecipes.size()));
        try {
            List<Callable<Void>> tasks = blogRecipes.stream()
                    .map(blogRecipe -> (Callable<Void>) () -> {
                        blogRecipe.changeThumbnail(crawlThumbnail(blogRecipe.getBlogUrl()));
                        return null;
                    })
                    .collect(Collectors.toList());
            pool.invokeAll(tasks, timeoutSeconds, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("blog thumbnail crawling interrupted");
        } finally {
            pool.shutdownNow();
        }

        blogRecipeRepository.saveAll(blogRecipes);
    }

    /**
     * 타깃 재시도 크롤: 페이지 fetch 실패(IOException/타임아웃)에만 최대 {@link #MAX_FETCH_ATTEMPTS}회 재시도한다.
     * 페이지는 왔는데 og:image 가 없는 경우(영구적 상황)는 빈 문자열을 그대로 반환하고 재시도하지 않는다.
     */
    public String crawlThumbnail(String blogUrl) {

        for (int attempt = 1; attempt <= MAX_FETCH_ATTEMPTS; attempt++) {
            try {
                return fetchThumbnail(blogUrl);
            } catch (IOException e) {
                if (attempt == MAX_FETCH_ATTEMPTS) {
                    log.debug("thumbnail fetch failed after {} attempts: {} ({})", attempt, blogUrl, e.getMessage());
                    return "";
                }
                sleepQuietly(RETRY_BACKOFF_MS);
            }
        }
        return "";
    }

    private String fetchThumbnail(String blogUrl) throws IOException {

        if (blogUrl.contains("naver")) {
            return fetchNaverThumbnail(blogUrl);
        } else if (blogUrl.contains("tistory")) {
            return fetchTistoryThumbnail(blogUrl);
        }
        return "";
    }

    private String fetchNaverThumbnail(String blogUrl) throws IOException {

        Document doc = Jsoup.parse(new URL(blogUrl), FETCH_TIMEOUT_MS);

        String src = doc.select("iframe#mainFrame").attr("src");
        if (src.isBlank()) {
            return "";   // mainFrame 이 없으면 구조상 추출 불가 → 재시도 무의미
        }

        Document doc2 = Jsoup.connect("http://blog.naver.com" + src).timeout(FETCH_TIMEOUT_MS).get();
        Elements og = doc2.select("meta[property=og:image]");
        return og.isEmpty() ? "" : og.get(0).attr("content");
    }

    private String fetchTistoryThumbnail(String blogUrl) throws IOException {

        Document doc = Jsoup.connect(blogUrl).timeout(FETCH_TIMEOUT_MS).get();

        for (Element image : doc.getElementsByTag("img")) {
            String src = image.attr("src");
            if (!src.contains("admin")) {
                return src;
            }
        }
        return "";
    }

    private void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
