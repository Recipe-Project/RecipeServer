package com.recipe.app.src.viewYoutube;

import com.recipe.app.src.viewYoutube.models.ViewYoutube;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ViewYoutubeRepository extends CrudRepository<ViewYoutube, Integer> {

}