package com.recipe.app.src.recipe.infra;

import com.recipe.app.src.recipe.domain.Recipe;
import com.recipe.app.src.user.domain.User;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "RecipeView")
public class RecipeViewEntity {

    @Id
    @Column(name = "recipeViewId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recipeViewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipeId")
    private RecipeEntity recipe;

    @CreatedDate
    @Column(name = "createdAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static RecipeViewEntity create(User user, Recipe recipe) {
        RecipeViewEntity recipeViewEntity = new RecipeViewEntity();
        recipeViewEntity.user = User.fromModel(user);
        recipeViewEntity.recipe = RecipeEntity.fromModel(recipe);
        return recipeViewEntity;
    }
}
