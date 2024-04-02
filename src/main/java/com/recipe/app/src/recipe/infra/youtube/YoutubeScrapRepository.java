package com.recipe.app.src.recipe.infra.youtube;

import com.recipe.app.src.recipe.domain.youtube.YoutubeScrap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface YoutubeScrapRepository extends JpaRepository<YoutubeScrap, Long> {

    Optional<YoutubeScrap> findByUserIdAndYoutubeRecipeId(Long userId, Long youtubeRecipeId);

    List<YoutubeScrap> findByUserId(Long userId);

    long countByUserId(Long userId);

    List<YoutubeScrap> findByYoutubeRecipeIdIn(Collection<Long> youtubeRecipeIds);

    long countByYoutubeRecipeId(Long youtubeRecipeId);
}
