package com.recipe.app.src.recipe.application;

import com.recipe.app.src.recipe.domain.RecipeScrap;
import com.recipe.app.src.recipe.infra.RecipeScrapRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
public class RecipeScrapService {

    private final RecipeScrapRepository recipeScrapRepository;

    public RecipeScrapService(RecipeScrapRepository recipeScrapRepository) {
        this.recipeScrapRepository = recipeScrapRepository;
    }

    @Transactional
    public void createRecipeScrap(long userId, long recipeId) {

        recipeScrapRepository.findByUserIdAndRecipeId(userId, recipeId)
                .orElseGet(() -> recipeScrapRepository.save(
                        RecipeScrap.builder()
                                .userId(userId)
                                .recipeId(recipeId)
                                .build()));
    }

    @Transactional
    public void deleteRecipeScrap(long userId, long recipeId) {

        recipeScrapRepository.findByUserIdAndRecipeId(userId, recipeId)
                .ifPresent(recipeScrapRepository::delete);
    }

    @Transactional
    public void deleteAllByRecipeId(Long recipeId) {

        List<RecipeScrap> recipeScraps = recipeScrapRepository.findByRecipeId(recipeId);

        recipeScrapRepository.deleteAll(recipeScraps);
    }

    @Transactional(readOnly = true)
    public long countByUserId(Long userId) {
        return recipeScrapRepository.countByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<RecipeScrap> findByRecipeIds(Collection<Long> recipeIds) {

        return recipeScrapRepository.findByRecipeIdIn(recipeIds);
    }

    @Transactional(readOnly = true)
    public long countByRecipeId(long recipeId) {

        return recipeScrapRepository.countByRecipeId(recipeId);
    }

    @Transactional(readOnly = true)
    public RecipeScrap findByUserIdAndRecipeId(long userId, long recipeId) {

        return recipeScrapRepository.findByUserIdAndRecipeId(userId, recipeId).orElse(null);
    }

    @Transactional(readOnly = true)
    public boolean existsByUserIdAndRecipeId(long userId, long recipeId) {

        return recipeScrapRepository.existsByUserIdAndRecipeId(userId, recipeId);
    }

    @Transactional
    public void deleteAllByUserId(long userId) {

        List<RecipeScrap> recipeScraps = recipeScrapRepository.findByUserId(userId);

        recipeScrapRepository.deleteAll(recipeScraps);
    }
}
