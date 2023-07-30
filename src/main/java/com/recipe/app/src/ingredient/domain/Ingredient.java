package com.recipe.app.src.ingredient.domain;

import com.recipe.app.common.entity.BaseEntity;
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
@Table(name = "Ingredient")
public class Ingredient extends BaseEntity implements Comparable<Ingredient> {
    @Id
    @Column(name = "ingredientId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ingredientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredientCategoryId", nullable = false)
    private IngredientCategory ingredientCategory;

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
    public int compareTo(@NotNull Ingredient ingredient) {
        return Integer.compare(this.getIngredientName().length(), ingredient.getIngredientName().length());
    }
}