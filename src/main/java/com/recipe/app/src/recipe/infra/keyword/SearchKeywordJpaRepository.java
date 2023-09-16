package com.recipe.app.src.recipe.infra.keyword;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SearchKeywordJpaRepository extends CrudRepository<SearchKeywordEntity, Long> {
    @Query(value = "select * from SearchKeyword sk where sk.createdAt between :startDTime and :endDTime group by sk.keyword order by count(*) desc limit 10;", nativeQuery = true)
    List<SearchKeywordEntity> findSearchKeywordsTop10(LocalDateTime startDTime, LocalDateTime endDTime);
}
