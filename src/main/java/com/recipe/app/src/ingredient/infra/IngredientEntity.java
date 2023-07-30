package com.recipe.app.src.ingredient.infra;

import com.recipe.app.common.entity.BaseEntity;
import com.recipe.app.src.ingredient.domain.Ingredient;
import com.recipe.app.src.user.domain.User;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "IngredientEntity")
public class IngredientEntity extends BaseEntity implements Comparable<IngredientEntity> {
    @Id
    @Column(name = "ingredientId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ingredientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredientCategoryId", nullable = false)
    private IngredientCategoryEntity ingredientCategoryEntity;

    @Column(name = "ingredientName", nullable = false, length = 64)
    private String ingredientName;

    @Column(name = "ingredientIconUrl", nullable = false)
    private String ingredientIconUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @Column(name = "defaultYn", length = 1)
    private String defaultYn;

    @Column(name = "hiddenYn", length = 1)
    private String hiddenYn;

    @Override
    public int compareTo(@NotNull IngredientEntity ingredientEntity) {
        return Integer.compare(this.getIngredientName().length(), ingredientEntity.getIngredientName().length());
    }

    public Ingredient toModel() {
        return Ingredient.builder()
                .ingredientId(ingredientId)
                .ingredientCategory(ingredientCategoryEntity.toModel())
                .ingredientName(ingredientName)
                .ingredientIconUrl(ingredientIconUrl)
                .createdAt(getCreatedAt())
                .updatedAt(getUpdatedAt())
                .user(user)
                .isDefault(defaultYn.equals("Y"))
                .isHidden(hiddenYn.equals("Y"))
                .build();
    }
}