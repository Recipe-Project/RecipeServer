package com.recipe.app.src.recipe.infra.youtube;

import com.recipe.app.src.user.infra.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface YoutubeScrapJpaRepository extends CrudRepository<YoutubeScrapEntity, Long> {
    Optional<YoutubeScrapEntity> findByUserAndYoutubeRecipe(UserEntity user, YoutubeRecipeEntity youtubeRecipe);

    List<YoutubeScrapEntity> findByUser(UserEntity user);
}
