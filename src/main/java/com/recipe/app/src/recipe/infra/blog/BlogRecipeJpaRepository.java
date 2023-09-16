package com.recipe.app.src.recipe.infra.blog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BlogRecipeJpaRepository extends CrudRepository<BlogRecipeEntity, Long> {
    Page<BlogRecipeEntity> findByTitleContainingOrDescriptionContainingOrderByCreatedAtDesc(String titleKeyword, String descriptionKeyword, Pageable pageable);

    @Query("select br from BlogRecipeEntity br\n" +
            "where br.title like concat('%', :titleKeyword,'%') or br.description like concat('%', :descriptionKeyword,'%')\n" +
            "group by br.blogRecipeId order by br.blogScraps.size desc")
    Page<BlogRecipeEntity> findByTitleContainingOrDescriptionContainingOrderByBlogScrapSizeDesc(String titleKeyword, String descriptionKeyword, Pageable pageable);

    @Query("select br from BlogRecipeEntity br\n" +
            "where br.title like concat('%', :titleKeyword,'%') or br.description like concat('%', :descriptionKeyword,'%')\n" +
            "group by br.blogRecipeId order by br.blogViews.size desc")
    Page<BlogRecipeEntity> findByTitleContainingOrDescriptionContainingOrderByBlogViewSizeDesc(String titleKeyword, String descriptionKeyword, Pageable pageable);

    List<BlogRecipeEntity> findByBlogUrlIn(List<String> blogUrls);
}
