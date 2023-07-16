package com.recipe.app.src.scrap.application;

import com.recipe.app.common.exception.BaseException;
import com.recipe.app.src.scrap.application.dto.ScrapYoutubeDto;
import com.recipe.app.src.scrap.domain.ScrapYoutube;
import com.recipe.app.src.scrap.mapper.ScrapYoutubeRepository;
import com.recipe.app.src.scrapYoutube.models.*;
import com.recipe.app.src.user.application.UserService;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.common.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScrapYoutubeService {

    private final ScrapYoutubeRepository scrapYoutubeRepository;

    public void createOrDeleteScrapYoutube(ScrapYoutubeDto.ScrapYoutubeRequest request, User user) {
        scrapYoutubeRepository.findByYoutubeIdAndUserAndStatus(request.getYoutubeId(), user,"ACTIVE")
                .ifPresentOrElse(scrapYoutubeRepository::delete, () -> {
                    scrapYoutubeRepository.save(new ScrapYoutube(user, request.getYoutubeId(), request.getTitle(), request.getThumbnail(), request.getYoutubeUrl(), request.getPostDate(), request.getChannelName(), request.getPlayTime()));
                });
    }

    public List<ScrapYoutube> retrieveScrapYoutubes(User user) {
        return scrapYoutubeRepository.findByUserAndStatusOrderByCreatedAtDesc(user, "ACTIVE");
    }

    public long countScrapYoutubesByUser(User user) {
        return scrapYoutubeRepository.countByUserAndStatus(user,"ACTIVE");
    }

    public long countScrapYoutubesByYoutubeId(String youtubeId) {
        return scrapYoutubeRepository.countByYoutubeIdAndStatus(youtubeId,"ACTIVE");
    }
}