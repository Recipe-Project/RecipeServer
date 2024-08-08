package com.recipe.app.src.recipe.application.blog;

import com.recipe.app.src.recipe.domain.blog.BlogScrap;
import com.recipe.app.src.recipe.infra.blog.BlogScrapRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
public class BlogScrapService {

    private final BlogScrapRepository blogScrapRepository;

    public BlogScrapService(BlogScrapRepository blogScrapRepository) {
        this.blogScrapRepository = blogScrapRepository;
    }

    @Transactional
    public void create(long userId, long blogRecipeId) {

        blogScrapRepository.findByUserIdAndBlogRecipeId(userId, blogRecipeId)
                .orElseGet(() -> blogScrapRepository.save(BlogScrap.builder()
                        .userId(userId)
                        .blogRecipeId(blogRecipeId)
                        .build()));
    }

    @Transactional
    public void delete(long userId, long blogRecipeId) {

        blogScrapRepository.findByUserIdAndBlogRecipeId(userId, blogRecipeId)
                .ifPresent(blogScrapRepository::delete);
    }

    @Transactional
    public void deleteAllByUserId(long userId) {

        List<BlogScrap> blogScraps = blogScrapRepository.findByUserId(userId);

        blogScrapRepository.deleteAll(blogScraps);
    }

    @Transactional(readOnly = true)
    public long countByUserId(long userId) {

        return blogScrapRepository.countByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<BlogScrap> findByBlogRecipeIds(Collection<Long> blogRecipeIds) {

        return blogScrapRepository.findByBlogRecipeIdIn(blogRecipeIds);
    }

    @Transactional(readOnly = true)
    public long countByBlogRecipeId(long blogRecipeId) {

        return blogScrapRepository.countByBlogRecipeId(blogRecipeId);
    }

    @Transactional(readOnly = true)
    public BlogScrap findByUserIdAndBlogRecipeId(long userId, long blogRecipeId) {

        return blogScrapRepository.findByUserIdAndBlogRecipeId(userId, blogRecipeId).orElse(null);
    }
}
