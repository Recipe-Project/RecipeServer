package com.recipe.app.src.recipe.infra.blog;

import com.recipe.app.src.recipe.domain.blog.BlogScrap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlogScrapRepository extends JpaRepository<BlogScrap, Long> {

    Optional<BlogScrap> findByUserIdAndBlogRecipeId(Long userId, Long blogRecipeId);

    Page<BlogScrap> findByUserId(Long userId, Pageable pageable);

    long countByUserId(Long userId);
}
