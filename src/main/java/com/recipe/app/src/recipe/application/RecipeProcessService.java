package com.recipe.app.src.recipe.application;

import com.recipe.app.src.recipe.application.dto.RecipeProcessResponse;
import com.recipe.app.src.recipe.domain.RecipeProcess;
import com.recipe.app.src.recipe.infra.RecipeProcessRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecipeProcessService {

    private final RecipeProcessRepository recipeProcessRepository;

    public RecipeProcessService(RecipeProcessRepository recipeProcessRepository) {
        this.recipeProcessRepository = recipeProcessRepository;
    }

    @Transactional
    public void deleteAllByRecipeIds(Collection<Long> recipeIds) {

        List<RecipeProcess> recipeProcesses = recipeProcessRepository.findByRecipeIdIn(recipeIds);

        recipeProcessRepository.deleteAll(recipeProcesses);
    }

    @Transactional
    public void createRecipeProcesses(Long recipeId, String content) {

        RecipeProcess recipeProcess = RecipeProcess.builder()
                .recipeId(recipeId)
                .cookingNo(1)
                .cookingDescription(content)
                .build();

        recipeProcessRepository.save(recipeProcess);
    }

    @Transactional
    public void deleteAllByRecipeId(Long recipeId) {

        recipeProcessRepository.deleteAll(findByRecipeId(recipeId));
    }

    @Transactional(readOnly = true)
    public List<RecipeProcessResponse> fineRecipeProcessesByRecipeId(Long recipeId) {

        return findByRecipeId(recipeId).stream()
                .map(RecipeProcessResponse::from)
                .collect(Collectors.toList());
    }

    private List<RecipeProcess> findByRecipeId(Long recipeId) {

        return recipeProcessRepository.findByRecipeId(recipeId);
    }
}
