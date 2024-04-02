package com.recipe.app.src.recipe.infra.keyword;

import com.recipe.app.src.recipe.domain.keyword.SearchKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchKeywordRepository extends JpaRepository<SearchKeyword, String> {
}
