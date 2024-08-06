package com.recipe.app.src.recipe.application.youtube;

import com.recipe.app.src.recipe.domain.youtube.YoutubeScrap;
import com.recipe.app.src.recipe.infra.youtube.YoutubeScrapRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
public class YoutubeScrapService {

    private final YoutubeScrapRepository youtubeScrapRepository;

    public YoutubeScrapService(YoutubeScrapRepository youtubeScrapRepository) {
        this.youtubeScrapRepository = youtubeScrapRepository;
    }

    @Transactional
    public void createYoutubeScrap(long userId, long youtubeRecipeId) {

        YoutubeScrap youtubeScrap = youtubeScrapRepository.findByUserIdAndYoutubeRecipeId(userId, youtubeRecipeId)
                .orElseGet(() -> YoutubeScrap.builder()
                        .userId(userId)
                        .youtubeRecipeId(youtubeRecipeId)
                        .build());

        youtubeScrapRepository.save(youtubeScrap);
    }

    @Transactional
    public void deleteYoutubeScrap(long userId, long youtubeRecipeId) {

        youtubeScrapRepository.findByUserIdAndYoutubeRecipeId(userId, youtubeRecipeId)
                .ifPresent(youtubeScrapRepository::delete);
    }

    @Transactional
    public void deleteYoutubeScrapsByUserId(long userId) {

        List<YoutubeScrap> youtubeScraps = youtubeScrapRepository.findByUserId(userId);

        youtubeScrapRepository.deleteAll(youtubeScraps);
    }

    @Transactional(readOnly = true)
    public long countYoutubeScrapByUserId(long userId) {

        return youtubeScrapRepository.countByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<YoutubeScrap> findByYoutubeRecipeIds(Collection<Long> youtubeRecipeIds) {

        return youtubeScrapRepository.findByYoutubeRecipeIdIn(youtubeRecipeIds);
    }

    @Transactional(readOnly = true)
    public long countByYoutubeRecipeId(long youtubeRecipeId) {

        return youtubeScrapRepository.countByYoutubeRecipeId(youtubeRecipeId);
    }

    @Transactional(readOnly = true)
    public YoutubeScrap findByUserIdAndYoutubeRecipeId(long userId, long youtubeRecipeId) {

        return youtubeScrapRepository.findByUserIdAndYoutubeRecipeId(userId, youtubeRecipeId).orElse(null);
    }
}
