package com.recipe.app.src.fridge.domain;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public enum Freshness {
    FRESH,
    RISKY,
    SPOILED;

    public static Freshness getFreshnessByExpiredAt(LocalDate expiredAt) {

        if (expiredAt == null) {
            return Freshness.FRESH;
        }

        long diffDay = ChronoUnit.DAYS.between(LocalDate.now(), expiredAt);

        if (diffDay <= 0) {
            return Freshness.SPOILED;
        }

        if (diffDay < 7) {
            return Freshness.RISKY;
        }

        return Freshness.FRESH;
    }
}
