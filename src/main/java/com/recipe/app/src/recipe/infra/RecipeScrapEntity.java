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
@Table(name = "RecipeScrap")
public class RecipeScrapEntity {

    @Id
    @Column(name = "recipeScrapId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recipeScrapId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipeId")
    private RecipeEntity recipe;

    @CreatedDate
    @Column(name = "createdAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static RecipeScrapEntity create(User user, Recipe recipe) {
        RecipeScrapEntity recipeScrapEntity = new RecipeScrapEntity();
        recipeScrapEntity.user = UserEntity.fromModel(user);
        recipeScrapEntity.recipe = RecipeEntity.fromModel(recipe);
        return recipeScrapEntity;
    }
}
