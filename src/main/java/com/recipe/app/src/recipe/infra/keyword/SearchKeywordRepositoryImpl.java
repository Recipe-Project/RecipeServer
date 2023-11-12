package com.recipe.app.src.recipe.infra.keyword;

import com.recipe.app.src.recipe.application.port.SearchKeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Repository
@RequiredArgsConstructor
public class SearchKeywordRepositoryImpl implements SearchKeywordRepository {

    private final SearchKeywordJpaRepository searchKeywordJpaRepository;

    @Override
    public List<String> findAll() {
        return StreamSupport.stream(searchKeywordJpaRepository.findAll().spliterator(), false)
                .map(SearchKeywordEntity::getKeyword)
                .collect(Collectors.toList());
    }
}
