package com.recipe.app.src.scrap.mapper;

import com.recipe.app.src.scrap.domain.ScrapYoutube;
import com.recipe.app.src.user.infra.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScrapYoutubeRepository extends CrudRepository<ScrapYoutube, Integer> {
    List<ScrapYoutube> findByUserAndStatus(UserEntity user, String status);

    List<ScrapYoutube> findByUserAndStatusOrderByCreatedAtDesc(UserEntity user, String status);

    Optional<ScrapYoutube> findByYoutubeIdAndUserAndStatus(String youtubeId, UserEntity user, String status);

    long countByYoutubeIdAndStatus(String youtubeId, String status);

    long countByUserAndStatus(UserEntity user, String status);

}
