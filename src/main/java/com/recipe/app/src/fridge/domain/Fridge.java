package com.recipe.app.src.fridge.domain;

import com.recipe.app.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "Fridge")
public class Fridge extends BaseEntity {

    @Id
    @Column(name = "fridgeId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fridgeId;

    @Column(name = "userId", nullable = false)
    private Long userId;

    @Column(name = "ingredientId", nullable = false)
    private Long ingredientId;

    @Column(name = "expiredAt")
    private LocalDateTime expiredAt;

    @Column(name = "quantity", nullable = false)
    private float quantity;

    @Column(name = "unit")
    private String unit;

    @Builder
    public Fridge(Long fridgeId, Long userId, Long ingredientId, LocalDateTime expiredAt, float quantity, String unit) {

        Objects.requireNonNull(userId, "유저 아이디를 입력해주세요.");
        Objects.requireNonNull(ingredientId, "재료 아이디를 입력해주세요");

        this.fridgeId = fridgeId;
        this.userId = userId;
        this.ingredientId = ingredientId;
        this.expiredAt = expiredAt;
        this.quantity = quantity;
        this.unit = unit;
    }

    public Freshness getFreshness() {

        if (this.expiredAt == null) {
            return Freshness.FRESH;
        }
        long diffDay = ChronoUnit.DAYS.between(LocalDate.now(), expiredAt);
        if (diffDay <= 0) {
            return Freshness.DISPOSAL;
        }
        if (diffDay < 7) {
            return Freshness.DANGER;
        }
        return Freshness.FRESH;
    }

    public void updateFridge(LocalDateTime expiredAt, float quantity, String unit) {

        this.expiredAt = expiredAt;
        this.quantity = quantity;
        this.unit = unit;
    }

    public void plusQuantity(float quantity) {

        this.quantity += quantity;
    }
}