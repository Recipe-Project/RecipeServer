package com.recipe.app.src.scrapYoutube;



import com.recipe.app.src.scrapYoutube.models.ScrapYoutube;
import com.recipe.app.src.user.models.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ScrapYoutubeRepository extends CrudRepository<ScrapYoutube, Integer> {
    List<ScrapYoutube> findByUserAndStatus(User user, String status);
    List<ScrapYoutube> findByUserAndStatus(User user, String status, Sort sort);
    ScrapYoutube findByYoutubeIdxAndUserAndStatus(Integer youtubeIdx, User user, String status);
    long countByYoutubeIdxAndStatus(Integer youtubeIdx,String status);
    long countByUserAndStatus(User user,String status);

}
