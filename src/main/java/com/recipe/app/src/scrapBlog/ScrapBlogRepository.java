package com.recipe.app.src.scrapBlog;

import com.recipe.app.src.scrapBlog.models.ScrapBlog;
import com.recipe.app.src.scrapYoutube.models.ScrapYoutube;
import com.recipe.app.src.user.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ScrapBlogRepository extends CrudRepository<ScrapBlog, Integer> {
}
