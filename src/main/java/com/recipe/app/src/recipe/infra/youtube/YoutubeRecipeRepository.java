package com.recipe.app.src.recipe.infra.youtube;

import com.recipe.app.src.recipe.domain.youtube.YoutubeRecipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YoutubeRecipeRepository extends JpaRepository<YoutubeRecipe, Long>, YoutubeRecipeCustomRepository {

    List<YoutubeRecipe> findByYoutubeIdIn(List<String> youtubeIds);
}
