package com.recipe.app.src.scrapYoutube;


import com.recipe.app.src.scrapYoutube.models.ScrapYoutube;
import com.recipe.app.src.user.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ScrapYoutubeRepository extends CrudRepository<ScrapYoutube, Integer> {
    Page<ScrapYoutube> findByUserAndStatus(User user, String status, Pageable pageable);
}
