package com.recipe.app.src.recipe.infra.youtube;

import com.recipe.app.src.recipe.application.port.YoutubeRecipeRepository;
import com.recipe.app.src.recipe.domain.YoutubeRecipe;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.user.infra.UserEntity;
import lombok.RequiredArgsConstructor;
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
    public List<YoutubeRecipe> getYoutubeRecipes(String keyword) {
        return youtubeRecipeJpaRepository.findByTitleContainingOrDescriptionContaining(keyword, keyword).stream()
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
        return youtubeScrapJpaRepository.countByUser(UserEntity.fromModel(user));
    }
}
