package com.recipe.app.src.recipe.infra.blog;

import com.recipe.app.src.recipe.domain.blog.BlogRecipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRecipeRepository extends JpaRepository<BlogRecipe, Long>, BlogRecipeCustomRepository {

    List<BlogRecipe> findByBlogUrlIn(List<String> blogUrls);
}
