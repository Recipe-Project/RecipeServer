package com.recipe.app.src.recipe.application;

import com.recipe.app.src.common.utils.DiscordAlertService;
import com.recipe.app.src.recipe.application.event.RecipeReportedEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 레시피 신고 이벤트를 받아 Discord 알림을 전송한다.
 * 트랜잭션이 실제로 커밋된 뒤에만 동작하므로 롤백 시 유령 알림이 발생하지 않는다.
 * DiscordAlertService 와 동일하게 prod 프로필에서만 등록된다. (다른 프로필에서는 이벤트가 무시됨)
 */
@Component
@Profile("prod")
public class RecipeReportAlertListener {

    private final DiscordAlertService discordAlertService;

    public RecipeReportAlertListener(DiscordAlertService discordAlertService) {
        this.discordAlertService = discordAlertService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleRecipeReported(RecipeReportedEvent event) {
        discordAlertService.sendReportAlert(event.recipeId(), event.reporterUserId(),
                event.reportCount(), event.reachedThreshold());
    }
}
