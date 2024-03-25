package com.recipe.app.src.recipe.infra.youtube;

import com.recipe.app.src.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface YoutubeScrapJpaRepository extends CrudRepository<YoutubeScrapEntity, Long> {
    Optional<YoutubeScrapEntity> findByUserAndYoutubeRecipe(User user, YoutubeRecipeEntity youtubeRecipe);

    Page<YoutubeScrapEntity> findByUser(User user, Pageable pageable);

    long countByUser(User user);
}
