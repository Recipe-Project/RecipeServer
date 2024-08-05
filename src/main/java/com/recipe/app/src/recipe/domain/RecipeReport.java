package com.recipe.app.src.recipe.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "Report")
public class RecipeReport {

    @Id
    @Column(name = "reportId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    @Column(name = "userId", nullable = false)
    private Long userId;

    @Column(name = "recipeId", nullable = false)
    private Long recipeId;

    @CreatedDate
    @Column(name = "createdAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public RecipeReport(Long reportId, Long userId, Long recipeId) {

        Objects.requireNonNull(userId, "유저 아이디를 입력해주세요.");
        Objects.requireNonNull(recipeId, "레시피 아이디를 입력해주세요.");

        this.reportId = reportId;
        this.userId = userId;
        this.recipeId = recipeId;
    }
}
