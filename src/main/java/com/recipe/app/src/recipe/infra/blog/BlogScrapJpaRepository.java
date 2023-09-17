package com.recipe.app.src.recipe.infra.blog;

import com.recipe.app.src.user.infra.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BlogScrapJpaRepository extends CrudRepository<BlogScrapEntity, Long> {
    Optional<BlogScrapEntity> findByUserAndBlogRecipe(UserEntity user, BlogRecipeEntity blogRecipe);

    Page<BlogScrapEntity> findByUser(UserEntity user, Pageable pageable);

    long countByUser(UserEntity user);
}
