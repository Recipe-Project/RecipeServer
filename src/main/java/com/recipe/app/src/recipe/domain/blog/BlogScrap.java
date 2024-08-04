package com.recipe.app.src.recipe.domain.blog;

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
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "BlogScrap")
public class BlogScrap {

    @Id
    @Column(name = "blogScrapId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long blogScrapId;

    @Column(name = "userId", nullable = false)
    private Long userId;

    @Column(name = "blogRecipeId", nullable = false)
    private Long blogRecipeId;

    @CreatedDate
    @Column(name = "createdAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public BlogScrap(Long blogScrapId, Long userId, Long blogRecipeId) {

        Objects.requireNonNull(userId, "유저 아이디를 입력해주세요.");
        Objects.requireNonNull(blogRecipeId, "블로그 레시피 아이디를 입력해주세요.");

        this.blogScrapId = blogScrapId;
        this.userId = userId;
        this.blogRecipeId = blogRecipeId;
    }
}
