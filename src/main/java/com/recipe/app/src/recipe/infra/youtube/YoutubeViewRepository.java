package com.recipe.app.src.recipe.infra.youtube;

import com.recipe.app.src.recipe.domain.youtube.YoutubeView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface YoutubeViewRepository extends JpaRepository<YoutubeView, Long> {

    Optional<YoutubeView> findByUserIdAndYoutubeRecipeId(Long userId, Long youtubeRecipeId);

    List<YoutubeView> findByUserId(Long userId);
}
