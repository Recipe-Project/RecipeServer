package com.recipe.app.src.recipe.infra.blog;

import com.recipe.app.src.recipe.domain.blog.BlogRecipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRecipeRepository extends JpaRepository<BlogRecipe, Long> {
    Page<BlogRecipe> findByTitleContainingOrDescriptionContainingOrderByCreatedAtDesc(String titleKeyword, String descriptionKeyword, Pageable pageable);

    @Query("select br from BlogRecipe br\n" +
            "where br.title like concat('%', :titleKeyword,'%') or br.description like concat('%', :descriptionKeyword,'%')\n" +
            "group by br.blogRecipeId order by br.blogScraps.size desc")
    Page<BlogRecipe> findByTitleContainingOrDescriptionContainingOrderByBlogScrapSizeDesc(String titleKeyword, String descriptionKeyword, Pageable pageable);

    @Query("select br from BlogRecipe br\n" +
            "where br.title like concat('%', :titleKeyword,'%') or br.description like concat('%', :descriptionKeyword,'%')\n" +
            "group by br.blogRecipeId order by br.blogViews.size desc")
    Page<BlogRecipe> findByTitleContainingOrDescriptionContainingOrderByBlogViewSizeDesc(String titleKeyword, String descriptionKeyword, Pageable pageable);

    List<BlogRecipe> findByBlogUrlIn(List<String> blogUrls);
}
