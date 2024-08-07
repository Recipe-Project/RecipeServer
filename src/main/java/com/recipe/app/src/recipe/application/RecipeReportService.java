package com.recipe.app.src.recipe.application;

import com.recipe.app.src.recipe.domain.RecipeReport;
import com.recipe.app.src.recipe.infra.RecipeReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RecipeReportService {

    private final RecipeReportRepository recipeReportRepository;

    public RecipeReportService(RecipeReportRepository recipeReportRepository) {
        this.recipeReportRepository = recipeReportRepository;
    }

    @Transactional
    public void createRecipeReport(long userId, long recipeId) {

        RecipeReport recipeReport = recipeReportRepository.findByUserIdAndRecipeId(userId, recipeId)
                .orElseGet(() -> RecipeReport.builder()
                        .userId(userId)
                        .recipeId(recipeId)
                        .build());

        recipeReportRepository.save(recipeReport);
    }

    @Transactional(readOnly = true)
    public boolean isRecipeReported(long recipeId) {

        return recipeReportRepository.countByRecipeId(recipeId) >= 5;
    }

    @Transactional
    public void deleteAllByRecipeId(long recipeId) {

        List<RecipeReport> reports = recipeReportRepository.findByRecipeId(recipeId);

        recipeReportRepository.deleteAll(reports);
    }
}
