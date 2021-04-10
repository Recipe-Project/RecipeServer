package com.recipe.app.src.keywordYoutube;

import com.recipe.app.src.keywordYoutube.models.KeywordYoutube;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface KeywordYoutubeRepository extends CrudRepository<KeywordYoutube, Integer> {

}
