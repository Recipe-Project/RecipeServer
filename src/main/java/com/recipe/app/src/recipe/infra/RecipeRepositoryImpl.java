package com.recipe.app.src.recipe.infra;

import com.recipe.app.src.recipe.application.port.RecipeRepository;
import com.recipe.app.src.recipe.domain.BlogRecipe;
import com.recipe.app.src.recipe.domain.Recipe;
import com.recipe.app.src.recipe.domain.YoutubeRecipe;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.user.infra.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RecipeRepositoryImpl implements RecipeRepository {

    private final RecipeJpaRepository recipeJpaRepository;
    private final RecipeScrapJpaRepository recipeScrapJpaRepository;
    private final RecipeViewJpaRepository recipeViewJpaRepository;
    private final BlogRecipeJpaRepository blogRecipeJpaRepository;
    private final BlogScrapJpaRepository blogScrapJpaRepository;
    private final BlogViewJpaRepository blogViewJpaRepository;
    private final YoutubeRecipeJpaRepository youtubeRecipeJpaRepository;
    private final YoutubeScrapJpaRepository youtubeScrapJpaRepository;
    private final YoutubeViewJpaRepository youtubeViewJpaRepository;

    @Override
    public Optional<Recipe> findById(Long recipeId) {
        return recipeJpaRepository.findById(recipeId).map(RecipeEntity::toModel);
    }

    @Override
    public List<Recipe> getRecipes(String keyword) {
        return recipeJpaRepository.getRecipes(keyword).stream().map(RecipeEntity::toModel).collect(Collectors.toList());
    }

    @Override
    public void saveRecipeScrap(Recipe recipe, User user) {
        recipeScrapJpaRepository.findByUserAndRecipe(UserEntity.fromModel(user), RecipeEntity.fromModel(recipe))
                .orElseGet(() -> recipeScrapJpaRepository.save(RecipeScrapEntity.create(user, recipe)));
    }

    @Override
    public void deleteRecipeScrap(Recipe recipe, User user) {
        recipeScrapJpaRepository.findByUserAndRecipe(UserEntity.fromModel(user), RecipeEntity.fromModel(recipe))
                .ifPresent(recipeScrapJpaRepository::delete);

    }

    @Override
    public List<Recipe> findScrapRecipesByUser(User user) {
        return recipeScrapJpaRepository.findByUser(UserEntity.fromModel(user)).stream()
                .map(RecipeScrapEntity::getRecipe)
                .map(RecipeEntity::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public void saveRecipeView(Recipe recipe, User user) {
        recipeViewJpaRepository.findByUserAndRecipe(UserEntity.fromModel(user), RecipeEntity.fromModel(recipe))
                .orElseGet(() -> recipeViewJpaRepository.save(RecipeViewEntity.create(user, recipe)));
    }

    @Override
    public List<BlogRecipe> getBlogRecipes(String keyword) {
        return blogRecipeJpaRepository.findByTitleContainingOrDescriptionContaining(keyword).stream()
                .map(BlogRecipeEntity::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<BlogRecipe> getBlogRecipe(Long blogRecipeId) {
        return blogRecipeJpaRepository.findById(blogRecipeId).map(BlogRecipeEntity::toModel);
    }

    @Override
    public void saveBlogRecipeView(BlogRecipe blogRecipe, User user) {
        blogViewJpaRepository.findByUserAndBlogRecipe(UserEntity.fromModel(user), BlogRecipeEntity.fromModel(blogRecipe))
                .orElseGet(() -> blogViewJpaRepository.save(BlogViewEntity.create(user, blogRecipe)));
    }

    @Override
    public void saveBlogRecipeScrap(BlogRecipe blogRecipe, User user) {
        blogScrapJpaRepository.findByUserAndBlogRecipe(UserEntity.fromModel(user), BlogRecipeEntity.fromModel(blogRecipe))
                .orElseGet(() -> blogScrapJpaRepository.save(BlogScrapEntity.createBlogScrap(user, blogRecipe)));
    }

    @Override
    public void deleteBlogRecipeScrap(BlogRecipe blogRecipe, User user) {
        blogScrapJpaRepository.findByUserAndBlogRecipe(UserEntity.fromModel(user), BlogRecipeEntity.fromModel(blogRecipe))
                .ifPresent(blogScrapJpaRepository::delete);
    }

    @Override
    public List<BlogRecipe> findBlogRecipesByUser(User user) {
        return blogScrapJpaRepository.findByUser(UserEntity.fromModel(user)).stream()
                .map(BlogScrapEntity::getBlogRecipe)
                .map(BlogRecipeEntity::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<YoutubeRecipe> getYoutubeRecipes(String keyword) {
        return youtubeRecipeJpaRepository.findByTitleContainingOrDescriptionContaining(keyword).stream()
                .map(YoutubeRecipeEntity::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<YoutubeRecipe> getYoutubeRecipe(Long youtubeRecipeId) {
        return youtubeRecipeJpaRepository.findById(youtubeRecipeId).map(YoutubeRecipeEntity::toModel);
    }

    @Override
    public void saveYoutubeRecipeView(YoutubeRecipe youtubeRecipe, User user) {
        youtubeViewJpaRepository.findByUserAndYoutubeRecipe(UserEntity.fromModel(user), YoutubeRecipeEntity.fromModel(youtubeRecipe))
                .orElseGet(() -> youtubeViewJpaRepository.save(YoutubeViewEntity.create(user, youtubeRecipe)));
    }

    @Override
    public void saveYoutubeRecipeScrap(YoutubeRecipe youtubeRecipe, User user) {
        youtubeScrapJpaRepository.findByUserAndYoutubeRecipe(UserEntity.fromModel(user), YoutubeRecipeEntity.fromModel(youtubeRecipe))
                .orElseGet(() -> youtubeScrapJpaRepository.save(YoutubeScrapEntity.create(user, youtubeRecipe)));
    }

    @Override
    public void deleteYoutubeRecipeScrap(YoutubeRecipe youtubeRecipe, User user) {
        youtubeScrapJpaRepository.findByUserAndYoutubeRecipe(UserEntity.fromModel(user), YoutubeRecipeEntity.fromModel(youtubeRecipe))
                .ifPresent(youtubeScrapJpaRepository::delete);

    }

    @Override
    public List<YoutubeRecipe> findYoutubeRecipesByUser(User user) {
        return youtubeScrapJpaRepository.findByUser(UserEntity.fromModel(user)).stream()
                .map(YoutubeScrapEntity::getYoutubeRecipe)
                .map(YoutubeRecipeEntity::toModel)
                .collect(Collectors.toList());
    }
}
