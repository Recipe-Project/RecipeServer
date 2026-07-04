package com.recipe.app.src.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

/**
 * 서버 이벤트(500 에러, 레시피 신고)를 Discord 채널로 알림 전송한다.
 * 운영(prod) 프로필에서만 빈으로 등록되며, 요청 스레드를 막지 않도록 비동기(enqueue)로 전송한다.
 */
@Component
@Profile("prod")
@RequiredArgsConstructor
public class DiscordAlertService {

    private static final Logger log = LoggerFactory.getLogger(DiscordAlertService.class);
    private static final int RED = 15158332;
    private static final int ORANGE = 15105570;
    private static final int MAX_STACKTRACE_LENGTH = 1000; // Discord embed field value 제한(1024) 대응

    @Value("${discord.webhook.url:}")
    private String errorWebhookUrl;

    @Value("${discord.webhook.report-url:}")
    private String reportWebhookUrl;

    private final ObjectMapper objectMapper;
    private final OkHttpClient client = new OkHttpClient();

    /**
     * 처리되지 않은 예외(500) 알림.
     */
    public void sendErrorAlert(String httpMethod, String requestUri, String clientIp, Throwable throwable) {
        Map<String, Object> embed = Map.of(
                "title", "🚨 500 Internal Server Error",
                "color", RED,
                "fields", List.of(
                        field("Endpoint", httpMethod + " " + requestUri),
                        field("Client IP", clientIp),
                        field("Exception", throwable.getClass().getSimpleName()),
                        field("Message", truncate(throwable.getMessage())),
                        field("Stacktrace", "```" + stackTrace(throwable) + "```")
                )
        );
        send(errorWebhookUrl, embed);
    }

    /**
     * 레시피 신고 접수 알림.
     *
     * @param reachedThreshold 누적 신고가 숨김 처리 기준 이상인지 여부
     */
    public void sendReportAlert(long recipeId, long reporterUserId, long reportCount, boolean reachedThreshold) {
        Map<String, Object> embed = Map.of(
                "title", reachedThreshold ? "⛔ 레시피 신고 (기준 초과)" : "⚠️ 레시피 신고 접수",
                "color", reachedThreshold ? RED : ORANGE,
                "fields", List.of(
                        field("Recipe ID", String.valueOf(recipeId)),
                        field("신고자 User ID", String.valueOf(reporterUserId)),
                        field("누적 신고 수", reportCount + "회" + (reachedThreshold ? " (기준 초과 · 숨김 처리 대상)" : ""))
                )
        );
        send(reportWebhookUrl, embed);
    }

    /**
     * 스캔성 요청 급증 감지 알림 (개별 요청이 아니라 집계 결과 1회 전송).
     * 드물게 발생하므로 별도 채널 없이 500 에러 채널로 보내되 제목/색으로 구분한다.
     */
    public void sendScanSurgeAlert(int count, long windowMinutes, List<String> topIps) {
        Map<String, Object> embed = Map.of(
                "title", "⚠️ 스캔성 요청 급증 감지",
                "color", ORANGE,
                "fields", List.of(
                        field("감지", "최근 " + windowMinutes + "분간 " + count + "건 (임계치 초과)"),
                        field("Top IP", topIps.isEmpty() ? "(none)" : String.join("\n", topIps))
                )
        );
        send(errorWebhookUrl, embed);
    }

    private void send(String webhookUrl, Map<String, Object> embed) {
        if (!StringUtils.hasText(webhookUrl)) {
            log.warn("Discord webhook URL 이 설정되지 않아 알림을 건너뜁니다.");
            return;
        }

        try {
            String payload = objectMapper.writeValueAsString(Map.of("embeds", List.of(embed)));
            RequestBody body = RequestBody.create(payload, MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url(webhookUrl)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    log.error("Discord 알림 전송 실패", e);
                }

                @Override
                public void onResponse(Call call, Response response) {
                    try (response) {
                        if (!response.isSuccessful()) {
                            log.error("Discord 알림 전송 거절. status={}", response.code());
                        }
                    }
                }
            });
        } catch (Exception e) {
            // 알림 전송 실패가 원본 요청 처리를 방해해서는 안 된다.
            log.error("Discord 알림 페이로드 생성 실패", e);
        }
    }

    private Map<String, Object> field(String name, String value) {
        return Map.of("name", name, "value", truncate(value), "inline", false);
    }

    private String stackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        return truncate(sw.toString(), MAX_STACKTRACE_LENGTH);
    }

    private String truncate(String value) {
        return truncate(value, 1000);
    }

    private String truncate(String value, int maxLength) {
        if (!StringUtils.hasText(value)) {
            return "(none)";
        }
        return value.length() > maxLength ? value.substring(0, maxLength) + "..." : value;
    }
}
