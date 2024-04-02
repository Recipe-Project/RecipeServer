package com.recipe.app.src.recipe.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "RecipeScrap")
public class RecipeScrap {

    @Id
    @Column(name = "recipeScrapId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recipeScrapId;

    @Column(name = "userId", nullable = false)
    private Long userId;

    @Column(name = "recipeId", nullable = false)
    private Long recipeId;

    @CreatedDate
    @Column(name = "createdAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public RecipeScrap(Long recipeScrapId, Long userId, Long recipeId) {

        Objects.requireNonNull(userId, "유저 아이디를 입력해주세요.");
        Objects.requireNonNull(recipeId, "레시피 아이디를 입력해주세요.");

        this.recipeScrapId = recipeScrapId;
        this.userId = userId;
        this.recipeId = recipeId;
    }
}
