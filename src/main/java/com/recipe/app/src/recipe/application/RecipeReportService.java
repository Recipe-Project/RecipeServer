package com.recipe.app.src.recipe.application;

import com.recipe.app.src.recipe.application.event.RecipeReportedEvent;
import com.recipe.app.src.recipe.domain.RecipeReport;
import com.recipe.app.src.recipe.infra.RecipeReportRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RecipeReportService {

    private static final int RECIPE_REPORT_MIN_CNT = 5;
    private final RecipeReportRepository recipeReportRepository;
    private final ApplicationEventPublisher eventPublisher;

    public RecipeReportService(RecipeReportRepository recipeReportRepository,
                               ApplicationEventPublisher eventPublisher) {
        this.recipeReportRepository = recipeReportRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void createRecipeReport(long userId, long recipeId) {

        recipeReportRepository.findByUserIdAndRecipeId(userId, recipeId)
                .ifPresentOrElse(
                        report -> {}, // 이미 신고한 경우: 중복 저장/알림하지 않는다.
                        () -> {
                            recipeReportRepository.save(
                                    RecipeReport.builder()
                                            .userId(userId)
                                            .recipeId(recipeId)
                                            .build());

                            long reportCount = recipeReportRepository.countByRecipeId(recipeId);
                            // 실제 알림 전송은 커밋 이후에 리스너가 처리한다. (롤백 시 유령 알림 방지)
                            eventPublisher.publishEvent(new RecipeReportedEvent(recipeId, userId, reportCount,
                                    reportCount >= RECIPE_REPORT_MIN_CNT));
                        });
    }

    @Transactional(readOnly = true)
    public boolean isRecipeReported(long recipeId) {

        return recipeReportRepository.countByRecipeId(recipeId) >= RECIPE_REPORT_MIN_CNT;
    }

    @Transactional
    public void deleteAllByRecipeId(long recipeId) {

        List<RecipeReport> reports = recipeReportRepository.findByRecipeId(recipeId);

        recipeReportRepository.deleteAll(reports);
    }
}
