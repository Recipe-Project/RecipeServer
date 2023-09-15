package com.recipe.app.src.recipe.infra.youtube;

import com.recipe.app.src.user.infra.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface YoutubeScrapJpaRepository extends CrudRepository<YoutubeScrapEntity, Long> {
    Optional<YoutubeScrapEntity> findByUserAndYoutubeRecipe(UserEntity user, YoutubeRecipeEntity youtubeRecipe);

    Page<YoutubeScrapEntity> findByUser(UserEntity user, Pageable pageable);

    long countByUser(UserEntity user);
}
