package com.recipe.app.src.recipe.domain;

import com.recipe.app.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Column(name = "capacity", nullable = false)
    private Long ingredientId;

    @Column(name = "capacity")
    private String capacity;

    @Builder
    public RecipeIngredient(Long recipeIngredientId, Long recipeId, Long ingredientId, String capacity) {

        Objects.requireNonNull(recipeId, "레시피 아이디를 입력해주세요.");
        Objects.requireNonNull(ingredientId, "재료 아이디를 입력해주세요.");

        this.recipeIngredientId = recipeIngredientId;
        this.recipeId = recipeId;
        this.ingredientId = ingredientId;
        this.capacity = capacity;
    }
}
