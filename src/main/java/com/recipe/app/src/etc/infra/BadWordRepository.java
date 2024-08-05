package com.recipe.app.src.etc.infra;

import com.recipe.app.src.etc.domain.BadWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BadWordRepository extends JpaRepository<BadWord, String> {
}
