package com.recipe.app.src.recipe.infra.youtube;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface YoutubeRecipeJpaRepository extends CrudRepository<YoutubeRecipeEntity, Long> {

    Page<YoutubeRecipeEntity> findByTitleContainingOrDescriptionContainingOrderByCreatedAtDesc(String titleKeyword, String descriptionKeyword, Pageable pageable);

    @Query("select yr from YoutubeRecipeEntity yr\n" +
            "where yr.title like concat('%', :titleKeyword,'%') or yr.description like concat('%', :descriptionKeyword,'%')\n" +
            "group by yr.youtubeRecipeId order by yr.youtubeScraps.size desc")
    Page<YoutubeRecipeEntity> findByTitleContainingOrDescriptionContainingOrderByYoutubeScrapSizeDesc(String titleKeyword, String descriptionKeyword, Pageable pageable);

    @Query("select yr from YoutubeRecipeEntity yr\n" +
            "where yr.title like concat('%', :titleKeyword,'%') or yr.description like concat('%', :descriptionKeyword,'%')\n" +
            "group by yr.youtubeRecipeId order by yr.youtubeViews.size desc")
    Page<YoutubeRecipeEntity> findByTitleContainingOrDescriptionContainingOrderByYoutubeViewSizeDesc(String titleKeyword, String descriptionKeyword, Pageable pageable);

    List<YoutubeRecipeEntity> findByYoutubeIdIn(List<String> youtubeIds);
}
