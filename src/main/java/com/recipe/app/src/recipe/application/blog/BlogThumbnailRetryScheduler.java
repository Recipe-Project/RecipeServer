package com.recipe.app.src.recipe.application.blog;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 썸네일이 비어 있는(크롤 실패로 미채움) 블로그 레시피를 주기적으로 재크롤한다.
 *
 * - 시간 제한 없이 빈 것 전체 대상(백로그 포함). 한 번에 BATCH_SIZE 개씩 처리하며 여러 회에 걸쳐 소진.
 * - 대부분은 일시 실패라 다음 회차에 채워지고, 영구 실패(삭제/비공개 등 소수)만 매번 재시도된다.
 * - 6시간마다 실행. 대량 외부 크롤이라 동시성은 서비스에서 낮게(8) 잡아 네이버 부담을 줄인다.
 * - 단일 인스턴스 전제(로컬 포함 항상 동작).
 */
@Slf4j
@Component
public class BlogThumbnailRetryScheduler {

    private static final int BATCH_SIZE = 300;

    private final BlogRecipeThumbnailCrawlingService blogRecipeThumbnailCrawlingService;

    public BlogThumbnailRetryScheduler(BlogRecipeThumbnailCrawlingService blogRecipeThumbnailCrawlingService) {
        this.blogRecipeThumbnailCrawlingService = blogRecipeThumbnailCrawlingService;
    }

    // 6시간마다 (00/06/12/18시)
    @Scheduled(cron = "0 0 0/6 * * *")
    public void retryEmptyThumbnails() {
        int processed = blogRecipeThumbnailCrawlingService.retryEmptyThumbnails(BATCH_SIZE);
        log.info("scheduled blog thumbnail retry processed={}", processed);
    }
}
