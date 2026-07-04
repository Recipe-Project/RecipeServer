package com.recipe.app.src.recipe.application.keyword;

import com.recipe.app.src.recipe.domain.keyword.SearchKeyword;
import com.recipe.app.src.recipe.infra.keyword.SearchKeywordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 검색어를 로그 테이블(SearchKeyword)에 쌓는다.
 * 검색 응답을 막지 않도록 비동기(@Async)로 기록하며, 기록 실패가 검색을 깨뜨리지 않게 예외를 삼킨다.
 */
@Service
public class SearchKeywordService {

    private static final Logger log = LoggerFactory.getLogger(SearchKeywordService.class);

    private final SearchKeywordRepository searchKeywordRepository;

    public SearchKeywordService(SearchKeywordRepository searchKeywordRepository) {
        this.searchKeywordRepository = searchKeywordRepository;
    }

    @Async
    @Transactional
    public void record(String keyword, Long userId) {
        try {
            searchKeywordRepository.save(SearchKeyword.builder()
                    .keyword(keyword)
                    .userId(userId)
                    .build());
        } catch (Exception e) {
            log.warn("검색어 기록 실패. keyword={}, userId={}", keyword, userId, e);
        }
    }
}
