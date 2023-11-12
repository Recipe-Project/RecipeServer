package com.recipe.app.src.recipe.application.port;

import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SearchKeywordRepository {
    void save(String keyword, User user);

    List<String> findSearchKeywordsTop10(LocalDateTime startDTime, LocalDateTime endDTime);
}
