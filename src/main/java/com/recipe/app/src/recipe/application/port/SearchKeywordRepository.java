package com.recipe.app.src.recipe.application.port;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchKeywordRepository {

    List<String> findAll();
}
