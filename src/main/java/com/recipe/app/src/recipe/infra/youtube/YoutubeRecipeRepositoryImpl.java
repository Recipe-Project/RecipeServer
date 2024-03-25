package com.recipe.app.src.recipe.infra.youtube;

import com.recipe.app.src.recipe.application.port.YoutubeRecipeRepository;
import com.recipe.app.src.recipe.domain.YoutubeRecipe;
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
public class YoutubeRecipeRepositoryImpl implements YoutubeRecipeRepository {

    private final YoutubeRecipeJpaRepository youtubeRecipeJpaRepository;
    private final YoutubeScrapJpaRepository youtubeScrapJpaRepository;
    private final YoutubeViewJpaRepository youtubeViewJpaRepository;


    @Override
    public Page<YoutubeRecipe> getYoutubeRecipesOrderByCreatedAtDesc(String keyword, Pageable pageable) {
        return youtubeRecipeJpaRepository.findByTitleContainingOrDescriptionContainingOrderByCreatedAtDesc(keyword, keyword, pageable).map(YoutubeRecipeEntity::toModel);
    }

    @Override
    public Page<YoutubeRecipe> getYoutubeRecipesOrderByYoutubeScrapSizeDesc(String keyword, Pageable pageable) {
        return youtubeRecipeJpaRepository.findByTitleContainingOrDescriptionContainingOrderByYoutubeScrapSizeDesc(keyword, keyword, pageable).map(YoutubeRecipeEntity::toModel);
    }

    @Override
    public Page<YoutubeRecipe> getYoutubeRecipesOrderByYoutubeViewSizeDesc(String keyword, Pageable pageable) {
        return youtubeRecipeJpaRepository.findByTitleContainingOrDescriptionContainingOrderByYoutubeViewSizeDesc(keyword, keyword, pageable).map(YoutubeRecipeEntity::toModel);
    }

    @Override
    public Optional<YoutubeRecipe> getYoutubeRecipe(Long youtubeRecipeId) {
        return youtubeRecipeJpaRepository.findById(youtubeRecipeId).map(YoutubeRecipeEntity::toModel);
    }

    @Override
    public void saveYoutubeRecipeView(YoutubeRecipe youtubeRecipe, User user) {
        youtubeViewJpaRepository.findByUserAndYoutubeRecipe(User.fromModel(user), YoutubeRecipeEntity.fromModel(youtubeRecipe))
                .orElseGet(() -> youtubeViewJpaRepository.save(YoutubeViewEntity.create(user, youtubeRecipe)));
    }

    @Override
    public void saveYoutubeRecipeScrap(YoutubeRecipe youtubeRecipe, User user) {
        youtubeScrapJpaRepository.findByUserAndYoutubeRecipe(User.fromModel(user), YoutubeRecipeEntity.fromModel(youtubeRecipe))
                .orElseGet(() -> youtubeScrapJpaRepository.save(YoutubeScrapEntity.create(user, youtubeRecipe)));
    }

    @Override
    public void deleteYoutubeRecipeScrap(YoutubeRecipe youtubeRecipe, User user) {
        youtubeScrapJpaRepository.findByUserAndYoutubeRecipe(User.fromModel(user), YoutubeRecipeEntity.fromModel(youtubeRecipe))
                .ifPresent(youtubeScrapJpaRepository::delete);

    }

    @Override
    public Page<YoutubeRecipe> findYoutubeRecipesByUser(User user, Pageable pageable) {
        return youtubeScrapJpaRepository.findByUser(User.fromModel(user), pageable)
                .map(YoutubeScrapEntity::getYoutubeRecipe)
                .map(YoutubeRecipeEntity::toModel);
    }

    @Override
    public List<YoutubeRecipe> saveYoutubeRecipes(List<YoutubeRecipe> youtubeRecipes) {
        return StreamSupport.stream(youtubeRecipeJpaRepository.saveAll(youtubeRecipes.stream()
                        .map(YoutubeRecipeEntity::fromModel)
                        .collect(Collectors.toList())).spliterator(), false)
                .map(YoutubeRecipeEntity::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public long countYoutubeScrapByUser(User user) {
        return youtubeScrapJpaRepository.countByUser(User.fromModel(user));
    }

    @Override
    public List<YoutubeRecipe> findYoutubeRecipesByYoutubeIdIn(List<String> youtubeIds) {
        return youtubeRecipeJpaRepository.findByYoutubeIdIn(youtubeIds).stream()
                .map(YoutubeRecipeEntity::toModel)
                .collect(Collectors.toList());
    }
}
