package com.recipe.app.src.recipe.infra.blog;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BlogRecipeJpaRepository extends CrudRepository<BlogRecipeEntity, Long> {
    List<BlogRecipeEntity> findByTitleContainingOrDescriptionContainingOrderByCreatedAtDesc(String titleKeyword, String descriptionKeyword, Pageable pageable);

    @Query("select br from BlogRecipeEntity br left join BlogScrapEntity bs on bs.blogRecipe.blogRecipeId = br.blogRecipeId\n" +
            "where br.title like concat('%', :titleKeyword,'%') or br.description like concat('%', :descriptionKeyword,'%')\n" +
            "group by br.blogRecipeId order by count(br) desc")
    List<BlogRecipeEntity> findByTitleContainingOrDescriptionContainingOrderByBlogScrapSizeDesc(String titleKeyword, String descriptionKeyword, Pageable pageable);

    @Query("select br from BlogRecipeEntity br left join BlogViewEntity bv on bv.blogRecipe.blogRecipeId = br.blogRecipeId\n" +
            "where br.title like concat('%', :titleKeyword,'%') or br.description like concat('%', :descriptionKeyword,'%')\n" +
            "group by br.blogRecipeId order by count(br) desc")
    List<BlogRecipeEntity> findByTitleContainingOrDescriptionContainingOrderByBlogViewSizeDesc(String titleKeyword, String descriptionKeyword, Pageable pageable);
}
