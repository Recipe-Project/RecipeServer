package com.recipe.app.src.recipe.application;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.src.recipe.application.port.SearchKeywordRepository;
import com.recipe.app.src.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchKeywordService {

    private final SearchKeywordRepository searchKeywordRepository;

    @Transactional
    public void createSearchKeyword(String keyword, User user) {
        searchKeywordRepository.save(keyword, user);
    }

    public List<String> retrieveRecipesDayBestKeyword() throws BaseException {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterday = now.minusDays(1).withMinute(0).withSecond(0);
        LocalDateTime today = now.withMinute(0).withSecond(0);
        return searchKeywordRepository.findSearchKeywordsTop10(yesterday, today);
    }
}