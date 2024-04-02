package com.recipe.app.src.recipe.domain.youtube;

import com.recipe.app.src.recipe.domain.youtube.YoutubeRecipe;
import com.recipe.app.src.user.domain.User;
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
@Table(name = "YoutubeView")
public class YoutubeView {

    @Id
    @Column(name = "youtubeViewId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long youtubeViewId;

    @Column(name = "userId", nullable = false)
    private Long userId;

    @Column(name = "youtubeRecipeId", nullable = false)
    private Long youtubeRecipeId;

    @CreatedDate
    @Column(name = "createdAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public YoutubeView(Long youtubeViewId, Long userId, Long youtubeRecipeId) {

        Objects.requireNonNull(userId, "유저 아이디를 입력해주세요.");
        Objects.requireNonNull(youtubeRecipeId, "유튜브 레시피 아이디를 입력해수제요.");

        this.youtubeViewId = youtubeViewId;
        this.userId = userId;
        this.youtubeRecipeId = youtubeRecipeId;
    }
}
