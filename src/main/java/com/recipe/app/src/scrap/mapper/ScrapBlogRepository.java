package com.recipe.app.src.scrap.mapper;

import com.recipe.app.src.scrap.domain.ScrapBlog;
import com.recipe.app.src.user.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScrapBlogRepository extends CrudRepository<ScrapBlog, Integer> {
    Optional<ScrapBlog> findByUserAndBlogUrlAndStatus(User user, String blogUrl, String status);

    Integer countByBlogUrlAndStatus(String blogUrl, String status);

    List<ScrapBlog> findByUserAndStatusOrderByCreatedAtDesc(User user, String status);
}
