package com.recipe.app.src.recipe.infra;

import com.recipe.app.src.user.infra.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BlogViewJpaRepository extends CrudRepository<BlogViewEntity, Long> {
    Optional<BlogViewEntity> findByUserAndBlogRecipe(UserEntity user, BlogRecipeEntity blogRecipe);
}
