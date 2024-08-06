package com.recipe.app.src.recipe.application;

import com.recipe.app.src.recipe.domain.RecipeView;
import com.recipe.app.src.recipe.infra.RecipeViewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RecipeViewService {

    private final RecipeViewRepository recipeViewRepository;

    public RecipeViewService(RecipeViewRepository recipeViewRepository) {
        this.recipeViewRepository = recipeViewRepository;
    }

    @Transactional
    public void createRecipeView(long userId, long recipeId) {

        RecipeView recipeView = recipeViewRepository.findByUserIdAndRecipeId(userId, recipeId)
                .orElseGet(() -> RecipeView.builder()
                        .userId(userId)
                        .recipeId(recipeId)
                        .build());

        recipeViewRepository.save(recipeView);
    }

    @Transactional
    public void deleteAllByRecipeId(long recipeId) {

        recipeViewRepository.deleteAll(findByRecipeId(recipeId));
    }

    @Transactional(readOnly = true)
    public List<RecipeView> findByRecipeId(long recipeId) {

        return recipeViewRepository.findByRecipeId(recipeId);
    }

    @Transactional(readOnly = true)
    public long countByRecipeId(long recipeId) {

        return recipeViewRepository.countByRecipeId(recipeId);
    }

    @Transactional
    public void deleteAllByUserId(long userId) {

        List<RecipeView> recipeViews = recipeViewRepository.findByUserId(userId);

        recipeViewRepository.deleteAll(recipeViews);
    }
}
