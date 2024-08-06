package com.recipe.app.src.recipe.application;

import com.recipe.app.src.recipe.domain.Recipe;
import com.recipe.app.src.recipe.domain.RecipeScrap;
import com.recipe.app.src.recipe.exception.NotFoundScrapException;
import com.recipe.app.src.recipe.infra.RecipeScrapRepository;
import com.recipe.app.src.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecipeScrapService {

    private final RecipeScrapRepository recipeScrapRepository;

    public RecipeScrapService(RecipeScrapRepository recipeScrapRepository) {
        this.recipeScrapRepository = recipeScrapRepository;
    }

    @Transactional
    public void createRecipeScrap(long userId, long recipeId) {

        RecipeScrap recipeScrap = recipeScrapRepository.findByUserIdAndRecipeId(userId, recipeId)
                .orElseGet(() -> RecipeScrap.builder()
                        .userId(userId)
                        .recipeId(recipeId)
                        .build());

        recipeScrapRepository.save(recipeScrap);
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

    public List<RecipeScrap> findByRecipeIds(Collection<Recipe> recipes) {

        List<Long> recipeIds = recipes.stream()
                .map(Recipe::getRecipeId)
                .collect(Collectors.toList());

        return recipeScrapRepository.findByRecipeIdIn(recipeIds);
    }

    @Transactional(readOnly = true)
    public long countByRecipeId(Long recipeId) {

        return recipeScrapRepository.countByRecipeId(recipeId);
    }

    @Transactional(readOnly = true)
    public RecipeScrap findByUserIdAndRecipeId(Long userId, Long recipeId) {

        return recipeScrapRepository.findByUserIdAndRecipeId(userId, recipeId).orElseThrow(() -> {
            throw new NotFoundScrapException();
        });
    }

    @Transactional(readOnly = true)
    public boolean existsByUserIdAndRecipeId(Long userId, Long recipeId) {

        return recipeScrapRepository.existsByUserIdAndRecipeId(userId, recipeId);
    }

    @Transactional
    public void deleteAllByUser(User user) {

        List<RecipeScrap> recipeScraps = recipeScrapRepository.findByUserId(user.getUserId());

        recipeScrapRepository.deleteAll(recipeScraps);
    }
}
