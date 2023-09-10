package com.recipe.app.src.recipe.infra.blog;

import com.recipe.app.src.user.infra.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface BlogScrapJpaRepository extends CrudRepository<BlogScrapEntity, Long> {
    Optional<BlogScrapEntity> findByUserAndBlogRecipe(UserEntity user, BlogRecipeEntity blogRecipe);

    List<BlogScrapEntity> findByUser(UserEntity user);

    long countByUser(UserEntity user);
}
