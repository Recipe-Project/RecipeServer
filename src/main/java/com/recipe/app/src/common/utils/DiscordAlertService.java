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
 * 처리되지 않은 예외(500)를 Discord 채널로 알림 전송한다.
 * 운영(prod) 프로필에서만 빈으로 등록되며, 요청 스레드를 막지 않도록 비동기(enqueue)로 전송한다.
 */
@Component
@Profile("prod")
@RequiredArgsConstructor
public class DiscordAlertService {

    private static final Logger log = LoggerFactory.getLogger(DiscordAlertService.class);
    private static final int RED = 15158332;
    private static final int MAX_STACKTRACE_LENGTH = 1000; // Discord embed field value 제한(1024) 대응

    @Value("${discord.webhook.url:}")
    private String webhookUrl;

    private final ObjectMapper objectMapper;
    private final OkHttpClient client = new OkHttpClient();

    public void sendErrorAlert(String httpMethod, String requestUri, Throwable throwable) {
        if (!StringUtils.hasText(webhookUrl)) {
            log.warn("Discord webhook URL 이 설정되지 않아 알림을 건너뜁니다.");
            return;
        }

        try {
            String payload = makePayload(httpMethod, requestUri, throwable);
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

    private String makePayload(String httpMethod, String requestUri, Throwable throwable) throws IOException {
        Map<String, Object> embed = Map.of(
                "title", "🚨 500 Internal Server Error",
                "color", RED,
                "fields", List.of(
                        Map.of("name", "Endpoint", "value", httpMethod + " " + requestUri, "inline", false),
                        Map.of("name", "Exception", "value", throwable.getClass().getSimpleName(), "inline", false),
                        Map.of("name", "Message", "value", truncate(throwable.getMessage()), "inline", false),
                        Map.of("name", "Stacktrace", "value", "```" + stackTrace(throwable) + "```", "inline", false)
                )
        );
        return objectMapper.writeValueAsString(Map.of("embeds", List.of(embed)));
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
