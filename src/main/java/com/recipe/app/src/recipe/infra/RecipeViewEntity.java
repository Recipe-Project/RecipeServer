package com.recipe.app.src.recipe.infra;

import com.recipe.app.src.recipe.domain.Recipe;
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
@Table(name = "RecipeView")
public class RecipeViewEntity {

    @Id
    @Column(name = "recipeViewId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recipeViewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipeId")
    private RecipeEntity recipe;

    @CreatedDate
    @Column(name = "createdAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static RecipeViewEntity create(User user, Recipe recipe) {
        RecipeViewEntity recipeViewEntity = new RecipeViewEntity();
        recipeViewEntity.user = UserEntity.fromModel(user);
        recipeViewEntity.recipe = RecipeEntity.fromModel(recipe);
        return recipeViewEntity;
    }
}
