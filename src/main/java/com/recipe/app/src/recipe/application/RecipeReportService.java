package com.recipe.app.src.recipe.application;

import com.recipe.app.src.recipe.domain.RecipeReport;
import com.recipe.app.src.recipe.infra.RecipeReportRepository;
import com.recipe.app.src.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecipeReportService {

    private final RecipeReportRepository recipeReportRepository;

    public RecipeReportService(RecipeReportRepository recipeReportRepository) {
        this.recipeReportRepository = recipeReportRepository;
    }

    @Transactional
    public void createRecipeReport(User user, Long recipeId) {

        RecipeReport recipeReport = recipeReportRepository.findByUserIdAndRecipeId(user.getUserId(), recipeId)
                .orElseGet(() -> RecipeReport.builder()
                        .userId(user.getUserId())
                        .recipeId(recipeId)
                        .build());

        recipeReportRepository.save(recipeReport);
    }

    @Transactional(readOnly = true)
    public boolean isRecipeReported(Long recipeId) {

        return recipeReportRepository.countByRecipeId(recipeId) >= 5;
    }
}
