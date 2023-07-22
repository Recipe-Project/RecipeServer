package com.recipe.app.src.scrap.mapper;

import com.recipe.app.src.scrap.domain.ScrapYoutube;
import com.recipe.app.src.user.domain.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScrapYoutubeRepository extends CrudRepository<ScrapYoutube, Integer> {
    List<ScrapYoutube> findByUserAndStatus(User user, String status);

    List<ScrapYoutube> findByUserAndStatusOrderByCreatedAtDesc(User user, String status);

    Optional<ScrapYoutube> findByYoutubeIdAndUserAndStatus(String youtubeId, User user, String status);

    long countByYoutubeIdAndStatus(String youtubeId, String status);

    long countByUserAndStatus(User user, String status);

}
