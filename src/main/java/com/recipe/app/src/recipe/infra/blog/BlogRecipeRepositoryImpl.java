package com.recipe.app.src.recipe.infra.blog;

import com.recipe.app.src.recipe.application.port.BlogRecipeRepository;
import com.recipe.app.src.recipe.domain.BlogRecipe;
import com.recipe.app.src.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Repository
@RequiredArgsConstructor
public class BlogRecipeRepositoryImpl implements BlogRecipeRepository {
    private final BlogRecipeJpaRepository blogRecipeJpaRepository;
    private final BlogScrapJpaRepository blogScrapJpaRepository;
    private final BlogViewJpaRepository blogViewJpaRepository;

    @Override
    public Page<BlogRecipe> getBlogRecipesOrderByCreatedAtDesc(String keyword, Pageable pageable) {
        return blogRecipeJpaRepository.findByTitleContainingOrDescriptionContainingOrderByCreatedAtDesc(keyword, keyword, pageable).map(BlogRecipeEntity::toModel);
    }

    @Override
    public Page<BlogRecipe> getBlogRecipesOrderByBlogScrapSizeDesc(String keyword, Pageable pageable) {
        return blogRecipeJpaRepository.findByTitleContainingOrDescriptionContainingOrderByBlogScrapSizeDesc(keyword, keyword, pageable).map(BlogRecipeEntity::toModel);
    }

    @Override
    public Page<BlogRecipe> getBlogRecipesOrderByBlogViewSizeDesc(String keyword, Pageable pageable) {
        return blogRecipeJpaRepository.findByTitleContainingOrDescriptionContainingOrderByBlogViewSizeDesc(keyword, keyword, pageable).map(BlogRecipeEntity::toModel);
    }

    @Override
    public Optional<BlogRecipe> getBlogRecipe(Long blogRecipeId) {
        return blogRecipeJpaRepository.findById(blogRecipeId).map(BlogRecipeEntity::toModel);
    }

    @Override
    public void saveBlogRecipeView(BlogRecipe blogRecipe, User user) {
        blogViewJpaRepository.findByUserAndBlogRecipe(User.fromModel(user), BlogRecipeEntity.fromModel(blogRecipe))
                .orElseGet(() -> blogViewJpaRepository.save(BlogViewEntity.create(user, blogRecipe)));
    }

    @Override
    public void saveBlogRecipeScrap(BlogRecipe blogRecipe, User user) {
        blogScrapJpaRepository.findByUserAndBlogRecipe(User.fromModel(user), BlogRecipeEntity.fromModel(blogRecipe))
                .orElseGet(() -> blogScrapJpaRepository.save(BlogScrapEntity.createBlogScrap(user, blogRecipe)));
    }

    @Override
    public void deleteBlogRecipeScrap(BlogRecipe blogRecipe, User user) {
        blogScrapJpaRepository.findByUserAndBlogRecipe(User.fromModel(user), BlogRecipeEntity.fromModel(blogRecipe))
                .ifPresent(blogScrapJpaRepository::delete);
    }

    @Override
    public Page<BlogRecipe> findBlogRecipesByUser(User user, Pageable pageable) {
        return blogScrapJpaRepository.findByUser(User.fromModel(user), pageable)
                .map(BlogScrapEntity::getBlogRecipe)
                .map(BlogRecipeEntity::toModel);
    }

    @Override
    public List<BlogRecipe> saveBlogRecipes(List<BlogRecipe> blogs) {
        return StreamSupport.stream(blogRecipeJpaRepository.saveAll(blogs.stream()
                        .map(BlogRecipeEntity::fromModel)
                        .collect(Collectors.toList())).spliterator(), false)
                .map(BlogRecipeEntity::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public long countBlogScrapByUser(User user) {
        return blogScrapJpaRepository.countByUser(User.fromModel(user));
    }

    @Override
    public List<BlogRecipe> findBlogRecipesByBlogUrlIn(List<String> blogUrls) {
        return blogRecipeJpaRepository.findByBlogUrlIn(blogUrls).stream()
                .map(BlogRecipeEntity::toModel)
                .collect(Collectors.toList());
    }
}
