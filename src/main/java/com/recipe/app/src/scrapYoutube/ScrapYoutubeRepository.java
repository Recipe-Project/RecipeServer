package com.recipe.app.src.scrapYoutube;



import com.recipe.app.src.scrapYoutube.models.ScrapYoutube;
import com.recipe.app.src.user.domain.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ScrapYoutubeRepository extends CrudRepository<ScrapYoutube, Integer> {
    List<ScrapYoutube> findByUserAndStatus(User user, String status);
    List<ScrapYoutube> findByUserAndStatus(User user, String status, Sort sort);
    ScrapYoutube findByYoutubeIdAndUserAndStatus(String youtubeId, User user, String status);
    long countByYoutubeIdAndStatus(String youtubeId,String status);
    long countByUserAndStatus(User user,String status);

}
