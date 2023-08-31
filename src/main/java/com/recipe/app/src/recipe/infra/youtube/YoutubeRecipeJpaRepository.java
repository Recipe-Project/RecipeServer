package com.recipe.app.src.recipe.infra.youtube;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface YoutubeRecipeJpaRepository extends CrudRepository<YoutubeRecipeEntity, Long> {
    List<YoutubeRecipeEntity> findByTitleContainingOrDescriptionContaining(String titleKeyword, String descriptionKeyword);
}
