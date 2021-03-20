package com.recipe.app.src.scrapBlog;

import com.recipe.app.src.scrapBlog.models.ScrapBlog;
import com.recipe.app.src.scrapYoutube.models.ScrapYoutube;
import com.recipe.app.src.user.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ScrapBlogRepository extends CrudRepository<ScrapBlog, Integer> {
    ScrapBlog findByUserAndBlogUrlAndStatus(User user, String blogUrl, String status);
    Integer countByBlogUrlAndStatus(String blogUrl, String status);
    //조회순
    @Query(value = "SELECT b From ScrapBlog b LEFT OUTER JOIN ViewBlog v ON b.blogUrl = v.blogUrl WHERE b.user.userIdx = :userIdx AND b.status = :status GROUP BY b.blogUrl ORDER BY count(v.blogUrl) DESC")
    List<ScrapBlog> findByUserAndStatusOrderByViewBlogCount(@Param("userIdx")Integer userIdx, @Param("status")String status);

    //최신순
    List<ScrapBlog> findByUserAndStatusOrderByPostDateDesc(User user, String status);

    //인기순(좋아요순)
    @Query(value = "SELECT b From ScrapBlog b LEFT OUTER JOIN ScrapBlog b2 ON b.blogUrl = b2.blogUrl AND b2.status=:status WHERE b.user.userIdx = :userIdx AND b.status = :status GROUP BY b2.blogUrl ORDER BY count(b2.blogUrl) DESC")
    List<ScrapBlog> findByBlogUrlAndUserAndStatusOrderByScrapBlogCount(@Param("userIdx")Integer userIdx, @Param("status")String status);

}
