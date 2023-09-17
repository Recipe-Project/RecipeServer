package com.recipe.app.src.recipe.infra.youtube;

import com.recipe.app.src.user.infra.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface YoutubeViewJpaRepository extends CrudRepository<YoutubeViewEntity, Long> {
    Optional<YoutubeViewEntity> findByUserAndYoutubeRecipe(UserEntity user, YoutubeRecipeEntity youtubeRecipe);
}
