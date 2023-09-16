package com.recipe.app.src.recipe.infra.youtube;

import com.recipe.app.src.recipe.domain.YoutubeRecipe;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.user.infra.UserEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "YoutubeView")
public class YoutubeViewEntity {

    @Id
    @Column(name = "youtubeViewId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long youtubeViewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "youtubeRecipeId")
    private YoutubeRecipeEntity youtubeRecipe;

    @CreatedDate
    @Column(name = "createdAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static YoutubeViewEntity create(User user, YoutubeRecipe youtubeRecipe) {
        YoutubeViewEntity youtubeViewEntity = new YoutubeViewEntity();
        youtubeViewEntity.user = UserEntity.fromModel(user);
        youtubeViewEntity.youtubeRecipe = YoutubeRecipeEntity.fromModel(youtubeRecipe);
        return youtubeViewEntity;
    }
}
