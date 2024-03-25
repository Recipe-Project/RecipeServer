package com.recipe.app.src.recipe.infra.blog;

import com.recipe.app.src.user.domain.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BlogViewJpaRepository extends CrudRepository<BlogViewEntity, Long> {
    Optional<BlogViewEntity> findByUserAndBlogRecipe(User user, BlogRecipeEntity blogRecipe);
}
