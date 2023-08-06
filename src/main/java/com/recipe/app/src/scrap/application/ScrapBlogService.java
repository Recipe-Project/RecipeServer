package com.recipe.app.src.scrap.application;

import com.recipe.app.src.scrap.application.dto.ScrapBlogDto;
import com.recipe.app.src.scrap.domain.ScrapBlog;
import com.recipe.app.src.scrap.mapper.ScrapBlogRepository;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.user.infra.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScrapBlogService {

    private final ScrapBlogRepository scrapBlogRepository;

    @Transactional
    public void createOrDeleteScrapBlog(User user, ScrapBlogDto.ScrapBlogRequest request) {

        scrapBlogRepository.findByUserAndBlogUrlAndStatus(UserEntity.fromModel(user), request.getBlogUrl(), "ACTIVE")
                .ifPresentOrElse(scrapBlogRepository::delete, () -> {
                    scrapBlogRepository.save(new ScrapBlog(UserEntity.fromModel(user), request.getTitle(), request.getThumbnail(), request.getBlogUrl(), request.getDescription(), request.getBloggerName(), request.getPostDate()));
                });
    }

    public List<ScrapBlog> retrieveScrapBlogs(User user) {
        return scrapBlogRepository.findByUserAndStatusOrderByCreatedAtDesc(UserEntity.fromModel(user), "ACTIVE");
    }

    public int countScrapBlogs(String blogUrl) {
        return scrapBlogRepository.countByBlogUrlAndStatus(blogUrl, "ACTIVE");
    }
}
