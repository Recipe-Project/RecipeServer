package com.recipe.app.src.recipe.infra.keyword;

import org.springframework.data.repository.CrudRepository;

public interface SearchKeywordJpaRepository extends CrudRepository<SearchKeywordEntity, String> {
}
