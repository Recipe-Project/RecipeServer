package com.recipe.app.src.recipe.infra.blog;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BlogRecipeJpaRepository extends CrudRepository<BlogRecipeEntity, Long> {
    List<BlogRecipeEntity> findByTitleContainingOrDescriptionContaining(String titleKeyword, String descriptionKeyword);
}
