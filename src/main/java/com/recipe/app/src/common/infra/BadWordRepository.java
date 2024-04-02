package com.recipe.app.src.common.infra;

import com.recipe.app.src.common.domain.BadWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BadWordRepository extends JpaRepository<BadWord, String> {

    List<BadWord> findByWordContaining(String keyword);
}
