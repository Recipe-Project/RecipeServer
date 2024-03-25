package com.recipe.app.src.common.infra;

import com.recipe.app.src.common.domain.BadWords;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BadWordsRepository extends CrudRepository<BadWords, String> {

    List<BadWords> findByWordContaining(String keyword);
}
