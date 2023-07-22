package com.recipe.app.src.keyword.mapper;

import com.recipe.app.src.keyword.domain.RecipeKeyword;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeKeywordRepository extends CrudRepository<RecipeKeyword, Integer> {
    @Query(value = "select keyword, count(keyword) as keywordCount\n" +
            "from RecipeKeyword\n" +
            "where status = 'ACTIVE' " +
            "       and createdAt BETWEEN concat(DATE_FORMAT(NOW()- INTERVAL 1 DAY,'%Y-%m-%d %H'),':00:00') AND concat(DATE_FORMAT(NOW(),'%Y-%m-%d %H'), ':00:00')\n" +
            "group by keyword\n" +
            "order by keywordCount desc limit 10;", nativeQuery = true)
    List<Object[]> findByBestKeywordTop10();
}
