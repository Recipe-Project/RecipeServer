package com.recipe.app.src.recipe.application.blog;

import com.recipe.app.src.recipe.domain.blog.BlogRecipe;
import com.recipe.app.src.recipe.domain.blog.BlogView;
import com.recipe.app.src.recipe.infra.blog.BlogViewRepository;
import com.recipe.app.src.user.domain.User;
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
    public void createBlogView(User user, Long blogRecipeId) {

        BlogView blogView = blogViewRepository.findByUserIdAndBlogRecipeId(user.getUserId(), blogRecipeId)
                .orElseGet(() -> BlogView.builder()
                        .userId(user.getUserId())
                        .blogRecipeId(blogRecipeId)
                        .build());

        blogViewRepository.save(blogView);
    }

    @Transactional
    public void deleteBlogRecipeViewByUser(User user) {

        List<BlogView> blogViews = blogViewRepository.findByUserId(user.getUserId());
        blogViewRepository.deleteAll(blogViews);
    }
}
