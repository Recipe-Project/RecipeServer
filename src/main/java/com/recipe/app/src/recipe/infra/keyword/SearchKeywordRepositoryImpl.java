package com.recipe.app.src.recipe.infra.keyword;

import com.recipe.app.src.recipe.application.port.SearchKeywordRepository;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.user.infra.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class SearchKeywordRepositoryImpl implements SearchKeywordRepository {

    private final SearchKeywordJpaRepository searchKeywordJpaRepository;

    @Override
    public void save(String keyword, User user) {
        searchKeywordJpaRepository.save(SearchKeywordEntity.fromModel(keyword, UserEntity.fromModel(user)));
    }

    @Override
    public List<String> findSearchKeywordsTop10(LocalDateTime startDTime, LocalDateTime endDTime) {
        return searchKeywordJpaRepository.findSearchKeywordsTop10(startDTime, endDTime).stream().map(SearchKeywordEntity::getKeyword).collect(Collectors.toList());
    }
}
