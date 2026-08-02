package com.recipe.app.src.recipe.infra.blog;

import com.recipe.app.src.recipe.domain.blog.BlogRecipe;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRecipeRepository extends JpaRepository<BlogRecipe, Long>, BlogRecipeCustomRepository {

    List<BlogRecipe> findByBlogUrlIn(List<String> blogUrls);

    // 썸네일이 비어 있는(크롤 실패로 미채움) 블로그 레시피 — 스케줄러가 배치로 재크롤(백로그 포함)
    @Query("SELECT b FROM BlogRecipe b WHERE b.blogThumbnailImgUrl IS NULL OR b.blogThumbnailImgUrl = '' ORDER BY b.blogRecipeId DESC")
    List<BlogRecipe> findEmptyThumbnails(Pageable pageable);
}
