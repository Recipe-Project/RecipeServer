package com.recipe.app.src.recipe.application.youtube;

import com.recipe.app.src.recipe.domain.youtube.YoutubeView;
import com.recipe.app.src.recipe.infra.youtube.YoutubeViewRepository;
import com.recipe.app.src.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
public class YoutubeViewService {

    private final YoutubeViewRepository youtubeViewRepository;

    public YoutubeViewService(YoutubeViewRepository youtubeViewRepository) {
        this.youtubeViewRepository = youtubeViewRepository;
    }

    @Transactional
    public void createYoutubeView(User user, Long youtubeRecipeId) {

        YoutubeView youtubeView = youtubeViewRepository.findByUserIdAndYoutubeRecipeId(user.getUserId(), youtubeRecipeId)
                .orElseGet(() -> YoutubeView.builder()
                        .userId(user.getUserId())
                        .youtubeRecipeId(youtubeRecipeId)
                        .build());

        youtubeViewRepository.save(youtubeView);
    }

    @Transactional
    public void deleteYoutubeViewsByUser(User user) {

        List<YoutubeView> youtubeViews = youtubeViewRepository.findByUserId(user.getUserId());
        youtubeViewRepository.deleteAll(youtubeViews);
    }

    @Transactional(readOnly = true)
    public List<YoutubeView> findByYoutubeRecipeIds(Collection<Long> youtubeRecipeIds) {

        return youtubeViewRepository.findByYoutubeRecipeIdIn(youtubeRecipeIds);
    }

    @Transactional(readOnly = true)
    public long countByYoutubeRecipeId(Long youtubeRecipeId) {

        return youtubeViewRepository.countByYoutubeRecipeId(youtubeRecipeId);
    }
}
