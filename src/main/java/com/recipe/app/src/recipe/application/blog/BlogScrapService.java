package com.recipe.app.src.recipe.application.blog;

import com.recipe.app.src.recipe.domain.blog.BlogRecipe;
import com.recipe.app.src.recipe.domain.blog.BlogScrap;
import com.recipe.app.src.recipe.infra.blog.BlogScrapRepository;
import com.recipe.app.src.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BlogScrapService {

    private final BlogScrapRepository blogScrapRepository;

    public BlogScrapService(BlogScrapRepository blogScrapRepository) {
        this.blogScrapRepository = blogScrapRepository;
    }

    @Transactional
    public void createBlogScrap(User user, Long blogRecipeId) {

        BlogScrap blogScrap = blogScrapRepository.findByUserIdAndBlogRecipeId(user.getUserId(), blogRecipeId)
                .orElseGet(() -> BlogScrap.builder()
                        .userId(user.getUserId())
                        .blogRecipeId(blogRecipeId)
                        .build());

        blogScrapRepository.save(blogScrap);
    }

    @Transactional
    public void deleteBlogScrap(User user, Long blogRecipeId) {

        blogScrapRepository.findByUserIdAndBlogRecipeId(user.getUserId(), blogRecipeId)
                .ifPresent(blogScrapRepository::delete);
    }

    @Transactional(readOnly = true)
    public Page<BlogScrap> findByUserId(Long userId, int page, int size) {

        return blogScrapRepository.findByUserId(userId, PageRequest.of(page, size));
    }
}
