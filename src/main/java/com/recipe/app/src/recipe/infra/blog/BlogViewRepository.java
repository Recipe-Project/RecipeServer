package com.recipe.app.src.recipe.infra.blog;

import com.recipe.app.src.recipe.domain.blog.BlogView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface BlogViewRepository extends JpaRepository<BlogView, Long> {

    Optional<BlogView> findByUserIdAndBlogRecipeId(Long userId, Long blogRecipeId);

    List<BlogView> findByUserId(Long userId);

    List<BlogView> findByBlogRecipeIdIn(Collection<Long> blogRecipeIds);

    long countByBlogRecipeId(Long blogRecipeId);
}