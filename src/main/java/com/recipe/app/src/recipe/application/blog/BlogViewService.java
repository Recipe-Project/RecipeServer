package com.recipe.app.src.recipe.application.blog;

import com.recipe.app.src.recipe.domain.blog.BlogView;
import com.recipe.app.src.recipe.infra.blog.BlogViewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BlogViewService {

    private final BlogViewRepository blogViewRepository;

    public BlogViewService(BlogViewRepository blogViewRepository) {
        this.blogViewRepository = blogViewRepository;
    }

    @Transactional
    public void createBlogView(long userId, long blogRecipeId) {

        blogViewRepository.findByUserIdAndBlogRecipeId(userId, blogRecipeId)
                .orElseGet(() -> blogViewRepository.save(BlogView.builder()
                        .userId(userId)
                        .blogRecipeId(blogRecipeId)
                        .build()));
    }

    @Transactional
    public void deleteBlogRecipeViewByUserId(long userId) {

        List<BlogView> blogViews = blogViewRepository.findByUserId(userId);

        blogViewRepository.deleteAll(blogViews);
    }

    @Transactional(readOnly = true)
    public long countByBlogRecipeId(long blogRecipeId) {

        return blogViewRepository.countByBlogRecipeId(blogRecipeId);
    }
}
