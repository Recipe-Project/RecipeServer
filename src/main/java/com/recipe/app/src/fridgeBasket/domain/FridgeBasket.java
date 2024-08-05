package com.recipe.app.src.fridgeBasket.domain;

import com.recipe.app.src.common.entity.BaseEntity;
import com.recipe.app.src.fridge.domain.Freshness;
import com.recipe.app.src.fridge.domain.Fridge;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "FridgeBasket")
public class FridgeBasket extends BaseEntity {

    @Id
    @Column(name = "fridgeBasketId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fridgeBasketId;

    @Column(name = "userId", nullable = false)
    private Long userId;

    @Column(name = "ingredientId", nullable = false)
    private Long ingredientId;

    @Column(name = "expiredAt")
    private LocalDate expiredAt;

    @Column(name = "quantity", nullable = false)
    private float quantity = 1;

    @Column(name = "unit")
    private String unit;

    @Builder
    public FridgeBasket(Long fridgeBasketId, Long userId, Long ingredientId, LocalDate expiredAt, float quantity, String unit) {

        Objects.requireNonNull(userId, "유저 아이디를 입력해주세요.");
        Objects.requireNonNull(ingredientId, "재료 아이디를 입력해주세요.");

        this.fridgeBasketId = fridgeBasketId;
        this.userId = userId;
        this.ingredientId = ingredientId;
        this.expiredAt = expiredAt;
        this.quantity = quantity;
        this.unit = unit;
    }

    public void plusQuantity(float quantity) {

        this.quantity += quantity;
    }

    public void updateFridgeBasket(LocalDate expiredAt, float quantity, String unit) {

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
            return Freshness.SPOILED;
        }
        if (diffDay < 7) {
            return Freshness.RISKY;
        }
        return Freshness.FRESH;
    }

    public Fridge toFridge() {
        return Fridge.builder()
                .userId(userId)
                .ingredientId(ingredientId)
                .expiredAt(expiredAt)
                .quantity(quantity)
                .unit(unit)
                .build();
    }
}