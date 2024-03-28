package com.recipe.app.src.recipe.infra.youtube;

import com.recipe.app.src.recipe.domain.youtube.YoutubeRecipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YoutubeRecipeRepository extends JpaRepository<YoutubeRecipe, Long> {

    Page<YoutubeRecipe> findByTitleContainingOrDescriptionContainingOrderByCreatedAtDesc(String titleKeyword, String descriptionKeyword, Pageable pageable);

    @Query("select yr from YoutubeRecipe yr\n" +
            "where yr.title like concat('%', :titleKeyword,'%') or yr.description like concat('%', :descriptionKeyword,'%')\n" +
            "group by yr.youtubeRecipeId order by yr.youtubeScraps.size desc")
    Page<YoutubeRecipe> findByTitleContainingOrDescriptionContainingOrderByYoutubeScrapSizeDesc(String titleKeyword, String descriptionKeyword, Pageable pageable);

    @Query("select yr from YoutubeRecipe yr\n" +
            "where yr.title like concat('%', :titleKeyword,'%') or yr.description like concat('%', :descriptionKeyword,'%')\n" +
            "group by yr.youtubeRecipeId order by yr.youtubeViews.size desc")
    Page<YoutubeRecipe> findByTitleContainingOrDescriptionContainingOrderByYoutubeViewSizeDesc(String titleKeyword, String descriptionKeyword, Pageable pageable);

    List<YoutubeRecipe> findByYoutubeIdIn(List<String> youtubeIds);
}
