package com.recipe.app.src.recipe.domain;

import com.google.common.base.Preconditions;
import com.recipe.app.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "RecipeIngredient")
public class RecipeIngredient extends BaseEntity {

    @Id
    @Column(name = "recipeIngredientId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recipeIngredientId;

    @Column(name = "recipeId", nullable = false)
    private Long recipeId;

    @Column(name = "ingredientName", nullable = false, length = 64)
    private String ingredientName;

    @Column(name = "ingredientIconId")
    private Long ingredientIconId;

    @Column(name = "quantity")
    private String quantity;

    @Column(name = "unit")
    private String unit;

    @Builder
    public RecipeIngredient(Long recipeIngredientId, Long recipeId, String ingredientName, Long ingredientIconId, String quantity, String unit) {

        Objects.requireNonNull(recipeId, "레시피 아이디를 입력해주세요.");
        Preconditions.checkArgument(StringUtils.hasText(ingredientName), "레시피 재료명을 입력해주세요.");

        this.recipeIngredientId = recipeIngredientId;
        this.recipeId = recipeId;
        this.ingredientName = ingredientName;
        this.ingredientIconId = ingredientIconId;
        this.quantity = quantity;
        this.unit = unit;
    }
}
