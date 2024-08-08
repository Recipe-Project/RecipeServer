package com.recipe.app.src.recipe.application.youtube;

import com.recipe.app.src.recipe.domain.youtube.YoutubeView;
import com.recipe.app.src.recipe.infra.youtube.YoutubeViewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class YoutubeViewService {

    private final YoutubeViewRepository youtubeViewRepository;

    public YoutubeViewService(YoutubeViewRepository youtubeViewRepository) {
        this.youtubeViewRepository = youtubeViewRepository;
    }

    @Transactional
    public void createYoutubeView(long userId, long youtubeRecipeId) {

        youtubeViewRepository.findByUserIdAndYoutubeRecipeId(userId, youtubeRecipeId)
                .orElseGet(() -> youtubeViewRepository.save(YoutubeView.builder()
                        .userId(userId)
                        .youtubeRecipeId(youtubeRecipeId)
                        .build()));
    }

    @Transactional
    public void deleteYoutubeViewsByUserId(long userId) {

        List<YoutubeView> youtubeViews = youtubeViewRepository.findByUserId(userId);

        youtubeViewRepository.deleteAll(youtubeViews);
    }

    @Transactional(readOnly = true)
    public long countByYoutubeRecipeId(long youtubeRecipeId) {

        return youtubeViewRepository.countByYoutubeRecipeId(youtubeRecipeId);
    }
}
