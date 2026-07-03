package com.recipe.app.src.recipe.application.event;

/**
 * 레시피 신고가 새로 접수되었을 때 발행되는 이벤트.
 * 실제 알림 전송은 트랜잭션 커밋 이후(AFTER_COMMIT) 리스너에서 처리한다.
 */
public record RecipeReportedEvent(long recipeId, long reporterUserId, long reportCount, boolean reachedThreshold) {
}
