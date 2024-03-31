package com.recipe.app.src.recipe.application;

import com.recipe.app.src.recipe.domain.RecipeView;
import com.recipe.app.src.recipe.infra.RecipeViewRepository;
import com.recipe.app.src.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
public class RecipeViewService {

    private final RecipeViewRepository recipeViewRepository;

    public RecipeViewService(RecipeViewRepository recipeViewRepository) {
        this.recipeViewRepository = recipeViewRepository;
    }

    @Transactional
    public void createRecipeView(User user, Long recipeId) {

        RecipeView recipeView = recipeViewRepository.findByUserIdAndRecipeId(user.getUserId(), recipeId)
                .orElseGet(() -> RecipeView.builder()
                        .userId(user.getUserId())
                        .recipeId(recipeId)
                        .build());

        recipeViewRepository.save(recipeView);
    }

    @Transactional
    public void deleteAllByRecipeId(Long recipeId) {

        recipeViewRepository.deleteAll(findByRecipeId(recipeId));
    }

    @Transactional(readOnly = true)
    public List<RecipeView> findByRecipeId(Long recipeId) {

        return recipeViewRepository.findByRecipeId(recipeId);
    }

    @Transactional(readOnly = true)
    public List<RecipeView> findByRecipeIds(Collection<Long> recipeIds) {

        return recipeViewRepository.findByRecipeIdIn(recipeIds);
    }

    @Transactional(readOnly = true)
    public long countByRecipeId(Long recipeId) {

        return recipeViewRepository.countByRecipeId(recipeId);
    }
}
