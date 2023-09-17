package com.recipe.app.src.recipe.application.port;

import com.recipe.app.src.user.domain.User;

import java.time.LocalDateTime;
import java.util.List;

public interface SearchKeywordRepository {
    void save(String keyword, User user);

    List<String> findSearchKeywordsTop10(LocalDateTime startDTime, LocalDateTime endDTime);
}
