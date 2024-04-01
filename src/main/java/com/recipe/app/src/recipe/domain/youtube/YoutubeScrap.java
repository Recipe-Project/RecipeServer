package com.recipe.app.src.recipe.domain.youtube;

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
@Table(name = "YoutubeScrap")
public class YoutubeScrap {

    @Id
    @Column(name = "youtubeScrapId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long youtubeScrapId;

    @Column(name = "userId", nullable = false)
    private Long userId;

    @Column(name = "youtubeRecipeId", nullable = false)
    private Long youtubeRecipeId;

    @CreatedDate
    @Column(name = "createdAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public YoutubeScrap(Long youtubeScrapId, Long userId, Long youtubeRecipeId) {

        Objects.requireNonNull(userId, "유저 아이디를 입력해주세요.");
        Objects.requireNonNull(youtubeRecipeId, "유튜브 레시피 아이디를 입력해주세요.");

        this.youtubeScrapId = youtubeScrapId;
        this.userId = userId;
        this.youtubeRecipeId = youtubeRecipeId;
    }
}
