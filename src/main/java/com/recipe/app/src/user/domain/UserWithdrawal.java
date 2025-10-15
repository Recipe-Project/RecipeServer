package com.recipe.app.src.user.domain;

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

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "UserWithdrawal")
public class UserWithdrawal {

    @Id
    @Column(name = "userWithdrawalId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userWithdrawalId;

    @Column(name = "userId", nullable = false)
    private Long userId;

    @Column(name = "withdrawalReason", length = 200)
    private String withdrawalReason;

    @Column(name = "createdAt", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder
    public UserWithdrawal(Long userId, String withdrawalReason) {
        this.userId = userId;
        this.withdrawalReason = withdrawalReason;
        this.createdAt = LocalDateTime.now();
    }
}
