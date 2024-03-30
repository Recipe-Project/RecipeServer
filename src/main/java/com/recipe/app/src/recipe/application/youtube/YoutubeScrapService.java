package com.recipe.app.src.recipe.application.youtube;

import com.recipe.app.src.recipe.domain.youtube.YoutubeScrap;
import com.recipe.app.src.recipe.infra.youtube.YoutubeScrapRepository;
import com.recipe.app.src.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    public void createYoutubeScrap(User user, Long youtubeRecipeId) {

        YoutubeScrap youtubeScrap = youtubeScrapRepository.findByUserIdAndYoutubeRecipeId(user.getUserId(), youtubeRecipeId)
                .orElseGet(() -> YoutubeScrap.builder()
                        .userId(user.getUserId())
                        .youtubeRecipeId(youtubeRecipeId)
                        .build());

        youtubeScrapRepository.save(youtubeScrap);
    }

    @Transactional
    public void deleteYoutubeScrap(User user, Long youtubeRecipeId) {

        youtubeScrapRepository.findByUserIdAndYoutubeRecipeId(user.getUserId(), youtubeRecipeId)
                .ifPresent(youtubeScrapRepository::delete);
    }

    @Transactional
    public void deleteYoutubeScrapsByUser(User user) {

        List<YoutubeScrap> youtubeScraps = youtubeScrapRepository.findByUserId(user.getUserId());
        youtubeScrapRepository.deleteAll(youtubeScraps);
    }

    @Transactional(readOnly = true)
    public long countYoutubeScrapByUser(User user) {

        return youtubeScrapRepository.countByUserId(user.getUserId());
    }

    @Transactional(readOnly = true)
    public Page<YoutubeScrap> findByUserId(Long userId, int page, int size) {

        return youtubeScrapRepository.findByUserId(userId, PageRequest.of(page, size));
    }

    @Transactional(readOnly = true)
    public List<YoutubeScrap> findByYoutubeRecipeIds(Collection<Long> youtubeRecipeIds) {

        return youtubeScrapRepository.findByYoutubeRecipeIdIn(youtubeRecipeIds);
    }
}
