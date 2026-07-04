package com.recipe.app.src.common.utils;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 스캔성(존재하지 않는 경로 / 방화벽 차단) 요청을 집계해, 일정 시간 안에 임계치를 넘으면
 * Discord 로 "급증" 알림을 1회 보낸다. 개별 요청마다 알림을 보내지 않으므로 채널이 노이즈로 덮이지 않는다.
 *
 * 스케줄러 없이 요청 스레드에서 슬라이딩(tumbling) 윈도우를 갱신하며, 재알림은 쿨다운으로 억제한다.
 */
@Component
public class ScanAttackMonitor {

    private static final long WINDOW_MS = 5 * 60 * 1000L;    // 집계 윈도우 5분
    private static final int THRESHOLD = 100;                // 윈도우당 임계치
    private static final long COOLDOWN_MS = 10 * 60 * 1000L; // 재알림 최소 간격 10분
    private static final int TOP_IP_COUNT = 5;

    // DiscordAlertService 는 webhook 미설정/비활성 환경에서 없을 수 있으므로 ObjectProvider 로 안전하게 주입한다.
    private final ObjectProvider<DiscordAlertService> discordAlertServiceProvider;

    private long windowStart = System.currentTimeMillis();
    private int count = 0;
    private final Map<String, Integer> ipCounts = new HashMap<>();
    private long lastAlertAt = 0L;

    public ScanAttackMonitor(ObjectProvider<DiscordAlertService> discordAlertServiceProvider) {
        this.discordAlertServiceProvider = discordAlertServiceProvider;
    }

    public synchronized void record(String clientIp) {
        long now = System.currentTimeMillis();

        // 윈도우가 지났으면 초기화
        if (now - windowStart > WINDOW_MS) {
            windowStart = now;
            count = 0;
            ipCounts.clear();
        }

        count++;
        ipCounts.merge(clientIp != null ? clientIp : "unknown", 1, Integer::sum);

        if (count >= THRESHOLD && now - lastAlertAt > COOLDOWN_MS) {
            lastAlertAt = now;
            DiscordAlertService discordAlertService = discordAlertServiceProvider.getIfAvailable();
            if (discordAlertService != null) {
                discordAlertService.sendScanSurgeAlert(count, WINDOW_MS / 60000, topIps());
            }
        }
    }

    private List<String> topIps() {
        return ipCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(TOP_IP_COUNT)
                .map(e -> e.getKey() + " (" + e.getValue() + "건)")
                .collect(Collectors.toList());
    }
}
