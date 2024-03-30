package com.recipe.app.src.recipe.application;

import com.recipe.app.src.recipe.domain.RecipeScrap;
import com.recipe.app.src.recipe.infra.RecipeScrapRepository;
import com.recipe.app.src.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    public void createRecipeScrap(User user, Long recipeId) {

        RecipeScrap recipeScrap = recipeScrapRepository.findByUserIdAndRecipeId(user.getUserId(), recipeId)
                .orElseGet(() -> RecipeScrap.builder()
                        .userId(user.getUserId())
                        .recipeId(recipeId)
                        .build());

        recipeScrapRepository.save(recipeScrap);
    }

    @Transactional
    public void deleteRecipeScrap(User user, Long recipeId) {

        recipeScrapRepository.findByUserIdAndRecipeId(user.getUserId(), recipeId)
                .ifPresent(recipeScrapRepository::delete);
    }

    @Transactional
    public void deleteAllByRecipeId(Long recipeId) {

        List<RecipeScrap> recipeScraps = recipeScrapRepository.findByRecipeId(recipeId);

        recipeScrapRepository.deleteAll(recipeScraps);
    }

    @Transactional(readOnly = true)
    public Page<RecipeScrap> findByUserId(Long userId, int page, int size) {

        return recipeScrapRepository.findByUserId(userId, PageRequest.of(page, size));
    }

    @Transactional(readOnly = true)
    public long countByUserId(Long userId) {

        return recipeScrapRepository.countByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<RecipeScrap> findByRecipeId(Long recipeId) {

        return recipeScrapRepository.findByRecipeId(recipeId);
    }

    @Transactional(readOnly = true)
    public List<RecipeScrap> findByRecipeIds(Collection<Long> recipeIds) {

        return recipeScrapRepository.findByRecipeIdIn(recipeIds);
    }
}
