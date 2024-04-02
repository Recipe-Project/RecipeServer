package com.recipe.app.src.recipe.infra.blog;

import com.recipe.app.src.recipe.domain.blog.BlogScrap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface BlogScrapRepository extends JpaRepository<BlogScrap, Long> {

    Optional<BlogScrap> findByUserIdAndBlogRecipeId(Long userId, Long blogRecipeId);

    List<BlogScrap> findByUserId(Long userId);

    long countByUserId(Long userId);

    List<BlogScrap> findByBlogRecipeIdIn(Collection<Long> blogRecipeIds);

    long countByBlogRecipeId(Long blogRecipeId);
}
