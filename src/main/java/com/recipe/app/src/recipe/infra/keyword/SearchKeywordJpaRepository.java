package com.recipe.app.src.recipe.infra.keyword;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SearchKeywordJpaRepository extends CrudRepository<SearchKeywordEntity, Long> {
    @Query(value = "select sk, count(keyword) as keywordCount from SearchKeywordEntity sk where group by sk.keyword order by keywordCount desc limit 10;", nativeQuery = true)
    List<SearchKeywordEntity> findSearchKeywordsTop10();
}
